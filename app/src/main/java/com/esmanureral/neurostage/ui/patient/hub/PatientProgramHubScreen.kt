package com.esmanureral.neurostage.ui.patient.hub

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Alarm
import androidx.compose.material.icons.outlined.Psychology
import androidx.compose.material.icons.outlined.Upload
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.esmanureral.neurostage.R
import com.esmanureral.neurostage.domain.patient.PatientStage
import com.esmanureral.neurostage.ui.patient.PatientHomeExitDialog
import com.esmanureral.neurostage.ui.patient.games.GameHubViewModel
import com.esmanureral.neurostage.ui.theme.PatientColors
import com.esmanureral.neurostage.ui.theme.PatientDimens

@Composable
fun PatientProgramHubScreen(
    onOpenExercises: () -> Unit,
    onOpenReminders: () -> Unit,
    onStartNewScan: () -> Unit,
    onBack: () -> Unit,
    viewModel: GameHubViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val stageIndex = uiState.stageIndex
    val isHubRoot = PatientStage.usesExerciseHubAsHome(stageIndex)
    val motivationQuote = rememberSessionMotivationQuote()
    var showExitDialog by remember { mutableStateOf(false) }

    val handleBack: () -> Unit = {
        if (isHubRoot) {
            showExitDialog = true
        } else {
            onBack()
        }
    }

    BackHandler(enabled = isHubRoot) {
        showExitDialog = true
    }

    PatientHomeExitDialog(
        visible = showExitDialog,
        onDismiss = { },
        onConfirm = {
            onBack()
        },
    )

    ProgramHubScreenBackground {
        Scaffold(
            containerColor = Color.Transparent,
        ) { innerPadding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = PatientDimens.gameHubScreenPadding),
                contentPadding = PaddingValues(
                    top = PatientDimens.gameHubContentTopPadding,
                    bottom = PatientDimens.gameHubContentBottomPadding,
                ),
                verticalArrangement = Arrangement.spacedBy(PatientDimens.gameHubCardGap),
            ) {
                item {
                    ProgramHubTopBar(
                        onBack = handleBack,
                        backContentDescription = if (isHubRoot) {
                            stringResource(R.string.patient_exercise_back_exit)
                        } else {
                            stringResource(R.string.patient_exercise_back_home)
                        },
                    )
                }
                item {
                    ProgramHubHeroSection(
                        greeting = stringResource(R.string.patient_hub_greeting),
                        motivationQuote = motivationQuote,
                        diagnosisLabel = uiState.diagnosisLabel,
                    )
                }
                item {
                    ProgramHubSectionLabel(
                        text = stringResource(R.string.patient_hub_actions_section),
                    )
                }
                item {
                    ProgramHubActionTile(
                        icon = Icons.Outlined.Upload,
                        iconBackground = PatientColors.primaryLight,
                        iconTint = PatientColors.primary,
                        title = stringResource(R.string.patient_hub_action_scan_title),
                        subtitle = stringResource(R.string.patient_hub_action_scan_sub),
                        onClick = onStartNewScan,
                    )
                }
                item {
                    ProgramHubActionTile(
                        icon = Icons.Outlined.Psychology,
                        iconBackground = PatientColors.hubMemoryCardBg,
                        iconTint = PatientColors.hubMemoryTitle,
                        title = stringResource(R.string.patient_hub_action_exercises_title),
                        subtitle = stringResource(R.string.patient_hub_action_exercises_sub),
                        onClick = onOpenExercises,
                    )
                }
                if (PatientStage.canUseReminders(stageIndex)) {
                    item {
                        ProgramHubActionTile(
                            icon = Icons.Outlined.Alarm,
                            iconBackground = PatientColors.hubRoutineCardBg,
                            iconTint = PatientColors.hubRoutineTitle,
                            title = stringResource(R.string.patient_hub_action_reminder_title),
                            subtitle = stringResource(R.string.patient_hub_action_reminder_sub),
                            onClick = onOpenReminders,
                        )
                    }
                }
            }
        }
    }
}
