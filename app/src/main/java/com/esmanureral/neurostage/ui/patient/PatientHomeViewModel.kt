package com.esmanureral.neurostage.ui.patient

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.esmanureral.neurostage.data.patient.BrainExerciseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class PatientHomeViewModel @Inject constructor(
    private val repository: BrainExerciseRepository,
) : ViewModel() {

    val uiState: StateFlow<PatientHomeUiState> = combine(
        repository.patientStage,
        repository.puzzleProgress,
        PatientHomeUiState::from,
    ).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS),
        initialValue = PatientHomeUiState(),
    )

    fun clearSession() = repository.clearSession()

    private companion object {
        const val STOP_TIMEOUT_MS = 5_000L
    }
}
