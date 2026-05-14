package com.esmanureral.neurostage.ui.doctor

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.esmanureral.neurostage.R
import com.esmanureral.neurostage.auth.AuthRepository
import com.esmanureral.neurostage.auth.AuthStatus
import com.esmanureral.neurostage.navigation.RouteArgs
import com.esmanureral.neurostage.patients.Patient
import com.esmanureral.neurostage.patients.PatientRepository
import com.esmanureral.neurostage.scans.ScanRecord
import com.esmanureral.neurostage.scans.ScanRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
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
)

@HiltViewModel
class PatientHistoryViewModel @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val auth: AuthRepository,
    private val patientRepo: PatientRepository,
    private val scanRepo: ScanRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val patientId: String = checkNotNull(savedStateHandle[RouteArgs.PATIENT_ID])

    private val _ui = MutableStateFlow(PatientHistoryUiState())
    val ui: StateFlow<PatientHistoryUiState> = _ui.asStateFlow()

    fun load() {
        val uid = (auth.status.value as? AuthStatus.SignedIn)?.user?.uid
        if (uid == null) {
            _ui.value = _ui.value.copy(
                isLoading = false,
                error = context.getString(R.string.error_session_required),
            )
            return
        }
        viewModelScope.launch {
            _ui.value = _ui.value.copy(isLoading = true, error = null, scanError = null)
            val patientResult = patientRepo.get(uid, patientId)
            val scansResult = scanRepo.refreshPatient(uid, patientId)
            _ui.value = _ui.value.copy(
                isLoading = false,
                patient = patientResult.getOrNull(),
                scans = scansResult.getOrElse { emptyList() },
                error = patientResult.exceptionOrNull()?.message,
                scanError = scansResult.exceptionOrNull()?.message,
            )
        }
    }

    fun toggleCompareMode() {
        _ui.value = _ui.value.copy(compareMode = !_ui.value.compareMode, selectedForCompare = emptySet())
    }

    fun toggleScanSelection(scanId: String) {
        val current = _ui.value.selectedForCompare
        val updated = if (scanId in current) current - scanId else {
            if (current.size < 2) current + scanId else current
        }
        _ui.value = _ui.value.copy(selectedForCompare = updated)
    }

    fun getSelectedScans(): Pair<ScanRecord, ScanRecord>? {
        val selected = _ui.value.selectedForCompare.toList()
        if (selected.size < 2) return null
        val a = _ui.value.scans.firstOrNull { it.id == selected[0] } ?: return null
        val b = _ui.value.scans.firstOrNull { it.id == selected[1] } ?: return null
        return Pair(a, b)
    }
}
