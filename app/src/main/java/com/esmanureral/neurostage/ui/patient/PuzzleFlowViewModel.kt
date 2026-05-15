package com.esmanureral.neurostage.ui.patient

import androidx.lifecycle.ViewModel
import com.esmanureral.neurostage.data.patient.BrainExerciseRepository
import com.esmanureral.neurostage.domain.patient.MildPuzzleCatalog
import com.esmanureral.neurostage.domain.patient.MildPuzzleStep
import com.esmanureral.neurostage.domain.patient.PatientStage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

data class PuzzleSessionConfig(
    val step: MildPuzzleStep,
    val rows: Int,
    val cols: Int,
    val usesCatalogPath: Boolean,
    val hasNextStep: Boolean,
)

@HiltViewModel
class PuzzleFlowViewModel @Inject constructor(
    private val repository: BrainExerciseRepository,
) : ViewModel() {

    val puzzleProgress: StateFlow<Int> = repository.puzzleProgress
    val patientStage: StateFlow<Int?> = repository.patientStage

    fun sessionConfig(stageIndex: Int?, progress: Int): PuzzleSessionConfig {
        val usesCatalog = PatientStage.isBrainExerciseEligible(stageIndex)
        val step = if (usesCatalog) {
            MildPuzzleCatalog.stepForProgress(progress)
        } else {
            MildPuzzleStep.TEA
        }
        val fallbackGrid = PatientStage.fallbackPuzzleGridSize(stageIndex)
        return PuzzleSessionConfig(
            step = step,
            rows = if (usesCatalog) step.rows else fallbackGrid,
            cols = if (usesCatalog) step.cols else fallbackGrid,
            usesCatalogPath = usesCatalog,
            hasNextStep = usesCatalog && MildPuzzleCatalog.hasNextStep(step),
        )
    }

    fun advanceToNextPuzzle() = repository.advancePuzzleProgress()
}
