package com.esmanureral.neurostage.ui.patient

import com.esmanureral.neurostage.ui.theme.PatientColors
import com.esmanureral.neurostage.ui.theme.PatientDimens
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight

@Composable
fun PatientScanGuidanceCard(
    stageIndex: Int,
    onOpenGames: (() -> Unit)?,
    modifier: Modifier = Modifier,
) {
    val guidance = PatientScanGuidanceMapper.from(stageIndex)
    PatientScanGuidanceCardContent(
        guidance = guidance,
        onOpenGames = onOpenGames,
        modifier = modifier,
    )
}

@Composable
private fun PatientScanGuidanceCardContent(
    guidance: PatientScanGuidanceUi,
    onOpenGames: (() -> Unit)?,
    modifier: Modifier = Modifier,
) {
    val dimens = PatientDimens
    val background = colorResource(guidance.palette.backgroundRes)
    val border = colorResource(guidance.palette.borderRes)
    val button = colorResource(guidance.palette.buttonRes)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(dimens.guidanceCardCorner))
            .background(background)
            .border(
                width = dimens.guidanceCardBorder,
                color = border,
                shape = RoundedCornerShape(dimens.guidanceCardCorner),
            )
            .padding(dimens.guidanceCardPadding),
        verticalArrangement = Arrangement.spacedBy(dimens.guidanceContentGap),
    ) {
        GuidanceHeader(guidance = guidance)
        GuidanceBody(guidance = guidance)
        GuidanceActionButton(
            guidance = guidance,
            buttonColor = button,
            onOpenGames = onOpenGames,
        )
    }
}

@Composable
private fun GuidanceHeader(guidance: PatientScanGuidanceUi) {
    val dimens = PatientDimens
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(dimens.guidanceHeaderGap),
    ) {
        guidance.iconRes?.let { iconRes ->
            Text(
                text = stringResource(iconRes),
                fontSize = dimens.guidanceIconSize,
            )
        }
        Text(
            text = stringResource(guidance.titleRes),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.ExtraBold,
            color = PatientColors.guidanceTextTitle,
        )
    }
}

@Composable
private fun GuidanceBody(guidance: PatientScanGuidanceUi) {
    val dimens = PatientDimens
    Text(
        text = stringResource(guidance.bodyRes),
        style = MaterialTheme.typography.bodyMedium,
        color = PatientColors.guidanceTextBody,
        lineHeight = dimens.guidanceBodyLineHeight,
    )
}

@Composable
private fun GuidanceActionButton(
    guidance: PatientScanGuidanceUi,
    buttonColor: androidx.compose.ui.graphics.Color,
    onOpenGames: (() -> Unit)?,
) {
    val dimens = PatientDimens
    val labelRes = guidance.buttonLabelRes ?: return
    val showButton = (guidance.showGamesButton && onOpenGames != null) || !guidance.showGamesButton
    if (!showButton) return

    Spacer(Modifier.height(dimens.guidanceButtonTopGap))
    Button(
        onClick = { if (guidance.showGamesButton) onOpenGames?.invoke() },
        colors = ButtonDefaults.buttonColors(containerColor = buttonColor),
        shape = RoundedCornerShape(dimens.guidanceButtonCorner),
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(vertical = dimens.guidanceButtonVPadding),
    ) {
        Text(
            text = stringResource(labelRes),
            fontWeight = FontWeight.Black,
            color = PatientColors.guidanceOnButton,
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}
