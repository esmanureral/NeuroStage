package com.esmanureral.neurostage.ui.patient.games

import com.esmanureral.neurostage.ui.theme.PatientColors
import com.esmanureral.neurostage.ui.theme.PatientDimens
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.esmanureral.neurostage.R
import com.esmanureral.neurostage.ui.patient.PatientExerciseTopBar
import androidx.compose.material3.ExperimentalMaterial3Api

@Composable
fun InstructionBox(
    text: String,
    accentColor: Color,
    backgroundColor: Color,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(PatientDimens.gameInstructionCorner))
            .background(backgroundColor)
            .padding(PatientDimens.gameInstructionPadding),
    ) {
        Text(
            text = text,
            fontSize = PatientDimens.gameInstructionSize,
            fontWeight = FontWeight.Bold,
            color = accentColor,
            textAlign = TextAlign.Center,
            lineHeight = PatientDimens.gameInstructionLineHeight,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
fun PrimaryGameButton(
    text: String,
    containerColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = containerColor),
        shape = RoundedCornerShape(PatientDimens.gameButtonCorner),
        modifier = modifier,
    ) {
        Text(
            text = text,
            fontSize = PatientDimens.gameButtonTextSize,
            fontWeight = FontWeight.Black,
            color = PatientColors.surface,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameScreenTopBar(
    title: String,
    onBack: () -> Unit,
) {
    PatientExerciseTopBar(
        title = title,
        onBack = onBack,
        backLabel = stringResource(R.string.patient_exercise_back_hub),
    )
}