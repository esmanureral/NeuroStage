package com.esmanureral.neurostage.ui.patient.games.puzzle

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import com.esmanureral.neurostage.R
import com.esmanureral.neurostage.ui.patient.puzzle.core.FallbackGridPuzzleTopBarTitle
import com.esmanureral.neurostage.ui.patient.puzzle.core.PuzzleCatalogKind
import com.esmanureral.neurostage.ui.patient.puzzle.core.PuzzleSessionConfig
import com.esmanureral.neurostage.ui.patient.puzzle.mild.MildBrainExercisePuzzleTopBarTitle
import com.esmanureral.neurostage.ui.theme.PatientColors
import androidx.compose.ui.res.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PuzzleGameTopBar(
    session: PuzzleSessionConfig,
    stepLabel: String,
    fallbackGrid: Int,
    slotCount: Int,
    onBack: () -> Unit,
    onReset: () -> Unit,
) {
    TopAppBar(
        title = {
            PuzzleTopBarTitle(
                catalogKind = session.catalogKind,
                stepLabel = stepLabel,
                fallbackGrid = fallbackGrid,
                slotCount = slotCount,
            )
        },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.puzzle_cd_back),
                    tint = PatientColors.puzzleTextPrimary,
                )
            }
        },
        actions = {
            IconButton(onClick = onReset) {
                Icon(
                    Icons.Filled.Refresh,
                    contentDescription = stringResource(R.string.puzzle_cd_reset),
                    tint = PatientColors.puzzleAccent,
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = PatientColors.surface,
            titleContentColor = PatientColors.puzzleTextPrimary,
            navigationIconContentColor = PatientColors.puzzleTextPrimary,
        ),
    )
}

@Composable
private fun PuzzleTopBarTitle(
    catalogKind: PuzzleCatalogKind,
    stepLabel: String,
    fallbackGrid: Int,
    slotCount: Int,
) {
    when (catalogKind) {
        PuzzleCatalogKind.MildProgression,
        PuzzleCatalogKind.ModerateProgression,
            -> MildBrainExercisePuzzleTopBarTitle(stepLabel)

        PuzzleCatalogKind.FallbackGrid -> FallbackGridPuzzleTopBarTitle(
            fallbackGrid = fallbackGrid,
            slotCount = slotCount,
        )
    }
}
