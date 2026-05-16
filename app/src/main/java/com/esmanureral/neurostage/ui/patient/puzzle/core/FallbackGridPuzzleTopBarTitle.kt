package com.esmanureral.neurostage.ui.patient.puzzle.core

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.esmanureral.neurostage.R
import com.esmanureral.neurostage.ui.theme.PatientColors
import com.esmanureral.neurostage.ui.theme.PatientDimens

@Composable
fun FallbackGridPuzzleTopBarTitle(
    fallbackGrid: Int,
    slotCount: Int,
) {
    Column {
        Text(
            text = stringResource(R.string.puzzle_title),
            fontSize = PatientDimens.puzzleTopBarTitleSize,
            fontWeight = FontWeight.SemiBold,
            color = PatientColors.puzzleTextPrimary,
        )
        Text(
            text = stringResource(R.string.puzzle_subtitle, fallbackGrid, fallbackGrid, slotCount),
            fontSize = PatientDimens.puzzleTopBarSubtitleSize,
            color = PatientColors.puzzleTextSecondary,
        )
    }
}