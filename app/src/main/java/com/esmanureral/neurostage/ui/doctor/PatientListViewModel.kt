package com.esmanureral.neurostage.ui.doctor

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.esmanureral.neurostage.R
import com.esmanureral.neurostage.auth.AuthRepository
import com.esmanureral.neurostage.auth.AuthStatus
import com.esmanureral.neurostage.patients.Patient
import com.esmanureral.neurostage.patients.PatientRepository
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

data class PatientSummary(
    val patient: Patient,
    val lastScanLabel: String?,
    val lastStatus: String?,
)

data class PatientListUiState(
    val isLoading: Boolean = false,
    val patients: List<PatientSummary> = emptyList(),
    val error: String? = null,
)

@HiltViewModel
class PatientListViewModel @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val auth: AuthRepository,
    private val patientsRepo: PatientRepository,
    private val scanRepo: ScanRepository,
) : ViewModel() {
    private val _ui = MutableStateFlow(PatientListUiState())
    val ui: StateFlow<PatientListUiState> = _ui.asStateFlow()

    fun refresh() {
        val uid = (auth.status.value as? AuthStatus.SignedIn)?.user?.uid
        if (uid == null) {
            _ui.value = PatientListUiState(error = context.getString(R.string.error_session_required))
            return
        }
        val improving = context.getString(R.string.patient_trend_improving)
        val stable = context.getString(R.string.patient_trend_stable)
        viewModelScope.launch {
            _ui.value = _ui.value.copy(isLoading = true, error = null)
            val res = patientsRepo.list(uid)
            val summaries = coroutineScope {
                res.getOrNull().orEmpty().map { patient ->
                    async {
                        val patientScans = scanRepo.refreshPatient(uid, patient.id)
                            .getOrElse { emptyList() }
                            .sortedByDescending { it.timestampMs }
                        val lastLabel = patientScans.firstOrNull()?.label
                        val lastStatus = when {
                            patientScans.size >= 2 ->
                                if (patientScans[0].stageIndex > patientScans[1].stageIndex) improving else stable
                            patientScans.size == 1 -> stable
                            else -> null
                        }
                        PatientSummary(patient = patient, lastScanLabel = lastLabel, lastStatus = lastStatus)
                    }
                }.awaitAll()
            }
            val failBase = context.getString(R.string.patient_list_load_failed)
            val exMsg = res.exceptionOrNull()?.message
            _ui.value = PatientListUiState(
                isLoading = false,
                patients = summaries,
                error = res.exceptionOrNull()?.let {
                    if (exMsg.isNullOrBlank()) failBase else "$failBase $exMsg"
                },
            )
        }
    }
}
