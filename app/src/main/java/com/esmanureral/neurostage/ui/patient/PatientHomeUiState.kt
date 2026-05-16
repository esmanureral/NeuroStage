package com.esmanureral.neurostage.ui.patient

import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import com.esmanureral.neurostage.R
import com.esmanureral.neurostage.domain.patient.puzzle.mild.MildPuzzleCatalog
import com.esmanureral.neurostage.domain.patient.puzzle.mild.MildPuzzleStep
import com.esmanureral.neurostage.domain.patient.PatientStage

data class StageChipUi(
    @get:StringRes val labelRes: Int,
    @get:ColorRes val textColorRes: Int,
)

data class PuzzleCardUi(
    @get:StringRes val subtitleFormatRes: Int,
    val stepIndexInLevel: Int,
    @get:StringRes val stepNameRes: Int,
)

data class PatientHomeUiState(
    val stageIndex: Int? = null,
    val stageChip: StageChipUi? = null,
    val showExercises: Boolean = false,
    val useExerciseAppBarTitle: Boolean = false,
    val puzzleCard: PuzzleCardUi? = null,
    val showModerateNotice: Boolean = false,
    val showNonEligibleNotice: Boolean = false,
) {
    companion object {
        fun from(stageIndex: Int?, progress: Int): PatientHomeUiState {
            val step = MildPuzzleCatalog.stepForProgress(progress)
            val eligible = PatientStage.isBrainExerciseEligible(stageIndex)
            return PatientHomeUiState(
                stageIndex = stageIndex,
                stageChip = StageChipMapper.from(stageIndex),
                showExercises = eligible,
                useExerciseAppBarTitle = eligible,
                puzzleCard = if (eligible) buildPuzzleCard(step) else null,
                showModerateNotice = stageIndex == PatientStage.MODERATE_DEMENTIA,
                showNonEligibleNotice = stageIndex != null && !eligible &&
                        stageIndex != PatientStage.MODERATE_DEMENTIA,
            )
        }

        private fun buildPuzzleCard(step: MildPuzzleStep) =
            PuzzleCardUi(
                subtitleFormatRes = MildPuzzleCatalog.homeSubtitleFormatRes(step.gameLevel),
                stepIndexInLevel = MildPuzzleCatalog.stepIndexInLevel(step),
                stepNameRes = step.nameRes,
            )
    }
}

private object StageChipMapper {
    fun from(stageIndex: Int?): StageChipUi? = when (stageIndex) {
        PatientStage.MILD_DEMENTIA -> StageChipUi(
            R.string.patient_stage_mild,
            R.color.patient_stage_mild,
        )

        PatientStage.MODERATE_DEMENTIA -> StageChipUi(
            R.string.patient_stage_moderate,
            R.color.patient_stage_moderate,
        )

        PatientStage.HEALTHY -> StageChipUi(
            R.string.patient_stage_healthy,
            R.color.patient_stage_healthy,
        )

        PatientStage.VERY_MILD_DEMENTIA -> StageChipUi(
            R.string.patient_stage_very_mild,
            R.color.patient_stage_very_mild,
        )

        else -> null
    }
}
