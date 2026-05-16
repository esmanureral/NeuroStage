package com.esmanureral.neurostage.ui.patient

import com.esmanureral.neurostage.ui.theme.PatientColors
import com.esmanureral.neurostage.ui.theme.PatientDimens
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.esmanureral.neurostage.R

@Composable
fun PatientHomeExitDialog(
    visible: Boolean,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    if (!visible) return
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.patient_exit_dialog_title)) },
        text = { Text(stringResource(R.string.patient_exit_dialog_message)) },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(stringResource(R.string.patient_exit_dialog_confirm))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.patient_exit_dialog_cancel))
            }
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatientHomeTopBar(
    uiState: PatientHomeUiState,
    onLogoutClick: () -> Unit,
) {
    TopAppBar(
        title = {
            Column {
                Text(
                    text = if (uiState.useExerciseAppBarTitle) {
                        stringResource(R.string.patient_home_appbar_title)
                    } else {
                        stringResource(R.string.patient_home_title)
                    },
                    fontWeight = FontWeight.SemiBold,
                    fontSize = PatientDimens.homeAppBarTitleSize,
                )
                uiState.stageChip?.let { chip ->
                    Text(
                        text = stringResource(chip.labelRes),
                        fontSize = PatientDimens.homeChipTextSize,
                        color = colorResource(chip.textColorRes),
                        fontWeight = FontWeight.Medium,
                    )
                }
            }
        },
        actions = {
            IconButton(onClick = onLogoutClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Logout,
                    contentDescription = stringResource(R.string.patient_home_cd_logout),
                    tint = PatientColors.textSecondary,
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = PatientColors.surface,
            titleContentColor = PatientColors.textPrimary,
            navigationIconContentColor = PatientColors.textPrimary,
        ),
    )
}

@Composable
fun PatientHomeExerciseSection(
    puzzleCard: PuzzleCardUi?,
    onStartPuzzleGame: () -> Unit,
    onStartRoutineGame: () -> Unit,
    onStartMemoryGame: () -> Unit,
    onStartMemoryMatchGame: () -> Unit,
    onOpenGames: () -> Unit,
) {
    Text(
        text = stringResource(R.string.patient_mild_exercises_section),
        fontSize = PatientDimens.homeExerciseSectionTitleSize,
        fontWeight = FontWeight.SemiBold,
        color = PatientColors.textPrimary,
    )
    Spacer(Modifier.height(PatientDimens.homeExerciseRowGap))

    puzzleCard?.let { card ->
        PatientHomeExerciseCard(
            emoji = stringResource(R.string.patient_emoji_puzzle),
            title = stringResource(R.string.patient_mild_puzzle_title),
            subtitle = stringResource(
                card.subtitleFormatRes,
                card.stepIndexInLevel,
                stringResource(card.stepNameRes),
            ),
            bgColor = PatientColors.puzzleCardBackground,
            textColor = PatientColors.puzzleCardText,
            highlighted = true,
            onClick = onStartPuzzleGame,
        )
        Spacer(Modifier.height(PatientDimens.homeExerciseRowGap))
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(PatientDimens.homeExerciseRowGap),
    ) {
        PatientHomeExerciseCard(
            emoji = stringResource(R.string.patient_emoji_routine),
            title = stringResource(R.string.patient_game_routine_short),
            subtitle = stringResource(R.string.patient_game_routine_sub),
            bgColor = PatientColors.routineCardBackground,
            textColor = PatientColors.routineCardText,
            modifier = Modifier.weight(1f),
            onClick = onStartRoutineGame,
        )
        PatientHomeExerciseCard(
            emoji = stringResource(R.string.patient_emoji_memory),
            title = stringResource(R.string.patient_game_memory_short),
            subtitle = stringResource(R.string.patient_game_memory_sub),
            bgColor = PatientColors.memoryCardBackground,
            textColor = PatientColors.memoryCardText,
            modifier = Modifier.weight(1f),
            onClick = onStartMemoryGame,
        )
    }

    Spacer(Modifier.height(PatientDimens.homeExerciseRowGap))
    PatientHomeExerciseCard(
        emoji = stringResource(R.string.patient_emoji_memory_match),
        title = stringResource(R.string.patient_game_memory_match_short),
        subtitle = stringResource(R.string.patient_game_memory_match_sub),
        bgColor = PatientColors.matchCardBackground,
        textColor = PatientColors.matchCardText,
        onClick = onStartMemoryMatchGame,
    )

    Spacer(Modifier.height(PatientDimens.homeExerciseSectionGap))
    TextButton(onClick = onOpenGames, modifier = Modifier.fillMaxWidth()) {
        Text(
            stringResource(R.string.patient_open_game_hub),
            color = PatientColors.primary,
            fontWeight = FontWeight.Medium,
        )
    }
}

@Composable
fun MildDementiaHeader() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(PatientDimens.homeHeaderCorner))
            .background(PatientColors.mildBackground)
            .padding(PatientDimens.homeMildHeaderPadding),
    ) {
        Text(
            stringResource(R.string.patient_emoji_brain),
            fontSize = PatientDimens.homeMildEmojiSize
        )
        Spacer(Modifier.height(PatientDimens.homeMildTitleGap))
        Text(
            stringResource(R.string.patient_mild_home_title),
            fontSize = PatientDimens.homeMildTitleSize,
            fontWeight = FontWeight.Bold,
            color = PatientColors.mildAccent,
        )
        Spacer(Modifier.height(PatientDimens.homeMildBodyGap))
        Text(
            stringResource(R.string.patient_mild_home_body),
            fontSize = PatientDimens.homeSubtitleTextSize,
            color = PatientColors.mildBodyText,
            lineHeight = PatientDimens.homeMildBodyLineHeight,
        )
    }
}

