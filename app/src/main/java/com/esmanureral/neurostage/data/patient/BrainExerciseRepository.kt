package com.esmanureral.neurostage.data.patient

import kotlinx.coroutines.flow.StateFlow

interface BrainExerciseRepository {
    val patientStage: StateFlow<Int?>
    val puzzleProgress: StateFlow<Int>
    val mriModeratePuzzleProgress: StateFlow<Int>
    val memoryMatchLevel: StateFlow<Int>
    val memoryMatchAllComplete: StateFlow<Boolean>

    fun advancePuzzleProgress()
    fun advanceMriModeratePuzzleProgress()
    fun setMemoryMatchLevel(levelIndex: Int)
    fun setMemoryMatchAllComplete()
    fun resetMemoryMatchProgress()
    fun clearSession()
}
