package com.esmanureral.neurostage.ui.patient

import com.esmanureral.neurostage.ui.theme.PatientColors
import com.esmanureral.neurostage.ui.theme.PatientDimens
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.esmanureral.neurostage.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StageAwarePatientHomeScreen(
    onStartScan: () -> Unit,
    onOpenGames: () -> Unit,
    onStartRoutineGame: () -> Unit,
    onStartMemoryGame: () -> Unit,
    onStartPuzzleGame: () -> Unit,
    onBackToRolePick: () -> Unit,
    viewModel: PatientHomeViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showExitDialog by remember { mutableStateOf(false) }

    PatientHomeExitDialog(
        visible = showExitDialog,
        onDismiss = { showExitDialog = false },
        onConfirm = {
            showExitDialog = false
            viewModel.clearSession()
            onBackToRolePick()
        },
    )

    Scaffold(
        containerColor = PatientColors.background,
        topBar = {
            PatientHomeTopBar(
                uiState = uiState,
                onLogoutClick = { showExitDialog = true },
            )
        },
    ) { innerPadding ->
        PatientHomeBody(
            uiState = uiState,
            onStartScan = onStartScan,
            onOpenGames = onOpenGames,
            onStartRoutineGame = onStartRoutineGame,
            onStartMemoryGame = onStartMemoryGame,
            onStartPuzzleGame = onStartPuzzleGame,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(
                    horizontal = PatientDimens.homeScreenHorizontalPadding,
                    vertical = PatientDimens.homeScreenVerticalPadding,
                ),
        )
    }
}

@Composable
private fun PatientHomeBody(
    uiState: PatientHomeUiState,
    onStartScan: () -> Unit,
    onOpenGames: () -> Unit,
    onStartRoutineGame: () -> Unit,
    onStartMemoryGame: () -> Unit,
    onStartPuzzleGame: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        PatientHomeIntroSection(uiState = uiState)

        PatientHomeMainSection(
            uiState = uiState,
            onStartPuzzleGame = onStartPuzzleGame,
            onStartRoutineGame = onStartRoutineGame,
            onStartMemoryGame = onStartMemoryGame,
            onOpenGames = onOpenGames,
        )

        Spacer(Modifier.height(PatientDimens.homeSectionSpacing))
        PatientHomeScanActionCard(onClick = onStartScan)
        Spacer(Modifier.height(PatientDimens.homeDisclaimerSpacing))
        Text(
            stringResource(R.string.patient_home_info_disclaimer),
            fontSize = PatientDimens.homeDisclaimerTextSize,
            color = PatientColors.textSecondary,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(PatientDimens.homeBottomSpacing))
    }
}

@Composable
private fun PatientHomeIntroSection(uiState: PatientHomeUiState) {
    if (uiState.useExerciseAppBarTitle) {
        MildDementiaHeader()
        Spacer(Modifier.height(PatientDimens.homeSectionSpacing))
    } else if (uiState.stageChip == null) {
        Text(
            stringResource(R.string.patient_home_subtitle),
            fontSize = PatientDimens.homeSubtitleTextSize,
            color = PatientColors.textSecondary,
        )
        Spacer(Modifier.height(PatientDimens.homeIntroBottomGap))
    }
}

@Composable
private fun PatientHomeMainSection(
    uiState: PatientHomeUiState,
    onStartPuzzleGame: () -> Unit,
    onStartRoutineGame: () -> Unit,
    onStartMemoryGame: () -> Unit,
    onOpenGames: () -> Unit,
) {
    when {
        uiState.showExercises -> PatientHomeExerciseSection(
            puzzleCard = uiState.puzzleCard,
            onStartPuzzleGame = onStartPuzzleGame,
            onStartRoutineGame = onStartRoutineGame,
            onStartMemoryGame = onStartMemoryGame,
            onOpenGames = onOpenGames,
        )

        uiState.showModerateNotice -> ModerateStageNotice()
        uiState.showNonEligibleNotice -> NonMildScannedNotice()
    }
}
