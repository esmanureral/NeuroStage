package com.esmanureral.neurostage.domain.patient

object PatientStage {
    const val MILD_DEMENTIA = 0
    const val MODERATE_DEMENTIA = 1
    const val HEALTHY = 2
    const val VERY_MILD_DEMENTIA = 3

    fun isBrainExerciseEligible(stageIndex: Int?): Boolean =
        stageIndex == MILD_DEMENTIA || stageIndex == VERY_MILD_DEMENTIA

    fun canAccessPatientExerciseHub(stageIndex: Int?): Boolean =
        isBrainExerciseEligible(stageIndex) || stageIndex == MODERATE_DEMENTIA

    fun memoryItemCount(stageIndex: Int?): Int = when (stageIndex) {
        MODERATE_DEMENTIA -> 2
        MILD_DEMENTIA, VERY_MILD_DEMENTIA -> 3
        else -> 4
    }

    fun fallbackPuzzleGridSize(stageIndex: Int?): Int = when (stageIndex) {
        MILD_DEMENTIA, VERY_MILD_DEMENTIA -> 2
        MODERATE_DEMENTIA -> 3
        else -> 3
    }
}