@Composable
fun NonMildScannedNotice() {
    PatientHomeNoticeCard(
        titleRes = R.string.patient_non_mild_notice_title,
        bodyRes = R.string.patient_non_mild_notice_body,
        titleColor = PatientColors.primary,
        bodyColor = PatientColors.textSecondary,
        backgroundColor = PatientColors.primaryLight,
    )
}

@Composable
fun ModerateStageNotice() {
    PatientHomeNoticeCard(
        titleRes = R.string.patient_moderate_notice_title,
        bodyRes = R.string.patient_moderate_notice_body,
        titleColor = PatientColors.moderateNoticeTitle,
        bodyColor = PatientColors.moderateNoticeBody,
        backgroundColor = PatientColors.moderateNoticeBackground,
    )
}

@Composable
private fun PatientHomeNoticeCard(
    titleRes: Int,
    bodyRes: Int,
    titleColor: Color,
    bodyColor: Color,
    backgroundColor: Color,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(PatientDimens.homeNoticeCorner))
            .background(backgroundColor)
            .padding(PatientDimens.homeNoticePadding),
    ) {
        Text(
            stringResource(titleRes),
            fontWeight = FontWeight.Bold,
            color = titleColor,
            fontSize = PatientDimens.homeActionTitleSize,
        )
        Spacer(Modifier.height(PatientDimens.homeNoticeTitleGap))
        Text(
            stringResource(bodyRes),
            fontSize = PatientDimens.homeActionSubtitleSize,
            color = bodyColor,
            lineHeight = PatientDimens.homeNoticeBodyLineHeight,
        )
    }
}

@Composable
fun PatientHomeExerciseCard(
    emoji: String,
    title: String,
    subtitle: String,
    bgColor: Color,
    textColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier.fillMaxWidth(),
    highlighted: Boolean = false,
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(PatientDimens.homeCardCorner))
            .background(bgColor)
            .then(
                if (highlighted) {
                    Modifier.border(
                        PatientDimens.homeExerciseHighlightBorder,
                        textColor.copy(alpha = 0.35f),
                        RoundedCornerShape(PatientDimens.homeCardCorner),
                    )
                } else {
                    Modifier
                },
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick,
            )
            .padding(PatientDimens.homeExerciseCardPadding),
    ) {
        Column {
            Text(emoji, fontSize = PatientDimens.homeExerciseEmojiSize)
            Spacer(Modifier.height(PatientDimens.homeExerciseEmojiGap))
            Text(
                title,
                fontSize = PatientDimens.homeActionTitleSize,
                fontWeight = FontWeight.SemiBold,
                color = textColor
            )
            Text(
                subtitle,
                fontSize = PatientDimens.homeChipTextSize,
                color = textColor.copy(alpha = 0.75f)
            )
        }
    }
}

@Composable
fun PatientHomeScanActionCard(
    onClick: () -> Unit,
) {
    PatientHomeActionCard(
        icon = Icons.Default.CameraAlt,
        iconBg = PatientColors.primaryLight,
        iconTint = PatientColors.primary,
        title = stringResource(R.string.patient_home_scan_button),
        subtitle = stringResource(R.string.patient_home_scan_subtitle),
        onClick = onClick,
    )
}

@Composable
private fun PatientHomeActionCard(
    icon: ImageVector,
    iconBg: Color,
    iconTint: Color,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
) {
    Surface(
        shape = RoundedCornerShape(PatientDimens.homeActionCardCorner),
        color = PatientColors.surface,
        shadowElevation = PatientDimens.homeActionCardElevation,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick,
            ),
    ) {
        Row(
            Modifier.padding(PatientDimens.homeActionRowPadding),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                Modifier
                    .size(PatientDimens.homeActionIconBoxSize)
                    .clip(RoundedCornerShape(PatientDimens.homeActionIconCorner))
                    .background(iconBg),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(PatientDimens.homeActionIconSize)
                )
            }
            Spacer(Modifier.width(PatientDimens.homeActionTextGap))
            Column(Modifier.weight(1f)) {
                Text(
                    title,
                    fontSize = PatientDimens.homeActionTitleSize,
                    fontWeight = FontWeight.SemiBold,
                    color = PatientColors.textPrimary
                )
                Text(
                    subtitle,
                    fontSize = PatientDimens.homeActionSubtitleSize,
                    color = PatientColors.textSecondary
                )
            }
            Icon(
                Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = PatientColors.divider,
                modifier = Modifier.size(PatientDimens.homeActionChevronSize),
            )
        }
    }
}
