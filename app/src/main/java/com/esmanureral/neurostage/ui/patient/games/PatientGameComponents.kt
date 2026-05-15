package com.esmanureral.neurostage.ui.patient.games

import com.esmanureral.neurostage.ui.theme.PatientColors
import com.esmanureral.neurostage.ui.theme.PatientDimens
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.esmanureral.neurostage.R

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
    modifier: Modifier = Modifier
        .fillMaxWidth()
        .height(PatientDimens.gamePrimaryButtonHeight),
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

@Composable
fun GameBackBottomBar(onBack: () -> Unit) {
    Button(
        onClick = onBack,
        colors = ButtonDefaults.buttonColors(containerColor = PatientColors.gameTextMuted),
        shape = RoundedCornerShape(PatientDimens.cornerNone),
        modifier = Modifier
            .fillMaxWidth()
            .height(PatientDimens.gameBottomBarHeight),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = stringResource(R.string.patient_home_cd_back),
                modifier = Modifier.size(PatientDimens.gameBackIconSize),
                tint = PatientColors.surface,
            )
            Spacer(Modifier.width(PatientDimens.gameBackIconGap))
            Text(
                text = stringResource(R.string.game_btn_back_upper),
                fontSize = PatientDimens.gameButtonTextSize,
                fontWeight = FontWeight.Black,
                color = PatientColors.surface,
            )
        }
    }
}
