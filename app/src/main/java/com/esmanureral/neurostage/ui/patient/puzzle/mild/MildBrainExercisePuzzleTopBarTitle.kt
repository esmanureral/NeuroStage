package com.esmanureral.neurostage.ui.patient.puzzle.mild

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.esmanureral.neurostage.R
import com.esmanureral.neurostage.ui.theme.PatientColors
import com.esmanureral.neurostage.ui.theme.PatientDimens

@Composable
fun MildBrainExercisePuzzleTopBarTitle(stepLabel: String) {
    Column {
        Text(
            text = stringResource(R.string.puzzle_title),
            fontSize = PatientDimens.puzzleTopBarTitleSize,
            fontWeight = FontWeight.SemiBold,
            color = PatientColors.puzzleTextPrimary,
        )
        Text(
            text = stringResource(R.string.puzzle_subtitle_mild, stepLabel),
            fontSize = PatientDimens.puzzleTopBarSubtitleSize,
            color = PatientColors.puzzleTextSecondary,
        )
    }
}