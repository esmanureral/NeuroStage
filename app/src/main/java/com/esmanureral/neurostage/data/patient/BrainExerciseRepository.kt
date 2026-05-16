package com.esmanureral.neurostage.data.patient

import kotlinx.coroutines.flow.StateFlow

interface BrainExerciseRepository {
    val patientStage: StateFlow<Int?>
    val puzzleProgress: StateFlow<Int>
    val mriModeratePuzzleProgress: StateFlow<Int>

    fun advancePuzzleProgress()
    fun advanceMriModeratePuzzleProgress()
    fun clearSession()
}
