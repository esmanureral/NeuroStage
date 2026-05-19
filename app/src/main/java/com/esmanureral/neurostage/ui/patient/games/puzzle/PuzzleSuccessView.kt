package com.esmanureral.neurostage.ui.patient.games.puzzle

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.esmanureral.neurostage.R
import com.esmanureral.neurostage.ui.theme.PatientColors
import com.esmanureral.neurostage.ui.theme.PatientDimens

@Composable
fun PuzzleSuccessView(
    modifier: Modifier = Modifier,
    puzzleBitmap: ImageBitmap,
    boardAspectRatio: Float,
    contentWidthFraction: Float,
    hasNextLevel: Boolean,
    onNextLevel: () -> Unit,
    onPlayAgain: () -> Unit,
    onBack: () -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(PatientDimens.puzzleSuccessPadding),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        PuzzleSuccessImage(
            puzzleBitmap = puzzleBitmap,
            boardAspectRatio = boardAspectRatio,
            contentWidthFraction = contentWidthFraction,
        )
        Spacer(Modifier.height(PatientDimens.puzzleSuccessTitleGap))
        Text(
            text = stringResource(R.string.puzzle_success_title),
            fontSize = PatientDimens.puzzleSuccessTitleSize,
            fontWeight = FontWeight.Bold,
            color = PatientColors.puzzleSuccess,
        )
        Spacer(Modifier.height(PatientDimens.puzzleSuccessButtonGap))
        PuzzleSuccessActions(
            contentWidthFraction = contentWidthFraction,
            hasNextLevel = hasNextLevel,
            onNextLevel = onNextLevel,
            onPlayAgain = onPlayAgain,
            onBack = onBack,
        )
    }
}

@Composable
private fun PuzzleSuccessImage(
    puzzleBitmap: ImageBitmap,
    boardAspectRatio: Float,
    contentWidthFraction: Float,
) {
    Image(
        bitmap = puzzleBitmap,
        contentDescription = null,
        contentScale = ContentScale.FillBounds,
        modifier = Modifier
            .fillMaxWidth(contentWidthFraction)
            .aspectRatio(boardAspectRatio)
            .clip(RoundedCornerShape(PatientDimens.puzzleSuccessCorner))
            .border(
                PatientDimens.puzzleSuccessBorder,
                PatientColors.puzzleSuccess,
                RoundedCornerShape(PatientDimens.puzzleSuccessCorner),
            ),
    )
}

@Composable
private fun PuzzleSuccessActions(
    contentWidthFraction: Float,
    hasNextLevel: Boolean,
    onNextLevel: () -> Unit,
    onPlayAgain: () -> Unit,
    onBack: () -> Unit,
) {
    if (hasNextLevel) {
        PuzzleSuccessPrimaryButton(
            textRes = R.string.puzzle_success_next,
            onClick = onNextLevel,
            contentWidthFraction = contentWidthFraction,
        )
    } else {
        PuzzleSuccessPrimaryButton(
            textRes = R.string.puzzle_success_play_again,
            onClick = onPlayAgain,
            contentWidthFraction = contentWidthFraction,
        )
        Spacer(Modifier.height(PatientDimens.puzzleSuccessSecondaryGap))
        PuzzleSuccessSecondaryButton(
            onClick = onBack,
            contentWidthFraction = contentWidthFraction,
        )
    }
}

@Composable
private fun PuzzleSuccessPrimaryButton(
    textRes: Int,
    onClick: () -> Unit,
    contentWidthFraction: Float,
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = PatientColors.puzzleSuccess),
        shape = RoundedCornerShape(PatientDimens.puzzleSuccessButtonCorner),
        modifier = Modifier
            .fillMaxWidth(contentWidthFraction)
            .height(PatientDimens.puzzleSuccessButtonHeight),
    ) {
        Text(
            text = stringResource(textRes),
            fontSize = PatientDimens.puzzleSuccessPrimaryButtonTextSize,
            fontWeight = FontWeight.Black,
            color = PatientColors.surface,
        )
    }
}

@Composable
private fun PuzzleSuccessSecondaryButton(
    onClick: () -> Unit,
    contentWidthFraction: Float,
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = PatientColors.puzzleTextSecondary),
        shape = RoundedCornerShape(PatientDimens.puzzleSuccessButtonCorner),
        modifier = Modifier
            .fillMaxWidth(contentWidthFraction)
            .height(PatientDimens.puzzleSuccessButtonHeight),
    ) {
        Text(
            stringResource(R.string.puzzle_success_back),
            fontSize = PatientDimens.puzzleSuccessSecondaryButtonTextSize,
            fontWeight = FontWeight.Bold,
            color = PatientColors.surface,
        )
    }
}
