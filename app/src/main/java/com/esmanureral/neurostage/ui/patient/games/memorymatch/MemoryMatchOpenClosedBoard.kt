package com.esmanureral.neurostage.ui.patient.games.memorymatch

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import com.esmanureral.neurostage.ui.theme.PatientColors
import com.esmanureral.neurostage.ui.theme.PatientDimens

@Composable
fun MemoryMatchOpenClosedLayout(
    board: OpenClosedMatchBoard,
    matchedClosedIds: Set<Int>,
    revealedClosedId: Int?,
    highlightClosedId: Int?,
    highlightOpenId: Int?,
    highlight: MemoryMatchHighlight,
    locked: Boolean,
    emojiSize: TextUnit,
    anchorEmojiSize: TextUnit,
    boardPadding: Dp,
    gridGap: Dp,
    highContrast: Boolean,
    onClosedClick: (MemoryMatchCard) -> Unit,
    onOpenClick: (MemoryMatchCard) -> Unit,
) {
    val boardBackground = if (highContrast) Color.White else PatientColors.matchBoardBackground
    val visibleClosed = board.closedPool.filter { it.instanceId !in matchedClosedIds }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(PatientDimens.memoryMatchBoardCorner))
            .background(boardBackground)
            .padding(boardPadding),
        verticalArrangement = Arrangement.spacedBy(gridGap),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(gridGap),
        ) {
            board.openAnchors.forEach { anchor ->
                val anchorHighlight = when {
                    highlightOpenId == anchor.instanceId -> highlight
                    else -> MemoryMatchHighlight.None
                }
                MemoryMatchAnchorCard(
                    card = anchor,
                    emojiSize = anchorEmojiSize,
                    highlight = anchorHighlight,
                    highContrast = highContrast,
                    enabled = !locked && revealedClosedId != null,
                    onClick = { onOpenClick(anchor) },
                    modifier = Modifier.weight(1f),
                )
            }
        }

        Spacer(Modifier.height(gridGap))

        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            horizontalArrangement = Arrangement.spacedBy(gridGap),
            verticalArrangement = Arrangement.spacedBy(gridGap),
            modifier = Modifier.fillMaxWidth(),
            userScrollEnabled = false,
        ) {
            items(
                items = visibleClosed,
                key = { it.instanceId },
            ) { card ->
                val faceUp = card.instanceId == revealedClosedId ||
                        card.instanceId == highlightClosedId
                val cardHighlight = when {
                    highlightClosedId == card.instanceId -> highlight
                    else -> MemoryMatchHighlight.None
                }
                MemoryMatchCardView(
                    card = card,
                    faceUp = faceUp,
                    highlight = cardHighlight,
                    emojiSize = emojiSize,
                    wordSize = PatientDimens.memoryMatchCardWordSize,
                    highContrast = highContrast,
                    enabled = !locked && !faceUp,
                    onClick = { onClosedClick(card) },
                )
            }
        }
    }
}
