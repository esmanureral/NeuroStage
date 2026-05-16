package com.esmanureral.neurostage.ui.patient

import androidx.lifecycle.ViewModel
import com.esmanureral.neurostage.data.patient.BrainExerciseRepository
import com.esmanureral.neurostage.domain.patient.PatientStage
import com.esmanureral.neurostage.ui.patient.puzzle.core.PuzzleProgressTrack
import com.esmanureral.neurostage.ui.patient.puzzle.core.PuzzleSessionConfig
import com.esmanureral.neurostage.ui.patient.puzzle.mild.buildFallbackGridPuzzleSession
import com.esmanureral.neurostage.ui.patient.puzzle.mild.buildMildBrainExercisePuzzleSession
import com.esmanureral.neurostage.ui.patient.puzzle.moderate.buildModerateDementiaPuzzleSession
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class PuzzleFlowViewModel @Inject constructor(
    private val repository: BrainExerciseRepository,
) : ViewModel() {

    fun puzzleProgressFor(track: PuzzleProgressTrack): StateFlow<Int> = when (track) {
        PuzzleProgressTrack.MildHomeCatalog -> repository.puzzleProgress
        PuzzleProgressTrack.MriModerateCatalog -> repository.mriModeratePuzzleProgress
    }

    fun sessionConfig(
        stageIndex: Int?,
        progress: Int,
        track: PuzzleProgressTrack
    ): PuzzleSessionConfig {
        return when (track) {
            PuzzleProgressTrack.MriModerateCatalog -> buildModerateDementiaPuzzleSession(progress)
            PuzzleProgressTrack.MildHomeCatalog -> when {
                PatientStage.isBrainExerciseEligible(stageIndex) ->
                    buildMildBrainExercisePuzzleSession(progress)

                else -> buildFallbackGridPuzzleSession(stageIndex)
            }
        }
    }

    fun advanceToNextPuzzle(track: PuzzleProgressTrack) = when (track) {
        PuzzleProgressTrack.MildHomeCatalog -> repository.advancePuzzleProgress()
        PuzzleProgressTrack.MriModerateCatalog -> repository.advanceMriModeratePuzzleProgress()
    }
}
