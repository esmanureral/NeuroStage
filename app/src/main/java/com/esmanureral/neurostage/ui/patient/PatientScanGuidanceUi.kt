package com.esmanureral.neurostage.ui.patient

import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import com.esmanureral.neurostage.R
import com.esmanureral.neurostage.domain.patient.PatientStage

data class PatientScanGuidancePalette(
    @get:ColorRes val backgroundRes: Int,
    @get:ColorRes val borderRes: Int,
    @get:ColorRes val buttonRes: Int,
)

data class PatientScanGuidanceUi(
    @get:StringRes val iconRes: Int?,
    @get:StringRes val titleRes: Int,
    @get:StringRes val bodyRes: Int,
    @get:StringRes val buttonLabelRes: Int?,
    val palette: PatientScanGuidancePalette,
    val showGamesButton: Boolean,
)

object PatientScanGuidanceMapper {
    fun from(stageIndex: Int): PatientScanGuidanceUi = when (stageIndex) {
        PatientStage.MODERATE_DEMENTIA -> PatientScanGuidanceUi(
            iconRes = null,
            titleRes = R.string.guidance_moderate_title,
            bodyRes = R.string.guidance_moderate_body,
            buttonLabelRes = R.string.guidance_moderate_button,
            palette = PatientScanGuidancePalette(
                backgroundRes = R.color.patient_guidance_moderate_bg,
                borderRes = R.color.patient_guidance_moderate_border,
                buttonRes = R.color.patient_guidance_moderate_button,
            ),
            showGamesButton = true,
        )

        PatientStage.MILD_DEMENTIA -> PatientScanGuidanceUi(
            iconRes = null,
            titleRes = R.string.guidance_mild_title,
            bodyRes = R.string.guidance_mild_body,
            buttonLabelRes = R.string.guidance_mild_button,
            palette = PatientScanGuidancePalette(
                backgroundRes = R.color.patient_guidance_mild_bg,
                borderRes = R.color.patient_guidance_mild_border,
                buttonRes = R.color.patient_guidance_mild_button,
            ),
            showGamesButton = true,
        )

        PatientStage.VERY_MILD_DEMENTIA -> PatientScanGuidanceUi(
            iconRes = null,
            titleRes = R.string.guidance_very_mild_title,
            bodyRes = R.string.guidance_very_mild_body,
            buttonLabelRes = R.string.guidance_very_mild_button,
            palette = PatientScanGuidancePalette(
                backgroundRes = R.color.patient_guidance_very_mild_bg,
                borderRes = R.color.patient_guidance_very_mild_border,
                buttonRes = R.color.patient_guidance_very_mild_button,
            ),
            showGamesButton = true,
        )

        else -> PatientScanGuidanceUi(
            iconRes = R.string.guidance_healthy_icon,
            titleRes = R.string.guidance_healthy_title,
            bodyRes = R.string.guidance_healthy_body,
            buttonLabelRes = null,
            palette = PatientScanGuidancePalette(
                backgroundRes = R.color.patient_guidance_healthy_bg,
                borderRes = R.color.patient_guidance_healthy_border,
                buttonRes = R.color.patient_guidance_healthy_button,
            ),
            showGamesButton = false,
        )
    }
}