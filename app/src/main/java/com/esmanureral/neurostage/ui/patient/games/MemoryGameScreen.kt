package com.esmanureral.neurostage.ui.patient.games

import com.esmanureral.neurostage.ui.theme.PatientColors
import com.esmanureral.neurostage.ui.theme.PatientDimens
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.esmanureral.neurostage.R

private enum class MemoryPhase { SHOW, RECALL, RESULT }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MemoryGameScreen(
    stageIndex: Int?,
    onBack: () -> Unit,
) {
    val screenTitle = stringResource(R.string.patient_game_memory_short)
    val targetCount = itemCountForStage(stageIndex)
    val distractorCount = 2

    val allItems = remember { memoryItemPool.shuffled() }
    val targets = remember { allItems.take(targetCount) }
    val distractors = remember { allItems.drop(targetCount).take(distractorCount) }
    val recallItems = remember { (targets + distractors).shuffled() }

    var phase by remember { mutableStateOf(MemoryPhase.SHOW) }
    val selectedIds = remember { mutableStateListOf<Int>() }

    fun resetGame() {
        phase = MemoryPhase.SHOW
        selectedIds.clear()
    }

    fun toggleItem(id: Int) {
        if (id in selectedIds) selectedIds.remove(id) else selectedIds.add(id)
    }

    val correctCount = selectedIds.count { id -> targets.any { it.id == id } }

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
                MemoryPhase.SHOW -> {
                    Text(
                        text = stringResource(R.string.memory_game_title),
                        fontSize = PatientDimens.gameTitleSize,
                        fontWeight = FontWeight.Black,
                        color = PatientColors.gameTextPrimary,
                        textAlign = TextAlign.Center,
                    )
                    Spacer(Modifier.height(PatientDimens.gameBlockGapS))
                    InstructionBox(
                        text = stringResource(R.string.memory_show_instruction, targetCount),
                        accentColor = PatientColors.memoryAccent,
                        backgroundColor = PatientColors.memoryAccentLight,
                    )
                    Spacer(Modifier.height(PatientDimens.gameBlockGapL))

                    val showColumns = if (targetCount <= 2) 2 else 3
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(showColumns),
                        horizontalArrangement = Arrangement.spacedBy(PatientDimens.gameGridGap),
                        verticalArrangement = Arrangement.spacedBy(PatientDimens.gameGridGap),
                        modifier = Modifier.fillMaxWidth(),
                        userScrollEnabled = false,
                    ) {
                        items(targets, key = { it.id }) { item ->
                            MemoryItemCard(
                                item = item,
                                isSelected = false,
                                isClickable = false,
                                accentColor = PatientColors.memoryAccent,
                                onClick = {},
                            )
                        }
                    }

                    Spacer(Modifier.height(PatientDimens.gameBlockGapXl))
                    PrimaryGameButton(
                        text = stringResource(R.string.memory_btn_ready),
                        containerColor = PatientColors.memoryAccent,
                        onClick = { phase = MemoryPhase.RECALL },
                    )
                }

                MemoryPhase.RECALL -> {
                    Text(
                        text = stringResource(R.string.memory_recall_title),
                        fontSize = PatientDimens.gameRecallTitleSize,
                        fontWeight = FontWeight.Black,
                        color = PatientColors.gameTextPrimary,
                        textAlign = TextAlign.Center,
                    )
                    Spacer(Modifier.height(PatientDimens.gameBlockGapS))
                    InstructionBox(
                        text = stringResource(R.string.memory_recall_instruction, targetCount),
                        accentColor = PatientColors.memoryAccent,
                        backgroundColor = PatientColors.memoryAccentLight,
                    )
                    Spacer(Modifier.height(PatientDimens.gameBlockGapM))

                    LazyVerticalGrid(
                        columns = GridCells.Fixed(3),
                        horizontalArrangement = Arrangement.spacedBy(PatientDimens.gameGridGapS),
                        verticalArrangement = Arrangement.spacedBy(PatientDimens.gameGridGapS),
                        modifier = Modifier.fillMaxWidth(),
                        userScrollEnabled = false,
                    ) {
                        items(recallItems, key = { it.id }) { item ->
                            MemoryItemCard(
                                item = item,
                                isSelected = item.id in selectedIds,
                                isClickable = true,
                                accentColor = PatientColors.memoryAccent,
                                onClick = { toggleItem(item.id) },
                            )
                        }
                    }

                    Spacer(Modifier.height(PatientDimens.gameBlockGapM))

                    if (selectedIds.size == targetCount) {
                        PrimaryGameButton(
                            text = stringResource(R.string.memory_btn_confirm),
                            containerColor = PatientColors.memoryAccent,
                            onClick = { phase = MemoryPhase.RESULT },
                        )
                    } else {
                        Text(
                            text = stringResource(
                                R.string.memory_selection_count,
                                selectedIds.size,
                                targetCount,
                            ),
                            fontSize = PatientDimens.gameCountTextSize,
                            color = PatientColors.gameTextMuted,
                        )
                    }
                }

                MemoryPhase.RESULT -> {
                    val isGood = correctCount == targetCount
                    val bgColor = if (isGood) {
                        PatientColors.gameSuccessLight
                    } else {
                        PatientColors.gameWarningBackground
                    }
                    val fgColor = if (isGood) {
                        PatientColors.gameSuccess
                    } else {
                        PatientColors.gameWarningText
                    }
                    val emoji = stringResource(
                        if (isGood) R.string.memory_result_emoji_success
                        else R.string.memory_result_emoji_retry,
                    )
                    val headline = if (isGood) {
                        stringResource(R.string.memory_result_perfect)
                    } else {
                        stringResource(
                            R.string.memory_result_partial,
                            correctCount,
                            targetCount,
                        )
                    }

                    Text(text = emoji, fontSize = PatientDimens.gameSuccessEmojiSize)
                    Spacer(Modifier.height(PatientDimens.gameResultEmojiGap))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(PatientDimens.gameButtonCorner))
                            .background(bgColor)
                            .padding(PatientDimens.gameResultPadding),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = headline,
                            fontSize = PatientDimens.gameRecallTitleSize,
                            fontWeight = FontWeight.Black,
                            color = fgColor,
                            textAlign = TextAlign.Center,
                        )
                    }
                    Spacer(Modifier.height(PatientDimens.gameBlockGapXl))
                    PrimaryGameButton(
                        text = stringResource(R.string.memory_btn_play_again),
                        containerColor = PatientColors.memoryAccent,
                        onClick = { resetGame() },
                    )
                }
            }
        }
    }
}

@Composable
private fun MemoryItemCard(
    item: MemoryItem,
    isSelected: Boolean,
    isClickable: Boolean,
    accentColor: Color,
    onClick: () -> Unit,
) {
    val bg = if (isSelected) accentColor else PatientColors.surface
    val textColor = if (isSelected) PatientColors.surface else PatientColors.gameTextPrimary
    val label = stringResource(item.labelRes)

    val clickMod: Modifier = if (isClickable) {
        Modifier.clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = ripple(bounded = true),
            onClick = onClick,
        )
    } else {
        Modifier
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(PatientDimens.gameButtonCorner))
            .background(bg)
            .then(clickMod)
            .padding(vertical = PatientDimens.gameItemCardPaddingV),
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = stringResource(item.emojiRes),
                fontSize = PatientDimens.gameHubCardEmojiSize
            )
            Spacer(Modifier.height(PatientDimens.gameItemLabelGap))
            Text(
                text = label,
                fontSize = PatientDimens.gameItemLabelSize,
                fontWeight = FontWeight.Bold,
                color = textColor,
            )
        }
    }
}