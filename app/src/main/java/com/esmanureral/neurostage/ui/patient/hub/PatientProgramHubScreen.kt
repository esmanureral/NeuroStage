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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.esmanureral.neurostage.R
import com.esmanureral.neurostage.domain.patient.PatientStage
import com.esmanureral.neurostage.ui.patient.PatientHomeExitDialog
import com.esmanureral.neurostage.ui.patient.games.GameHubViewModel
import com.esmanureral.neurostage.ui.theme.PatientDimens

@Composable
fun PatientProgramHubScreen(
    onOpenExercises: () -> Unit,
    onOpenReminders: () -> Unit,
    onStartNewScan: () -> Unit,
    onBack: () -> Unit,
    onExitToRolePick: () -> Unit,
    onDismissOverlay: () -> Unit = {},
    viewModel: GameHubViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val stageIndex = uiState.stageIndex
    val isHubRoot = true
    val motivationQuote = remember { viewModel.resolveMotivationQuote() }
    var showExitDialog by remember { mutableStateOf(false) }

    val exerciseBg = colorResource(R.color.patient_hub_tile_exercise_bg)
    val exerciseIconBg = colorResource(R.color.patient_hub_tile_exercise_icon_bg)
    val exerciseIcon = colorResource(R.color.patient_hub_tile_exercise_icon)
    val reminderBg = colorResource(R.color.patient_hub_tile_reminder_bg)
    val reminderIconBg = colorResource(R.color.patient_hub_tile_reminder_icon_bg)
    val reminderIcon = colorResource(R.color.patient_hub_tile_reminder_icon)
    val scanBg = colorResource(R.color.patient_hub_tile_scan_bg)
    val scanIconBg = colorResource(R.color.patient_hub_tile_scan_icon_bg)
    val scanIcon = colorResource(R.color.patient_hub_tile_scan_icon)

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

    LaunchedEffect(showExitDialog) {
        if (showExitDialog) {
            onDismissOverlay()
        }
    }

    PatientHomeExitDialog(
        visible = showExitDialog,
        onDismiss = { showExitDialog = false },
        onConfirm = {
            showExitDialog = false
            onExitToRolePick()
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
                    ProgramHubHeroSection(motivationQuote = motivationQuote)
                }
                item {
                    ProgramHubSectionLabel(
                        text = stringResource(R.string.patient_hub_actions_section),
                    )
                }
                item {
                    ProgramHubTintedActionTile(
                        containerColor = exerciseBg,
                        iconBackground = exerciseIconBg,
                        iconTint = exerciseIcon,
                        icon = Icons.Outlined.Psychology,
                        title = stringResource(R.string.patient_hub_action_exercises_title),
                        subtitle = stringResource(R.string.patient_hub_action_exercises_sub),
                        onClick = onOpenExercises,
                    )
                }
                if (PatientStage.canUseReminders(stageIndex)) {
                    item {
                        ProgramHubTintedActionTile(
                            containerColor = reminderBg,
                            iconBackground = reminderIconBg,
                            iconTint = reminderIcon,
                            icon = Icons.Outlined.Alarm,
                            title = stringResource(R.string.patient_hub_action_reminder_title),
                            subtitle = stringResource(R.string.patient_hub_action_reminder_sub),
                            onClick = onOpenReminders,
                        )
                    }
                }
                item {
                    ProgramHubTintedActionTile(
                        containerColor = scanBg,
                        iconBackground = scanIconBg,
                        iconTint = scanIcon,
                        icon = Icons.Outlined.Upload,
                        title = stringResource(R.string.patient_hub_action_scan_title),
                        subtitle = stringResource(R.string.patient_hub_action_scan_sub),
                        onClick = onStartNewScan,
                    )
                }
            }
        }
    }
}
