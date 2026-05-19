package com.esmanureral.neurostage.ui.patient.games.memorymatch

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import com.esmanureral.neurostage.R
import com.esmanureral.neurostage.ui.theme.PatientColors
import com.esmanureral.neurostage.ui.theme.PatientDimens

internal const val MEMORY_MATCH_FLIP_MS = 320

@Composable
internal fun MemoryMatchCardView(
    card: MemoryMatchCard,
    faceUp: Boolean,
    highlight: MemoryMatchHighlight,
    emojiSize: TextUnit,
    wordSize: TextUnit,
    highContrast: Boolean,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(PatientDimens.memoryMatchCardCorner)
    val closedDesc = stringResource(R.string.memory_match_cd_card_closed)
    val openDesc = stringResource(
        R.string.memory_match_cd_card_open,
        stringResource(card.labelRes),
    )

    val rotation by animateFloatAsState(
        targetValue = if (faceUp) 180f else 0f,
        animationSpec = tween(MEMORY_MATCH_FLIP_MS),
        label = "cardFlipY",
    )

    val clickMod = if (enabled) {
        Modifier.clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = ripple(bounded = true, color = PatientColors.matchAccent),
            onClick = onClick,
        )
    } else {
        Modifier
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .semantics {
                contentDescription = if (faceUp) openDesc else closedDesc
            }
            .graphicsLayer {
                rotationY = rotation
                cameraDistance = 14f * density
            }
            .clip(shape)
            .then(clickMod),
        contentAlignment = Alignment.Center,
    ) {
        if (rotation <= 90f) {
            MatchCardBack(shape = shape, highContrast = highContrast)
        } else {
            MatchCardFront(
                face = card.face,
                emojiSize = emojiSize,
                wordSize = wordSize,
                highlight = highlight,
                highContrast = highContrast,
                shape = shape,
                modifier = Modifier.graphicsLayer { rotationY = 180f },
            )
        }
    }
}

@Composable
internal fun MemoryMatchAnchorCard(
    card: MemoryMatchCard,
    emojiSize: TextUnit,
    highlight: MemoryMatchHighlight,
    highContrast: Boolean,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(PatientDimens.memoryMatchCardCorner)
    val openDesc = stringResource(
        R.string.memory_match_cd_card_open,
        stringResource(card.labelRes),
    )
    val clickMod = if (enabled) {
        Modifier.clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = ripple(bounded = true, color = PatientColors.matchAccent),
            onClick = onClick,
        )
    } else {
        Modifier
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .semantics { contentDescription = openDesc }
            .clip(shape)
            .then(clickMod),
        contentAlignment = Alignment.Center,
    ) {
        MatchCardFront(
            face = card.face,
            emojiSize = emojiSize,
            wordSize = PatientDimens.memoryMatchCardWordSize,
            highlight = highlight,
            highContrast = highContrast,
            shape = shape,
        )
    }
}

@Composable
private fun MatchCardBack(shape: RoundedCornerShape, highContrast: Boolean) {
    val backColor =
        if (highContrast) PatientColors.matchAccentLight else PatientColors.matchCardBack
    val borderColor =
        if (highContrast) PatientColors.matchAccent else PatientColors.matchCardBackBorder
    val borderWidth: Dp = if (highContrast) {
        PatientDimens.memoryMatchCardBorder * 1.5f
    } else {
        PatientDimens.memoryMatchCardBorder
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(shape)
            .background(backColor)
            .border(width = borderWidth, color = borderColor, shape = shape),
    )
}

@Composable
private fun MatchCardFront(
    face: MemoryMatchCardFace,
    emojiSize: TextUnit,
    wordSize: TextUnit,
    highlight: MemoryMatchHighlight,
    highContrast: Boolean,
    shape: RoundedCornerShape,
    modifier: Modifier = Modifier,
) {
    val (backgroundColor, borderColor) = when {
        highlight == MemoryMatchHighlight.Success ->
            PatientColors.matchMatchedBackground to PatientColors.matchMatchedBorder

        highlight == MemoryMatchHighlight.Failure ->
            PatientColors.matchMismatchBackground to PatientColors.matchMismatchBorder

        highContrast ->
            Color.White to PatientColors.matchAccent

        else ->
            PatientColors.matchCardFace to PatientColors.matchCardFace.copy(alpha = 0.2f)
    }
    val borderWidth: Dp = if (highContrast) {
        PatientDimens.memoryMatchCardBorder * 1.5f
    } else {
        PatientDimens.memoryMatchCardBorder
    }
    val facePadding = if (highContrast) {
        PatientDimens.memoryMatchModerateBoardPadding
    } else {
        PatientDimens.gameGridGapS
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .clip(shape)
            .background(backgroundColor)
            .border(width = borderWidth, color = borderColor, shape = shape)
            .padding(horizontal = facePadding),
        contentAlignment = Alignment.Center,
    ) {
        when (face) {
            is MemoryMatchCardFace.Emoji -> Text(
                text = stringResource(face.res),
                fontSize = emojiSize,
                textAlign = TextAlign.Center,
                maxLines = 1,
            )

            is MemoryMatchCardFace.Word -> Text(
                text = stringResource(face.res),
                fontSize = wordSize,
                fontWeight = FontWeight.Bold,
                color = PatientColors.matchAccent,
                textAlign = TextAlign.Center,
                maxLines = 2,
            )
        }
    }
}
