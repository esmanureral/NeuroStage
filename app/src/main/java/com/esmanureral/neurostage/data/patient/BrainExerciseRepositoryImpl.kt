package com.esmanureral.neurostage.data.patient

import com.esmanureral.neurostage.data.AppPreferences
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BrainExerciseRepositoryImpl @Inject constructor(
    private val prefs: AppPreferences,
) : BrainExerciseRepository {

    override val patientStage: StateFlow<Int?> = prefs.patientStage
    override val puzzleProgress: StateFlow<Int> = prefs.mildPuzzleProgress
    override val mriModeratePuzzleProgress: StateFlow<Int> = prefs.mriModeratePuzzleProgress
    override val memoryMatchLevel: StateFlow<Int> = prefs.memoryMatchLevel
    override val memoryMatchAllComplete: StateFlow<Boolean> = prefs.memoryMatchAllComplete

    override fun advancePuzzleProgress() = prefs.advanceMildPuzzle()

    override fun advanceMriModeratePuzzleProgress() = prefs.advanceMriModeratePuzzle()

    override fun setMemoryMatchLevel(levelIndex: Int) = prefs.setMemoryMatchLevel(levelIndex)

    override fun setMemoryMatchAllComplete() = prefs.setMemoryMatchAllComplete()

    override fun resetMemoryMatchProgress() = prefs.resetMemoryMatchProgress()

    override fun clearSession() = prefs.clearWorld()
}