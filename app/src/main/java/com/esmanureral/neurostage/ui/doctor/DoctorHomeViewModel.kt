package com.esmanureral.neurostage.ui.doctor

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.esmanureral.neurostage.R
import com.esmanureral.neurostage.auth.AuthRepository
import com.esmanureral.neurostage.auth.AuthStatus
import com.esmanureral.neurostage.auth.LocalAuthSnapshot
import com.esmanureral.neurostage.data.AppPreferences
import com.esmanureral.neurostage.data.doctor.DoctorSessionCache
import com.esmanureral.neurostage.patients.Patient
import com.esmanureral.neurostage.patients.PatientRepository
import com.esmanureral.neurostage.profile.UserProfile
import com.esmanureral.neurostage.profile.UserProfileRepository
import com.esmanureral.neurostage.scans.ScanRecord
import com.esmanureral.neurostage.scans.ScanRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DoctorHeaderState(
    val displayName: String? = null,
    val email: String? = null,
    val error: String? = null,
)

data class DoctorDashboardState(
    val patientCount: Int = 0,
    val scanCount: Int = 0,
    val scansThisWeek: Int = 0,
    val isLoading: Boolean = false,
)

@HiltViewModel
class DoctorHomeViewModel @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val auth: AuthRepository,
    private val profiles: UserProfileRepository,
    private val patientsRepo: PatientRepository,
    private val scanRepo: ScanRepository,
    private val prefs: AppPreferences,
    private val sessionCache: DoctorSessionCache,
) : ViewModel() {
    val authStatus: StateFlow<AuthStatus> = auth.status

    private val _header = MutableStateFlow(DoctorHeaderState())
    val header: StateFlow<DoctorHeaderState> = _header.asStateFlow()

    private val _dashboard = MutableStateFlow(DoctorDashboardState())
    val dashboard: StateFlow<DoctorDashboardState> = _dashboard.asStateFlow()

    init {
        warmStartFromLocal()
        viewModelScope.launch {
            auth.status.collect { status ->
                if (status is AuthStatus.SignedIn) {
                    warmStartForDoctor(status.user.uid)
                    val hasCache = prefs.readDoctorDashboardCache(status.user.uid) != null
                    if (!hasCache) {
                        _dashboard.value = _dashboard.value.copy(isLoading = true)
                    }
                    refreshHome(status.user.uid)
                    _dashboard.value = _dashboard.value.copy(isLoading = false)
                }
            }
        }
    }

    fun onHomeVisible() {
        val uid = doctorUid() ?: return
        val hasCachedData = prefs.readDoctorDashboardCache(uid) != null ||
            prefs.readCachedDoctorDisplayName(uid) != null
        warmStartForDoctor(uid)
        if (!hasCachedData) {
            _dashboard.value = _dashboard.value.copy(isLoading = true)
        }
        viewModelScope.launch {
            refreshHome(uid)
            _dashboard.value = _dashboard.value.copy(isLoading = false)
        }
    }

    private fun warmStartFromLocal() {
        auth.localAuthSnapshot()?.let { snapshot ->
            applyHeaderFromSnapshot(snapshot)
            warmStartForDoctor(snapshot.uid)
        }
    }

    private fun warmStartForDoctor(doctorUid: String) {
        prefs.readCachedDoctorDisplayName(doctorUid)?.let { cachedName ->
            _header.value = _header.value.copy(displayName = cachedName)
        }
        prefs.readDoctorDashboardCache(doctorUid)?.let { cache ->
            _dashboard.value = cache.toState()
        }
        val memoryScans = scanRepo.doctorScans.value
        if (memoryScans.isNotEmpty()) {
            _dashboard.value = _dashboard.value.mergeScanStats(memoryScans)
        }
    }

    private suspend fun refreshHome(doctorUid: String) {
        applyHeaderFromSnapshot(auth.localAuthSnapshot())

        val (patients, scans) = coroutineScope {
            val patientsDeferred = async { patientsRepo.list(doctorUid) }
            val scansDeferred = async { scanRepo.refreshDoctor(doctorUid) }
            val patientsResult = patientsDeferred.await()
            val patientList = patientsResult.getOrElse { emptyList() }
            _dashboard.value = _dashboard.value.copy(patientCount = patientList.size)

            val scansResult = scansDeferred.await()
            val scanList = when {
                scansResult.isSuccess -> scanRepo.doctorScans.value
                patientList.isEmpty() -> emptyList()
                else -> loadScansPerPatient(doctorUid, patientList)
            }
            patientList to scanList
        }

        val dashboardState = scans.toDashboardState(patients.size)
        _dashboard.value = dashboardState.copy(isLoading = false)
        prefs.writeDoctorDashboardCache(
            doctorUid = doctorUid,
            patientCount = dashboardState.patientCount,
            scanCount = dashboardState.scanCount,
            scansThisWeek = dashboardState.scansThisWeek,
        )

        val improving = context.getString(R.string.patient_trend_improving)
        val stable = context.getString(R.string.patient_trend_stable)
        val patientSummaries = DoctorPatientEnricher.summaries(
            patients = patients,
            scans = scans,
            improvingLabel = improving,
            stableLabel = stable,
        )
        sessionCache.savePatients(doctorUid, patientSummaries)

        refreshProfileName(doctorUid)
    }

    private suspend fun refreshProfileName(doctorUid: String) {
        val local = auth.localAuthSnapshot()
        val firebaseDisplay = local.blankToNullDisplayName()

        profiles.get(doctorUid)
            .onSuccess { profile ->
                val name = mergedDisplayName(profile, firebaseDisplay)
                if (!name.isNullOrBlank()) {
                    _header.value = DoctorHeaderState(
                        displayName = name,
                        email = profile.email?.takeIf { it.isNotBlank() } ?: local?.email,
                    )
                    prefs.writeCachedDoctorDisplayName(doctorUid, name)
                }
            }
            .onFailure {
                if (_header.value.displayName.isNullOrBlank() && firebaseDisplay != null) {
                    _header.value = DoctorHeaderState(
                        displayName = firebaseDisplay,
                        email = local?.email,
                        error = context.getString(R.string.doctor_home_profile_error),
                    )
                }
            }
    }

    private suspend fun loadScansPerPatient(
        doctorUid: String,
        patients: List<Patient>,
    ): List<ScanRecord> = coroutineScope {
        patients.map { patient ->
            async {
                scanRepo.refreshPatient(doctorUid, patient.id).getOrElse { emptyList() }
            }
        }.awaitAll().flatten()
    }.sortedByDescending { it.timestampMs }

    private fun applyHeaderFromSnapshot(snapshot: LocalAuthSnapshot?) {
        snapshot ?: return
        val display = snapshot.displayName?.takeIf { it.isNotBlank() }
        if (display != null) {
            _header.value = DoctorHeaderState(
                displayName = display,
                email = snapshot.email,
            )
        }
    }

    private fun mergedDisplayName(
        profile: UserProfile,
        firebaseDisplayFallback: String?,
    ): String? {
        val combined = listOf(profile.firstName, profile.lastName)
            .map { it.trim() }
            .filter { it.isNotBlank() }
            .joinToString(" ")
            .takeIf { it.isNotBlank() }
        return combined ?: firebaseDisplayFallback?.takeIf { it.isNotBlank() }
    }

    fun signOut() {
        viewModelScope.launch {
            auth.signOut()
            _header.value = DoctorHeaderState()
            _dashboard.value = DoctorDashboardState()
            prefs.clearDoctorHomeCache()
            sessionCache.clear()
        }
    }

    private fun doctorUid(): String? =
        (auth.status.value as? AuthStatus.SignedIn)?.user?.uid

    private fun AppPreferences.DoctorDashboardCache.toState() = DoctorDashboardState(
        patientCount = patientCount,
        scanCount = scanCount,
        scansThisWeek = scansThisWeek,
        isLoading = false,
    )

    private fun List<ScanRecord>.toDashboardState(patientCount: Int): DoctorDashboardState {
        val weekAgo = System.currentTimeMillis() - WEEK_MS
        return DoctorDashboardState(
            patientCount = patientCount,
            scanCount = size,
            scansThisWeek = count { it.timestampMs >= weekAgo },
            isLoading = false,
        )
    }

    private fun DoctorDashboardState.mergeScanStats(scans: List<ScanRecord>): DoctorDashboardState {
        val weekAgo = System.currentTimeMillis() - WEEK_MS
        return copy(
            scanCount = scans.size,
            scansThisWeek = scans.count { it.timestampMs >= weekAgo },
        )
    }

    private companion object {
        const val WEEK_MS = 7L * 24 * 60 * 60 * 1000
    }
}

private fun LocalAuthSnapshot?.blankToNullDisplayName(): String? =
    this?.displayName?.trim()?.takeIf { it.isNotBlank() }
