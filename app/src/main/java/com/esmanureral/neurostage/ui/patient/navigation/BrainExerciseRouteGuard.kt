package com.esmanureral.neurostage.ui.patient.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.esmanureral.neurostage.domain.patient.PatientStage

@Composable
fun BrainExerciseRouteGuard(
    stageIndex: Int?,
    onBlocked: () -> Unit,
    content: @Composable () -> Unit,
) {
    LaunchedEffect(stageIndex) {
        if (stageIndex != null && !PatientStage.isBrainExerciseEligible(stageIndex)) {
            onBlocked()
        }
    }
    if (PatientStage.isBrainExerciseEligible(stageIndex)) {
        content()
    }
}
