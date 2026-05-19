package com.esmanureral.neurostage.ui.patient.hub

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.esmanureral.neurostage.R
import com.esmanureral.neurostage.ui.theme.PatientColors
import com.esmanureral.neurostage.ui.theme.PatientDimens
@Composable
fun ProgramHubScreenBackground(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val top = colorResource(R.color.patient_hub_gradient_top)
    val bottom = colorResource(R.color.patient_hub_gradient_bottom)
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(top, bottom),
                ),
            ),
    ) {
        content()
    }
}

@Composable
fun ProgramHubTopBar(
    onBack: () -> Unit,
    backContentDescription: String,
    modifier: Modifier = Modifier,
) {
    BackHandler(onBack = onBack)

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = onBack) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = backContentDescription,
                tint = PatientColors.primary,
            )
        }
    }
}

@Composable
fun ProgramHubHeroSection(
    motivationQuote: HubMotivationQuote,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(PatientDimens.gameHubSectionGap),
    ) {
        ProgramHubScrapbookQuoteCard(quote = motivationQuote)
        Text(
            text = stringResource(R.string.patient_hub_journey_subtitle),
            fontSize = PatientDimens.gameHubGreetingSubtitleSize,
            fontWeight = FontWeight.Medium,
            color = PatientColors.primary.copy(alpha = 0.7f),
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
fun ProgramHubSectionLabel(
    text: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = text,
        modifier = modifier.fillMaxWidth(),
        fontSize = PatientDimens.homeExerciseSectionTitleSize,
        fontWeight = FontWeight.SemiBold,
        color = PatientColors.primary.copy(alpha = 0.85f),
    )
}

@Composable
fun ProgramHubTintedActionTile(
    containerColor: Color,
    iconBackground: Color,
    iconTint: Color,
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(20.dp)
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick,
            ),
        shape = shape,
        color = containerColor,
        shadowElevation = PatientDimens.homeActionCardElevation,
        tonalElevation = 0.dp,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(iconBackground),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(28.dp),
                )
            }
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Text(
                    text = title,
                    fontSize = PatientDimens.homeActionTitleSize,
                    fontWeight = FontWeight.Bold,
                    color = PatientColors.textPrimary,
                    textAlign = TextAlign.Center,
                )
                Text(
                    text = subtitle,
                    fontSize = PatientDimens.homeActionSubtitleSize,
                    color = PatientColors.textSecondary,
                    lineHeight = PatientDimens.homeNoticeBodyLineHeight,
                    textAlign = TextAlign.Center,
                )
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = PatientColors.primary.copy(alpha = 0.45f),
                modifier = Modifier.size(PatientDimens.homeActionChevronSize),
            )
        }
    }
}

@Composable
fun ProgramHubEmojiActionTile(
    containerColor: Color,
    title: String,
    subtitle: String,
    emoji: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    centeredContent: Boolean = false,
) {
    val shape = RoundedCornerShape(20.dp)
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick,
            ),
        shape = shape,
        color = containerColor,
        shadowElevation = PatientDimens.homeActionCardElevation,
        tonalElevation = 0.dp,
    ) {
        if (centeredContent) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 20.dp),
            ) {
                Column(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .fillMaxWidth(0.88f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Text(text = emoji, fontSize = PatientDimens.homeMildEmojiSize)
                    Text(
                        text = title,
                        fontSize = PatientDimens.homeActionTitleSize,
                        fontWeight = FontWeight.Bold,
                        color = PatientColors.textPrimary,
                        textAlign = TextAlign.Center,
                    )
                    if (subtitle.isNotBlank()) {
                        Text(
                            text = subtitle,
                            fontSize = PatientDimens.homeActionSubtitleSize,
                            color = PatientColors.textSecondary,
                            lineHeight = PatientDimens.homeNoticeBodyLineHeight,
                            textAlign = TextAlign.Center,
                        )
                    }
                }
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = PatientColors.primary.copy(alpha = 0.45f),
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .size(PatientDimens.homeActionChevronSize),
                )
            }
        } else {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 18.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                Box(
                    modifier = Modifier.size(52.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(text = emoji, fontSize = PatientDimens.homeMildEmojiSize)
                }
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Text(
                        text = title,
                        fontSize = PatientDimens.homeActionTitleSize,
                        fontWeight = FontWeight.Bold,
                        color = PatientColors.textPrimary,
                        textAlign = TextAlign.Center,
                    )
                    if (subtitle.isNotBlank()) {
                        Text(
                            text = subtitle,
                            fontSize = PatientDimens.homeActionSubtitleSize,
                            color = PatientColors.textSecondary,
                            lineHeight = PatientDimens.homeNoticeBodyLineHeight,
                            textAlign = TextAlign.Center,
                        )
                    }
                }
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = PatientColors.primary.copy(alpha = 0.45f),
                    modifier = Modifier.size(PatientDimens.homeActionChevronSize),
                )
            }
        }
    }
}
