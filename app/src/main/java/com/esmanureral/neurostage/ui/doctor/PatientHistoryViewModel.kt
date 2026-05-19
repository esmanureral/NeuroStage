package com.esmanureral.neurostage.ui.doctor

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.esmanureral.neurostage.R
import com.esmanureral.neurostage.auth.AuthRepository
import com.esmanureral.neurostage.auth.AuthStatus
import com.esmanureral.neurostage.data.doctor.DoctorSessionCache
import com.esmanureral.neurostage.navigation.RouteArgs
import com.esmanureral.neurostage.patients.Patient
import com.esmanureral.neurostage.patients.PatientRepository
import com.esmanureral.neurostage.scans.ScanRecord
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

data class PatientHistoryUiState(
    val patient: Patient? = null,
    val scans: List<ScanRecord> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val scanError: String? = null,
    val compareMode: Boolean = false,
    val selectedForCompare: Set<String> = emptySet(),
    val selectionMode: Boolean = false,
    val selectedScanIds: Set<String> = emptySet(),
    val isDeleting: Boolean = false,
    val deleteError: String? = null,
)

@HiltViewModel
class PatientHistoryViewModel @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val auth: AuthRepository,
    private val patientRepo: PatientRepository,
    private val scanRepo: ScanRepository,
    private val sessionCache: DoctorSessionCache,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val patientId: String = checkNotNull(savedStateHandle[RouteArgs.PATIENT_ID])

    private val _ui = MutableStateFlow(PatientHistoryUiState(isLoading = true))
    val ui: StateFlow<PatientHistoryUiState> = _ui.asStateFlow()

    init {
        warmStartFromCache()
    }

    private fun warmStartFromCache() {
        val uid = (auth.status.value as? AuthStatus.SignedIn)?.user?.uid ?: return
        sessionCache.historyFor(uid, patientId)?.let { cached ->
            _ui.value = PatientHistoryUiState(
                patient = cached.patient,
                scans = cached.scans,
                isLoading = false,
            )
            return
        }
        sessionCache.patientsFor(uid)
            ?.firstOrNull { it.patient.id == patientId }
            ?.let { summary ->
                _ui.value = PatientHistoryUiState(
                    patient = summary.patient,
                    scans = emptyList(),
                    isLoading = true,
                )
            }
    }

    fun load() {
        val uid = (auth.status.value as? AuthStatus.SignedIn)?.user?.uid
        if (uid == null) {
            _ui.value = _ui.value.copy(
                isLoading = false,
                error = context.getString(R.string.error_session_required),
            )
            return
        }

        val showBlockingLoader = _ui.value.patient == null
        if (showBlockingLoader) {
            _ui.value = _ui.value.copy(isLoading = true, error = null, scanError = null)
        }

        viewModelScope.launch {
            val (patientResult, scansResult) = coroutineScope {
                val patientDeferred = async { patientRepo.get(uid, patientId) }
                val scansDeferred = async { scanRepo.refreshPatient(uid, patientId) }
                patientDeferred.await() to scansDeferred.await()
            }

            val patient = patientResult.getOrNull()
            val scans = scansResult.getOrElse { emptyList() }

            if (patient != null) {
                sessionCache.saveHistory(uid, patientId, patient, scans)
            }

            _ui.value = _ui.value.copy(
                isLoading = false,
                patient = patient,
                scans = scans,
                error = patientResult.exceptionOrNull()?.message,
                scanError = scansResult.exceptionOrNull()?.message,
            )
        }
    }

    fun toggleCompareMode() {
        _ui.value = _ui.value.copy(
            compareMode = !_ui.value.compareMode,
            selectedForCompare = emptySet(),
            selectionMode = false,
            selectedScanIds = emptySet(),
            deleteError = null,
        )
    }

    fun setSelectionMode(enabled: Boolean) {
        _ui.value = _ui.value.copy(
            selectionMode = enabled,
            selectedScanIds = if (enabled) _ui.value.selectedScanIds else emptySet(),
            compareMode = false,
            selectedForCompare = emptySet(),
            deleteError = null,
        )
    }

    fun toggleCompareSelection(scanId: String) {
        val current = _ui.value.selectedForCompare
        val updated = if (scanId in current) current - scanId else {
            if (current.size < 2) current + scanId else current
        }
        _ui.value = _ui.value.copy(selectedForCompare = updated)
    }

    fun toggleScanSelection(scanId: String) {
        val current = _ui.value.selectedScanIds
        val updated = if (scanId in current) current - scanId else current + scanId
        _ui.value = _ui.value.copy(selectedScanIds = updated)
    }

    fun selectAllScans() {
        val allIds = _ui.value.scans.map { it.id }.toSet()
        _ui.value = _ui.value.copy(selectedScanIds = allIds)
    }

    fun deleteSelectedScans() {
        val uid = (auth.status.value as? AuthStatus.SignedIn)?.user?.uid
        if (uid == null) {
            _ui.value = _ui.value.copy(
                deleteError = context.getString(R.string.error_session_required),
            )
            return
        }
        val ids = _ui.value.selectedScanIds
        if (ids.isEmpty()) return

        viewModelScope.launch {
            _ui.value = _ui.value.copy(isDeleting = true, deleteError = null)
            var failed: String? = null
            ids.forEach { scanId ->
                val result = scanRepo.delete(uid, patientId, scanId)
                if (result.isFailure) {
                    failed = result.exceptionOrNull()?.message
                }
            }
            val refreshed = scanRepo.refreshPatient(uid, patientId).getOrElse { emptyList() }
            _ui.value.patient?.let { patient ->
                sessionCache.saveHistory(uid, patientId, patient, refreshed)
            }
            val base = context.getString(R.string.patient_list_delete_failed)
            _ui.value = _ui.value.copy(
                scans = refreshed,
                isDeleting = false,
                selectedScanIds = emptySet(),
                selectionMode = false,
                deleteError = failed?.let { msg ->
                    if (msg.isBlank()) base else "$base $msg"
                },
            )
        }
    }

    fun getSelectedScans(): Pair<ScanRecord, ScanRecord>? {
        val selected = _ui.value.selectedForCompare.toList()
        if (selected.size < 2) return null
        val a = _ui.value.scans.firstOrNull { it.id == selected[0] } ?: return null
        val b = _ui.value.scans.firstOrNull { it.id == selected[1] } ?: return null
        return Pair(a, b)
    }
}
