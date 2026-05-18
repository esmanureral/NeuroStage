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
    val hubAllowed = PatientStage.canAccessPatientExerciseHub(stageIndex)
    val showContent = stageIndex == null || hubAllowed
    LaunchedEffect(stageIndex, hubAllowed) {
        if (stageIndex != null && !hubAllowed) {
            onBlocked()
        }
    }
    if (showContent) {
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
fun MemoryMatchRouteGuard(
    stageIndex: Int?,
    onBlocked: () -> Unit,
    content: @Composable () -> Unit,
) {
    val allowed = PatientStage.isBrainExerciseEligible(stageIndex) ||
            stageIndex == PatientStage.MODERATE_DEMENTIA
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
fun ReminderRouteGuard(
    stageIndex: Int?,
    onBlocked: () -> Unit,
    content: @Composable () -> Unit,
) {
    val allowed = PatientStage.canUseReminders(stageIndex)
    LaunchedEffect(stageIndex, allowed) {
        if (stageIndex != null && !allowed) {
            onBlocked()
        }
    }
    when {
        stageIndex == null -> Unit
        allowed -> content()
    }
}

@Composable
fun ColorMatchRouteGuard(
    stageIndex: Int?,
    onBlocked: () -> Unit,
    content: @Composable () -> Unit,
) {
    val allowed = PatientStage.canAccessPatientExerciseHub(stageIndex)
    LaunchedEffect(stageIndex, allowed) {
        if (stageIndex != null && !allowed) {
            onBlocked()
        }
    }
    when {
        stageIndex == null -> Unit
        allowed -> content()
    }
}

@Composable
fun MriModeratePuzzleRouteGuard(
    stageIndex: Int?,
    onBlocked: () -> Unit,
    content: @Composable () -> Unit,
) {
    val allowed = stageIndex == PatientStage.MODERATE_DEMENTIA
    LaunchedEffect(stageIndex, allowed) {
        if (stageIndex != null && !allowed) {
            onBlocked()
        }
    }
    when {
        stageIndex == null -> Unit
        allowed -> content()
    }
}