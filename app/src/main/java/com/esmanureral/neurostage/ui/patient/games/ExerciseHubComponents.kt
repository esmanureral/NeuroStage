package com.esmanureral.neurostage.ui.patient.games

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Upload
import androidx.compose.material3.TextButton
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import com.esmanureral.neurostage.R
import com.esmanureral.neurostage.ui.theme.PatientColors
import com.esmanureral.neurostage.ui.theme.PatientDimens

data class HubActivityCardStyle(
    val cardBackground: Color,
    val titleColor: Color,
    val buttonContainer: Color,
    val buttonText: Color,
)

@Composable
fun ExerciseHubGreetingHeader(
    greeting: String,
    diagnosisLabel: String?,
    onBack: () -> Unit,
    backContentDescription: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top,
    ) {
        IconButton(onClick = onBack) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = backContentDescription,
                tint = PatientColors.textPrimary,
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = greeting,
                fontSize = PatientDimens.gameHubGreetingSize,
                fontWeight = FontWeight.Bold,
                color = PatientColors.textPrimary,
            )
            if (!diagnosisLabel.isNullOrBlank()) {
                Spacer(Modifier.height(PatientDimens.gameHubHeaderSubtitleGap))
                Text(
                    text = diagnosisLabel,
                    fontSize = PatientDimens.gameHubGreetingSubtitleSize,
                    fontWeight = FontWeight.SemiBold,
                    color = PatientColors.primary,
                )
            }
        }
    }
}

@Composable
fun ExerciseHubStageInfoBanner(
    stageChipLabel: String?,
    description: String?,
    confidencePercent: Int?,
    modifier: Modifier = Modifier,
) {
    if (stageChipLabel.isNullOrBlank() && description.isNullOrBlank()) return

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(PatientDimens.gameHubSectionGap),
    ) {
        Text(
            text = stringResource(R.string.patient_hub_stage_section_title),
            fontSize = PatientDimens.gameHubPlayfulTitleSize,
            fontWeight = FontWeight.Bold,
            color = PatientColors.textPrimary,
        )
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(PatientDimens.gameHubPlayfulCardCorner),
            color = PatientColors.surface,
            shadowElevation = PatientDimens.gameHubCardElevation,
            tonalElevation = PatientDimens.gameHubCardElevation,
        ) {
            Column(
                modifier = Modifier.padding(PatientDimens.gameHubStageBannerPadding),
                verticalArrangement = Arrangement.spacedBy(PatientDimens.gameHubHeaderSubtitleGap),
            ) {
                if (!stageChipLabel.isNullOrBlank()) {
                    Surface(
                        shape = RoundedCornerShape(PatientDimens.gameHubStageChipCorner),
                        color = PatientColors.primaryLight,
                    ) {
                        Text(
                            text = stageChipLabel,
                            modifier = Modifier.padding(
                                horizontal = PatientDimens.gameHubStageChipPaddingH,
                                vertical = PatientDimens.gameHubStageChipPaddingV,
                            ),
                            fontSize = PatientDimens.gameHubStageChipTextSize,
                            fontWeight = FontWeight.SemiBold,
                            color = PatientColors.primary,
                        )
                    }
                }
                if (!description.isNullOrBlank()) {
                    Text(
                        text = description,
                        fontSize = PatientDimens.gameHubGreetingSubtitleSize,
                        color = PatientColors.textSecondary,
                        lineHeight = PatientDimens.gameHubPlayfulTitleLineHeight,
                    )
                }
                if (confidencePercent != null) {
                    Text(
                        text = stringResource(
                            R.string.patient_scan_history_confidence,
                            confidencePercent,
                        ),
                        fontSize = PatientDimens.gameHubStageChipTextSize,
                        fontWeight = FontWeight.Medium,
                        color = PatientColors.textSecondary,
                    )
                }
            }
        }
    }
}

