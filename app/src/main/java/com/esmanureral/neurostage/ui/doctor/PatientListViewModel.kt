package com.esmanureral.neurostage.ui.doctor

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.esmanureral.neurostage.R
import com.esmanureral.neurostage.auth.AuthRepository
import com.esmanureral.neurostage.auth.AuthStatus
import com.esmanureral.neurostage.data.doctor.DoctorSessionCache
import com.esmanureral.neurostage.patients.Patient
import com.esmanureral.neurostage.patients.PatientRepository
import com.esmanureral.neurostage.scans.ScanRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PatientSummary(
    val patient: Patient,
    val lastScanLabel: String?,
    val lastStatus: String?,
)

data class PatientListUiState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val patients: List<PatientSummary> = emptyList(),
    val error: String? = null,
    val selectionMode: Boolean = false,
    val selectedPatientIds: Set<String> = emptySet(),
    val isDeleting: Boolean = false,
    val deleteError: String? = null,
)

@HiltViewModel
class PatientListViewModel @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val auth: AuthRepository,
    private val patientsRepo: PatientRepository,
    private val scanRepo: ScanRepository,
    private val sessionCache: DoctorSessionCache,
) : ViewModel() {
    private val _ui = MutableStateFlow(PatientListUiState())
    val ui: StateFlow<PatientListUiState> = _ui.asStateFlow()

    init {
        warmStartFromCache()
    }

    private fun warmStartFromCache() {
        val uid = doctorUid() ?: return
        sessionCache.patientsFor(uid)?.let { cached ->
            _ui.value = PatientListUiState(patients = cached, isLoading = false)
        }
    }

    fun refresh() {
        val uid = doctorUid() ?: run {
            _ui.value = PatientListUiState(error = context.getString(R.string.error_session_required))
            return
        }
        val improving = context.getString(R.string.patient_trend_improving)
        val stable = context.getString(R.string.patient_trend_stable)

        val cached = sessionCache.patientsFor(uid)
        if (cached != null) {
            _ui.value = _ui.value.copy(
                patients = cached,
                isLoading = false,
                isRefreshing = true,
                error = null,
                deleteError = null,
            )
        } else {
            _ui.value = _ui.value.copy(
                isLoading = true,
                isRefreshing = false,
                error = null,
                deleteError = null,
            )
        }

        viewModelScope.launch {
            val patientsResult = patientsRepo.list(uid)
            val patients = patientsResult.getOrElse { emptyList() }

            val partialSummaries = patients.map { patient ->
                PatientSummary(patient = patient, lastScanLabel = null, lastStatus = null)
            }
            _ui.value = _ui.value.copy(
                patients = partialSummaries,
                isLoading = false,
            )

            val memoryScans = scanRepo.doctorScans.value
            if (memoryScans.isNotEmpty()) {
                val quickEnriched = DoctorPatientEnricher.summaries(
                    patients = patients,
                    scans = memoryScans,
                    improvingLabel = improving,
                    stableLabel = stable,
                )
                _ui.value = _ui.value.copy(patients = quickEnriched)
            }

            val scansResult = scanRepo.refreshDoctor(uid)
            val scans = if (scansResult.isSuccess) {
                scanRepo.doctorScans.value
            } else {
                memoryScans
            }

            val enriched = DoctorPatientEnricher.summaries(
                patients = patients,
                scans = scans,
                improvingLabel = improving,
                stableLabel = stable,
            )

            val failBase = context.getString(R.string.patient_list_load_failed)
            val exMsg = patientsResult.exceptionOrNull()?.message
            _ui.value = _ui.value.copy(
                isLoading = false,
                isRefreshing = false,
                patients = enriched,
                error = patientsResult.exceptionOrNull()?.let {
                    if (exMsg.isNullOrBlank()) failBase else "$failBase $exMsg"
                },
            )
            sessionCache.savePatients(uid, enriched)
        }
    }

    fun setSelectionMode(enabled: Boolean) {
        _ui.value = _ui.value.copy(
            selectionMode = enabled,
            selectedPatientIds = if (enabled) _ui.value.selectedPatientIds else emptySet(),
            deleteError = null,
        )
    }

    fun togglePatientSelection(patientId: String) {
        val current = _ui.value.selectedPatientIds
        val updated = if (patientId in current) current - patientId else current + patientId
        _ui.value = _ui.value.copy(selectedPatientIds = updated)
    }

    fun selectAllPatients() {
        val allIds = _ui.value.patients.map { it.patient.id }.toSet()
        _ui.value = _ui.value.copy(selectedPatientIds = allIds)
    }

    fun deleteSelected() {
        val uid = doctorUid() ?: run {
            _ui.value = _ui.value.copy(deleteError = context.getString(R.string.error_session_required))
            return
        }
        val ids = _ui.value.selectedPatientIds.toList()
        if (ids.isEmpty()) return

        viewModelScope.launch {
            _ui.value = _ui.value.copy(isDeleting = true, deleteError = null)
            val result = patientsRepo.deleteMany(uid, ids)
            result.onSuccess {
                scanRepo.refreshDoctor(uid)
                _ui.value = _ui.value.copy(
                    isDeleting = false,
                    selectionMode = false,
                    selectedPatientIds = emptySet(),
                )
                refresh()
            }.onFailure {
                val base = context.getString(R.string.patient_list_delete_failed)
                val msg = it.message
                _ui.value = _ui.value.copy(
                    isDeleting = false,
                    deleteError = if (msg.isNullOrBlank()) base else "$base $msg",
                )
            }
        }
    }

    private fun doctorUid(): String? =
        (auth.status.value as? AuthStatus.SignedIn)?.user?.uid
}
