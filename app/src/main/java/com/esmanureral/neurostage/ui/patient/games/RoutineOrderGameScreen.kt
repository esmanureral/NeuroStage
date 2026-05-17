package com.esmanureral.neurostage.ui.patient.games

import com.esmanureral.neurostage.ui.theme.PatientColors
import com.esmanureral.neurostage.ui.theme.PatientDimens
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.esmanureral.neurostage.R
import java.util.Locale

private enum class GamePhase { PLAYING, TRY_AGAIN, SUCCESS }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoutineOrderGameScreen(
    stageIndex: Int?,
    onBack: () -> Unit,
) {
    val screenTitle = stringResource(R.string.patient_game_routine_short)
    val count = itemCountForStage(stageIndex)
    val game = remember { routineGames.random() }
    val steps = remember { game.steps.take(count).shuffled() }
    val gameTitle = stringResource(game.titleRes)

    val assignments = remember { mutableStateMapOf<Int, Int>() }
    var nextPos by remember { mutableStateOf(1) }
    var phase by remember { mutableStateOf(GamePhase.PLAYING) }

    fun resetGame() {
        assignments.clear()
        nextPos = 1
        phase = GamePhase.PLAYING
    }

    fun confirmOrder() {
        val correct = steps.all { step -> assignments[step.id] == step.correctPosition }
        phase = if (correct) GamePhase.SUCCESS else GamePhase.TRY_AGAIN
    }

    fun onStepTapped(stepId: Int) {
        if (phase != GamePhase.PLAYING) return
        val existing = assignments[stepId]
        if (existing != null) {
            assignments.remove(stepId)
            val maxAssigned = assignments.values.maxOrNull() ?: 0
            nextPos = maxAssigned + 1
        } else {
            assignments[stepId] = nextPos
            nextPos++
        }
    }

    Scaffold(
        containerColor = PatientColors.gameBackgroundCream,
        topBar = { GameScreenTopBar(title = screenTitle, onBack = onBack) },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = PatientDimens.gameScreenPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            when (phase) {
                GamePhase.SUCCESS -> SuccessView(
                    gameName = gameTitle,
                    onPlayAgain = { resetGame() },
                )

                GamePhase.PLAYING, GamePhase.TRY_AGAIN -> {
                    Text(
                        text = "${stringResource(game.emojiRes)}  ${
                            gameTitle.uppercase(
                                Locale.forLanguageTag(
                                    "tr"
                                )
                            )
                        }",
                        fontSize = PatientDimens.gameTitleSize,
                        fontWeight = FontWeight.Black,
                        color = PatientColors.gameTextPrimary,
                        textAlign = TextAlign.Center,
                    )

                    Spacer(Modifier.height(PatientDimens.gameBlockGapS))

                    InstructionBox(
                        text = stringResource(R.string.routine_instruction),
                        accentColor = PatientColors.routineAccent,
                        backgroundColor = PatientColors.routineAccentLight,
                    )

                    if (phase == GamePhase.TRY_AGAIN) {
                        Spacer(Modifier.height(PatientDimens.gameSuccessTitleGap))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(PatientDimens.gameTryAgainCorner))
                                .background(PatientColors.gameWarningBackground)
                                .padding(PatientDimens.gameTryAgainPadding),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = stringResource(R.string.routine_try_again),
                                fontSize = PatientDimens.gameTryAgainTextSize,
                                fontWeight = FontWeight.Bold,
                                color = PatientColors.gameWarningText,
                                textAlign = TextAlign.Center,
                            )
                        }
                    }

                    Spacer(Modifier.height(PatientDimens.gameBlockGapM))

                    steps.forEach { step ->
                        val assignedPos = assignments[step.id]
                        val isAssigned = assignedPos != null
                        val cardBg by animateColorAsState(
                            targetValue = if (isAssigned) {
                                PatientColors.routineAccent
                            } else {
                                PatientColors.surface
                            },
                            animationSpec = tween(300),
                            label = "cardBg_${step.id}",
                        )
                        val textColor = if (isAssigned) {
                            PatientColors.surface
                        } else {
                            PatientColors.gameTextPrimary
                        }

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = PatientDimens.gameStepRowGapV)
                                .clip(RoundedCornerShape(PatientDimens.gameButtonCorner))
                                .background(cardBg)
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = ripple(bounded = true),
                                    onClick = { onStepTapped(step.id) },
                                )
                                .padding(
                                    horizontal = PatientDimens.gameStepRowPaddingH,
                                    vertical = PatientDimens.gameStepRowPaddingV,
                                ),
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier
                                        .size(PatientDimens.gameStepBadgeSize)
                                        .clip(CircleShape)
                                        .background(
                                            if (isAssigned) {
                                                PatientColors.surface.copy(alpha = 0.25f)
                                            } else {
                                                PatientColors.routineAccentLight
                                            },
                                        ),
                                ) {
                                    Text(
                                        text = if (isAssigned) {
                                            "$assignedPos"
                                        } else {
                                            stringResource(R.string.routine_position_unknown)
                                        },
                                        fontSize = PatientDimens.gameStepLabelSize,
                                        fontWeight = FontWeight.Black,
                                        color = if (isAssigned) {
                                            PatientColors.surface
                                        } else {
                                            PatientColors.routineAccent
                                        },
                                    )
                                }
                                Spacer(Modifier.width(PatientDimens.gameStepIconGap))
                                Text(
                                    text = stringResource(step.emojiRes),
                                    fontSize = PatientDimens.gameStepEmojiSize
                                )
                                Spacer(Modifier.width(PatientDimens.gameStepEmojiGap))
                                Text(
                                    text = stringResource(step.labelRes),
                                    fontSize = PatientDimens.gameStepLabelSize,
                                    fontWeight = FontWeight.Bold,
                                    color = textColor,
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(PatientDimens.gameBlockGapM))

                    if (assignments.size == steps.size) {
                        PrimaryGameButton(
                            text = stringResource(R.string.routine_btn_confirm),
                            containerColor = PatientColors.routineAccent,
                            onClick = { confirmOrder() },
                        )
                    } else {
                        Text(
                            text = stringResource(
                                R.string.routine_steps_count,
                                assignments.size,
                                steps.size,
                            ),
                            fontSize = PatientDimens.gameCountTextSize,
                            color = PatientColors.gameTextMuted,
                            textAlign = TextAlign.Center,
                        )
                    }

                    if (phase == GamePhase.TRY_AGAIN) {
                        Spacer(Modifier.height(PatientDimens.gameSuccessTitleGap))
                        Button(
                            onClick = { resetGame() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = PatientColors.gameWarningText,
                            ),
                            shape = RoundedCornerShape(PatientDimens.gameButtonCorner),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(PatientDimens.gameSecondaryButtonHeight),
                        ) {
                            Text(
                                text = stringResource(R.string.routine_btn_restart),
                                fontSize = PatientDimens.gameButtonTextSize,
                                fontWeight = FontWeight.Black,
                                color = PatientColors.surface,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SuccessView(
    gameName: String,
    onPlayAgain: () -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize(),
    ) {
        Text(
            text = stringResource(R.string.routine_success_emoji),
            fontSize = PatientDimens.gameSuccessIconSize,
        )
        Spacer(Modifier.height(PatientDimens.gameSuccessSectionGap))
        Text(
            text = stringResource(R.string.routine_success_title),
            fontSize = PatientDimens.gameSuccessTitleSize,
            fontWeight = FontWeight.Black,
            color = PatientColors.gameSuccess,
        )
        Spacer(Modifier.height(PatientDimens.gameSuccessTitleGap))
        Text(
            text = stringResource(R.string.routine_success_body, gameName),
            fontSize = PatientDimens.gameStepLabelSize,
            color = PatientColors.gameTextPrimary,
            textAlign = TextAlign.Center,
            lineHeight = PatientDimens.gameSuccessBodyLineHeight,
        )
        Spacer(Modifier.height(PatientDimens.gameSuccessActionsTopGap))
        PrimaryGameButton(
            text = stringResource(R.string.routine_btn_play_again),
            containerColor = PatientColors.gameSuccess,
            onClick = onPlayAgain,
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .height(PatientDimens.gameSecondaryButtonHeight),
        )
    }
}
