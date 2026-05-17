package com.esmanureral.neurostage.ui.patient.games

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.esmanureral.neurostage.R
import com.esmanureral.neurostage.domain.patient.PatientStage
import com.esmanureral.neurostage.ui.theme.PatientColors
import com.esmanureral.neurostage.ui.theme.PatientDimens

private data class HubGameItem(
    val emojiRes: Int,
    val titleRes: Int,
    val style: HubActivityCardStyle,
    val onStart: () -> Unit,
)

@OptIn(ExperimentalMaterial3Api::class)
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
                    style = HubActivityCardStyle(
                        cardBackground = PatientColors.hubColorMatchCardBg,
                        titleColor = PatientColors.hubColorMatchTitle,
                        buttonContainer = PatientColors.hubColorMatchButton,
                        buttonText = PatientColors.hubColorMatchButtonText,
                    ),
                    onStart = onStartColorMatchGame,
                ),
            )
        }
        if (showMemoryMatch) {
            add(
                HubGameItem(
                    emojiRes = R.string.patient_emoji_memory_match,
                    titleRes = R.string.patient_game_memory_match_short,
                    style = HubActivityCardStyle(
                        cardBackground = PatientColors.hubMatchCardBg,
                        titleColor = PatientColors.hubMatchTitle,
                        buttonContainer = PatientColors.hubMatchButton,
                        buttonText = PatientColors.hubMatchButtonText,
                    ),
                    onStart = onStartMemoryMatchGame,
                ),
            )
        }
        add(
            HubGameItem(
                emojiRes = R.string.patient_emoji_puzzle,
                titleRes = R.string.patient_game_puzzle_short,
                style = HubActivityCardStyle(
                    cardBackground = PatientColors.hubPuzzleCardBg,
                    titleColor = PatientColors.hubPuzzleTitle,
                    buttonContainer = PatientColors.hubPuzzleButton,
                    buttonText = PatientColors.hubPuzzleButtonText,
                ),
                onStart = onStartPuzzleGame,
            ),
        )
    }

    Scaffold(
        containerColor = PatientColors.gameBackgroundCream,
        topBar = {
            GameScreenTopBar(
                title = stringResource(R.string.patient_hub_exercises_section_title),
                onBack = onBack,
            )
        },
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = PatientDimens.gameHubScreenPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(
                PatientDimens.gameHubCardGap,
                Alignment.CenterVertically,
            ),
            contentPadding = PaddingValues(
                vertical = PatientDimens.gameHubContentBottomPadding,
            ),
        ) {
            item {
                Text(
                    text = stringResource(R.string.patient_exercise_list_hint),
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 520.dp)
                        .padding(bottom = PatientDimens.gameHubSectionGap),
                    fontSize = PatientDimens.gameHubGreetingSubtitleSize,
                    color = PatientColors.textSecondary,
                    textAlign = TextAlign.Center,
                )
            }
            items(games, key = { it.titleRes }) { game ->
                PlayfulHubActivityCard(
                    emojiRes = game.emojiRes,
                    title = stringResource(game.titleRes),
                    style = game.style,
                    onStart = game.onStart,
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 520.dp),
                )
            }
        }
    }
}
