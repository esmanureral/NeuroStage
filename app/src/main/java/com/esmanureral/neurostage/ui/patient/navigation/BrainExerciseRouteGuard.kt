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
    val allowed = PatientStage.canAccessPatientExerciseHub(stageIndex)
    LaunchedEffect(stageIndex) {
        if (stageIndex != null && !allowed) {
            onBlocked()
        }
    }
    if (allowed) {
        content()
    }
}

@Composable
fun MildHomePuzzleRouteGuard(
    stageIndex: Int?,
    onBlocked: () -> Unit,
    content: @Composable () -> Unit,
) {
    val allowed = PatientStage.isBrainExerciseEligible(stageIndex)
    LaunchedEffect(stageIndex) {
        if (stageIndex != null && !allowed) {
            onBlocked()
        }
    }
    if (allowed) {
        content()
    }
}

@Composable
fun MriModeratePuzzleRouteGuard(
    stageIndex: Int?,
    onBlocked: () -> Unit,
    content: @Composable () -> Unit,
) {
    val allowed = stageIndex == PatientStage.MODERATE_DEMENTIA
    LaunchedEffect(stageIndex) {
        if (!allowed) {
            onBlocked()
        }
    }
    if (allowed) {
        content()
    }
}