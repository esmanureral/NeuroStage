package com.esmanureral.neurostage.ui.patient.games

import androidx.annotation.StringRes
import com.esmanureral.neurostage.ui.theme.PatientColors
import com.esmanureral.neurostage.ui.theme.PatientDimens
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.esmanureral.neurostage.R
import com.esmanureral.neurostage.domain.patient.PatientStage
import com.esmanureral.neurostage.ui.patient.PatientExerciseTopBar

@Composable
fun GameHubScreen(
    stageIndex: Int?,
    onStartRoutineGame: () -> Unit,
    onStartMemoryGame: () -> Unit,
    onStartPuzzleGame: () -> Unit,
    onBack: () -> Unit,
) {
    val hubSubtitle = if (PatientStage.canAccessPatientExerciseHub(stageIndex)) {
        stringResource(R.string.patient_exercise_hub_subtitle)
    } else {
        null
    }

    Scaffold(
        containerColor = PatientColors.background,
        topBar = {
            PatientExerciseTopBar(
                title = stringResource(R.string.patient_home_appbar_title),
                subtitle = hubSubtitle,
                onBack = onBack,
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = PatientDimens.gameHubScreenPadding),
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = stringResource(R.string.patient_game_hub_pick),
                fontSize = PatientDimens.gameHubPickTextSize,
                color = PatientColors.textSecondary,
            )

            Spacer(Modifier.height(PatientDimens.gameHubSectionGap))

            GameCard(
                emojiRes = R.string.patient_emoji_routine,
                title = stringResource(R.string.patient_game_routine_title),
                subtitle = stringResource(R.string.patient_game_routine_hub_sub),
                bgColor = PatientColors.routineCardBackground,
                accentColor = PatientColors.routineCardText,
                onClick = onStartRoutineGame,
            )

            Spacer(Modifier.height(PatientDimens.gameHubCardGap))

            GameCard(
                emojiRes = R.string.patient_emoji_memory,
                title = stringResource(R.string.patient_game_memory_title),
                subtitle = stringResource(R.string.patient_game_memory_hub_sub),
                bgColor = PatientColors.memoryCardBackground,
                accentColor = PatientColors.memoryCardText,
                onClick = onStartMemoryGame,
            )

            Spacer(Modifier.height(PatientDimens.gameHubCardGap))

            GameCard(
                emojiRes = R.string.patient_emoji_puzzle,
                title = stringResource(R.string.patient_mild_puzzle_title),
                subtitle = stringResource(R.string.patient_game_puzzle_hub_sub),
                bgColor = PatientColors.puzzleCardBackground,
                accentColor = PatientColors.puzzleCardText,
                onClick = onStartPuzzleGame,
            )
        }
    }
}

@Composable
private fun GameCard(
    @StringRes emojiRes: Int,
    title: String,
    subtitle: String,
    bgColor: Color,
    accentColor: Color,
    onClick: () -> Unit,
    enabled: Boolean = true,
) {
    val alpha = if (enabled) 1f else 0.5f

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(PatientDimens.gameHubCardCorner))
            .background(bgColor.copy(alpha = alpha))
            .then(
                if (enabled) {
                    Modifier.clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onClick,
                    )
                } else {
                    Modifier
                },
            )
            .padding(PatientDimens.gameHubCardPadding),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = stringResource(emojiRes), fontSize = PatientDimens.gameHubCardEmojiSize)
            Spacer(Modifier.width(PatientDimens.gameHubCardEmojiGap))
            Column {
                Text(
                    text = title,
                    fontSize = PatientDimens.homeActionTitleSize,
                    fontWeight = FontWeight.SemiBold,
                    color = accentColor.copy(alpha = alpha),
                )
                Spacer(Modifier.height(PatientDimens.gameHubCardSubtitleGap))
                Text(
                    text = subtitle,
                    fontSize = PatientDimens.homeActionSubtitleSize,
                    color = accentColor.copy(alpha = alpha * 0.7f),
                )
            }
        }
    }
}
