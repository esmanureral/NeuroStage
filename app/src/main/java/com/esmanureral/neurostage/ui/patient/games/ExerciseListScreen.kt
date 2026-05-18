package com.esmanureral.neurostage.ui.patient.games

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.esmanureral.neurostage.R
import com.esmanureral.neurostage.domain.patient.PatientStage
import com.esmanureral.neurostage.ui.patient.hub.ProgramHubEmojiActionTile
import com.esmanureral.neurostage.ui.patient.hub.ProgramHubScreenBackground
import com.esmanureral.neurostage.ui.patient.hub.ProgramHubSectionLabel
import com.esmanureral.neurostage.ui.patient.hub.ProgramHubTopBar
import com.esmanureral.neurostage.ui.theme.PatientDimens

private data class HubGameItem(
    val emojiRes: Int,
    val titleRes: Int,
    val subtitleRes: Int? = null,
    val cardBackgroundRes: Int,
    val onStart: () -> Unit,
)

@Composable
fun ExerciseListScreen(
    stageIndex: Int?,
    onStartMemoryMatchGame: () -> Unit,
    onStartPuzzleGame: () -> Unit,
    onStartColorMatchGame: () -> Unit,
    onBack: () -> Unit,
) {
    val showMemoryMatch = PatientStage.isBrainExerciseEligible(stageIndex) ||
        stageIndex == PatientStage.MODERATE_DEMENTIA
    val showColorMatch = PatientStage.canAccessPatientExerciseHub(stageIndex)

    val games = buildList {
        if (showColorMatch) {
            add(
                HubGameItem(
                    emojiRes = R.string.patient_emoji_color_match,
                    titleRes = R.string.patient_game_color_match_short,
                    cardBackgroundRes = R.color.patient_hub_color_match_card_bg,
                    onStart = onStartColorMatchGame,
                ),
            )
        }
        if (showMemoryMatch) {
            add(
                HubGameItem(
                    emojiRes = R.string.patient_emoji_memory_match,
                    titleRes = R.string.patient_game_memory_match_short,
                    cardBackgroundRes = R.color.patient_hub_match_card_bg,
                    onStart = onStartMemoryMatchGame,
                ),
            )
        }
        add(
            HubGameItem(
                emojiRes = R.string.patient_emoji_puzzle,
                titleRes = R.string.patient_game_puzzle_short,
                cardBackgroundRes = R.color.patient_hub_puzzle_card_bg,
                onStart = onStartPuzzleGame,
            ),
        )
    }

    ProgramHubScreenBackground {
        Scaffold(
            containerColor = Color.Transparent,
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = PatientDimens.gameHubScreenPadding),
            ) {
                ProgramHubTopBar(
                    onBack = onBack,
                    backContentDescription = stringResource(R.string.patient_exercise_back_home),
                )

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center,
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(PatientDimens.gameHubCardGap),
                    ) {
                        ProgramHubSectionLabel(
                            text = stringResource(R.string.patient_exercise_list_section),
                            modifier = Modifier.fillMaxWidth(0.94f),
                        )
                        games.forEach { game ->
                            ProgramHubEmojiActionTile(
                                containerColor = colorResource(game.cardBackgroundRes),
                                title = stringResource(game.titleRes),
                                subtitle = game.subtitleRes?.let { stringResource(it) }.orEmpty(),
                                emoji = stringResource(game.emojiRes),
                                onClick = game.onStart,
                                centeredContent = true,
                                modifier = Modifier
                                    .fillMaxWidth(0.94f)
                                    .widthIn(max = 400.dp),
                            )
                        }
                    }
                }
            }
        }
    }
}