@Composable
fun ExerciseHubScanSection(
    scanCount: Int,
    onUploadScan: () -> Unit,
    onOpenRecords: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val recordsLabel = if (scanCount > 0) {
        stringResource(R.string.patient_hub_bottom_records_count, scanCount)
    } else {
        stringResource(R.string.patient_hub_bottom_records)
    }

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(PatientDimens.gameHubPlayfulCardCorner),
        color = PatientColors.surface,
        shadowElevation = PatientDimens.gameHubCardElevation,
        tonalElevation = PatientDimens.gameHubCardElevation,
    ) {
        Column(
            modifier = Modifier.padding(PatientDimens.gameHubStageBannerPadding),
            verticalArrangement = Arrangement.spacedBy(PatientDimens.gameHubHeaderSubtitleGap),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(R.string.patient_hub_scan_section_title),
                    fontSize = PatientDimens.gameHubPlayfulTitleSize,
                    fontWeight = FontWeight.Bold,
                    color = PatientColors.textPrimary,
                )
                if (scanCount > 0) {
                    TextButton(onClick = onOpenRecords) {
                        Text(
                            text = recordsLabel,
                            fontSize = PatientDimens.gameHubStageChipTextSize,
                            fontWeight = FontWeight.SemiBold,
                            color = PatientColors.primary,
                        )
                    }
                }
            }
            Button(
                onClick = onUploadScan,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(PatientDimens.gameHubStartButtonHeight),
                shape = RoundedCornerShape(PatientDimens.gameHubStartButtonCorner),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PatientColors.primary,
                    contentColor = PatientColors.surface,
                ),
            ) {
                Icon(
                    imageVector = Icons.Outlined.Upload,
                    contentDescription = null,
                    modifier = Modifier.size(PatientDimens.gameBackIconSize),
                )
                Spacer(Modifier.width(PatientDimens.gameBackIconGap))
                Text(
                    text = stringResource(R.string.patient_hub_bottom_new_scan),
                    fontSize = PatientDimens.gameHubStartButtonTextSize,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}

@Composable
fun PlayfulHubGridCard(
    @StringRes emojiRes: Int,
    title: String,
    style: HubActivityCardStyle,
    onStart: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(PatientDimens.gameHubPlayfulCardCorner)

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(PatientDimens.gameHubGridCardHeight),
        shape = shape,
        color = style.cardBackground,
        shadowElevation = PatientDimens.gameHubCardElevation,
        tonalElevation = PatientDimens.gameHubCardElevation,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(PatientDimens.gameHubPlayfulCardPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = stringResource(emojiRes),
                fontSize = PatientDimens.gameHubGridEmojiSize,
            )
            Text(
                text = title,
                fontSize = PatientDimens.gameHubGridTitleSize,
                fontWeight = FontWeight.Bold,
                color = style.titleColor,
                textAlign = TextAlign.Center,
                maxLines = 2,
                lineHeight = PatientDimens.gameHubPlayfulTitleLineHeight,
            )
            Button(
                onClick = onStart,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(PatientDimens.gameHubStartButtonHeight),
                shape = RoundedCornerShape(PatientDimens.gameHubStartButtonCorner),
                colors = ButtonDefaults.buttonColors(
                    containerColor = style.buttonContainer,
                    contentColor = style.buttonText,
                ),
                contentPadding = ButtonDefaults.ContentPadding,
            ) {
                Text(
                    text = stringResource(R.string.patient_hub_start),
                    fontSize = PatientDimens.gameHubStartButtonTextSize,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}

@Composable
fun PlayfulHubActivityCard(
    @StringRes emojiRes: Int,
    title: String,
    style: HubActivityCardStyle,
    onStart: () -> Unit,
    modifier: Modifier = Modifier,
    cardHeight: Dp = PatientDimens.gameHubPlayfulCardHeight,
) {
    val shape = RoundedCornerShape(PatientDimens.gameHubPlayfulCardCorner)

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(cardHeight),
        shape = shape,
        color = style.cardBackground,
        shadowElevation = PatientDimens.gameHubCardElevation,
        tonalElevation = PatientDimens.gameHubCardElevation,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(PatientDimens.gameHubPlayfulCardPadding),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(PatientDimens.gameHubPlayfulContentGap),
            ) {
                Text(
                    text = title,
                    fontSize = PatientDimens.gameHubPlayfulTitleSize,
                    fontWeight = FontWeight.Bold,
                    color = style.titleColor,
                    lineHeight = PatientDimens.gameHubPlayfulTitleLineHeight,
                )
                Button(
                    onClick = onStart,
                    shape = RoundedCornerShape(PatientDimens.gameHubStartButtonCorner),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = style.buttonContainer,
                        contentColor = style.buttonText,
                    ),
                    contentPadding = ButtonDefaults.ContentPadding,
                    modifier = Modifier.height(PatientDimens.gameHubStartButtonHeight),
                ) {
                    Text(
                        text = stringResource(R.string.patient_hub_start),
                        fontSize = PatientDimens.gameHubStartButtonTextSize,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
            Spacer(Modifier.width(PatientDimens.gameHubPlayfulEmojiGap))
            Box(
                modifier = Modifier.size(PatientDimens.gameHubPlayfulEmojiBox),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = stringResource(emojiRes),
                    fontSize = PatientDimens.gameHubPlayfulEmojiSize,
                )
            }
        }
    }
}
