package com.esmanureral.neurostage.ui.patient.hub

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.outlined.Alarm
import androidx.compose.material.icons.outlined.Psychology
import androidx.compose.material.icons.outlined.Upload
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
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.esmanureral.neurostage.R
import com.esmanureral.neurostage.ui.theme.PatientColors
import com.esmanureral.neurostage.ui.theme.PatientDimens
import java.util.Calendar

@Composable
fun rememberSessionMotivationQuote(): HubMotivationQuote {
    val morning = stringArrayResource(R.array.patient_hub_motivation_quotes_morning)
    val afternoon = stringArrayResource(R.array.patient_hub_motivation_quotes_afternoon)
    return remember(morning, afternoon) {
        HubMotivationQuotes.pickRandom(morning, afternoon)
    }
}

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
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = onBack) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = backContentDescription,
                tint = PatientColors.textPrimary,
            )
        }
    }
}

@Composable
fun ProgramHubMotivationQuoteCard(
    quote: HubMotivationQuote,
    modifier: Modifier = Modifier,
) {
    if (quote.text.isBlank()) return

    val gradientStart = colorResource(R.color.patient_hub_quote_gradient_start)
    val gradientEnd = colorResource(R.color.patient_hub_quote_gradient_end)
    val accent = colorResource(R.color.patient_hub_quote_accent)
    val labelColor = colorResource(R.color.patient_hub_quote_label)
    val shape = RoundedCornerShape(PatientDimens.homeActionCardCorner)

    val periodLabelRes = when (quote.period) {
        HubMotivationPeriod.MORNING -> R.string.patient_hub_quote_label_morning
        HubMotivationPeriod.AFTERNOON -> R.string.patient_hub_quote_label_afternoon
        HubMotivationPeriod.EVENING -> R.string.patient_hub_quote_label_evening
    }

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = shape,
        shadowElevation = PatientDimens.homeActionCardElevation,
        tonalElevation = PatientDimens.homeActionCardElevation,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(gradientStart, gradientEnd),
                    ),
                )
                .border(
                    width = 1.dp,
                    color = accent.copy(alpha = 0.18f),
                    shape = shape,
                ),
        ) {
            Box(
                modifier = Modifier
                    .width(5.dp)
                    .fillMaxHeight()
                    .background(accent.copy(alpha = 0.85f)),
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(
                        horizontal = PatientDimens.homeMildHeaderPadding,
                        vertical = PatientDimens.homeNoticePadding,
                    ),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(PatientDimens.homeMildBodyGap),
            ) {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color.White.copy(alpha = 0.72f),
                ) {
                    Text(
                        text = stringResource(periodLabelRes),
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                        fontSize = PatientDimens.gameHubStageChipTextSize,
                        fontWeight = FontWeight.SemiBold,
                        color = labelColor,
                        textAlign = TextAlign.Center,
                    )
                }
                Text(
                    text = stringResource(R.string.patient_hub_quote_mark),
                    fontSize = PatientDimens.homeMildEmojiSize,
                    color = accent.copy(alpha = 0.35f),
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = quote.text,
                    fontSize = PatientDimens.homeSubtitleTextSize,
                    fontWeight = FontWeight.Medium,
                    fontStyle = FontStyle.Italic,
                    color = PatientColors.textPrimary,
                    textAlign = TextAlign.Center,
                    lineHeight = PatientDimens.homeMildBodyLineHeight,
                )
            }
        }
    }
}

@Composable
fun ProgramHubHeroSection(
    greeting: String,
    motivationQuote: HubMotivationQuote,
    diagnosisLabel: String?,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(PatientDimens.gameHubSectionGap),
    ) {
        ProgramHubMotivationQuoteCard(quote = motivationQuote)

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(PatientDimens.homeMildTitleGap),
        ) {
            Text(
                text = greeting,
                fontSize = PatientDimens.homeMildTitleSize,
                fontWeight = FontWeight.Bold,
                color = PatientColors.textPrimary,
                textAlign = TextAlign.Center,
            )
            if (!diagnosisLabel.isNullOrBlank()) {
                Surface(
                    shape = RoundedCornerShape(PatientDimens.gameHubStageChipCorner),
                    color = PatientColors.primaryLight,
                ) {
                    Text(
                        text = diagnosisLabel,
                        modifier = Modifier.padding(
                            horizontal = PatientDimens.gameHubStageChipPaddingH,
                            vertical = PatientDimens.gameHubStageChipPaddingV,
                        ),
                        fontSize = PatientDimens.gameHubStageChipTextSize,
                        fontWeight = FontWeight.SemiBold,
                        color = PatientColors.primary,
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }
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
        color = PatientColors.textSecondary,
    )
}

@Composable
fun ProgramHubActionTile(
    icon: ImageVector,
    iconBackground: Color,
    iconTint: Color,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick,
            ),
        shape = RoundedCornerShape(PatientDimens.homeActionCardCorner),
        color = PatientColors.surface,
        shadowElevation = PatientDimens.homeActionCardElevation,
        tonalElevation = PatientDimens.homeActionCardElevation,
    ) {
        Row(
            modifier = Modifier.padding(PatientDimens.homeActionRowPadding),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(PatientDimens.homeActionTextGap),
        ) {
            Box(
                modifier = Modifier
                    .size(PatientDimens.homeActionIconBoxSize)
                    .clip(RoundedCornerShape(PatientDimens.homeActionIconCorner))
                    .background(iconBackground),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(PatientDimens.homeActionIconSize),
                )
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                Text(
                    text = title,
                    fontSize = PatientDimens.homeActionTitleSize,
                    fontWeight = FontWeight.SemiBold,
                    color = PatientColors.textPrimary,
                )
                Text(
                    text = subtitle,
                    fontSize = PatientDimens.homeActionSubtitleSize,
                    color = PatientColors.textSecondary,
                    lineHeight = PatientDimens.homeNoticeBodyLineHeight,
                )
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = PatientColors.divider,
                modifier = Modifier.size(PatientDimens.homeActionChevronSize),
            )
        }
    }
}
