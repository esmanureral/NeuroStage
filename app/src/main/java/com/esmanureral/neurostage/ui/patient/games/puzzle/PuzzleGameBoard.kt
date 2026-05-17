package com.esmanureral.neurostage.ui.patient.games.puzzle

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.zIndex
import com.esmanureral.neurostage.ui.theme.PatientColors
import com.esmanureral.neurostage.ui.theme.PatientDimens

@Composable
fun PuzzleGameBoard(
    puzzleBitmap: ImageBitmap,
    pieces: List<PuzzlePiece>,
    gridRows: Int,
    gridCols: Int,
    slotCount: Int,
    boardAspectRatio: Float,
    dragState: PuzzleDragStateHolder,
    ghostAlpha: Float,
    showGhost: Boolean = true,
    showSlotOutlines: Boolean = true,
    completedBoard: Boolean = false,
    knobFraction: Float,
    borderAnimDurationMs: Int,
    boardPieceZIndex: Float,
    slotStrokeNormalPx: Float,
    slotStrokeMagnetPx: Float,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(boardAspectRatio)
            .background(
                PatientColors.puzzleBoardBackground,
                RoundedCornerShape(PatientDimens.puzzleBoardCorner),
            )
            .border(
                PatientDimens.puzzleBoardBorder,
                PatientColors.puzzleSlot,
                RoundedCornerShape(PatientDimens.puzzleBoardCorner),
            )
            .onGloballyPositioned { coordinates ->
                dragState.boardRoot = coordinates.positionInRoot()
                dragState.refreshSlotCenters(slotCount, gridCols)
            }
            .onSizeChanged { size ->
                dragState.cellWidthPx = size.width.toFloat() / gridCols
                dragState.cellHeightPx = size.height.toFloat() / gridRows
                dragState.refreshSlotCenters(slotCount, gridCols)
            },
    ) {
        if (dragState.cellWidthPx <= 0f || dragState.cellHeightPx <= 0f) return@Box

        val density = LocalDensity.current
        val pieceWidthDp = with(density) { dragState.cellWidthPx.toDp() }
        val pieceHeightDp = with(density) { dragState.cellHeightPx.toDp() }

        if (showGhost) {
            PuzzleBoardGhostLayer(
                puzzleBitmap = puzzleBitmap,
                ghostAlpha = ghostAlpha,
            )
        }

        if (showSlotOutlines) {
            PuzzleSlotOutlines(
                slotCount = slotCount,
                gridRows = gridRows,
                gridCols = gridCols,
                cellWidthPx = dragState.cellWidthPx,
                cellHeightPx = dragState.cellHeightPx,
                pieceWidthDp = pieceWidthDp,
                pieceHeightDp = pieceHeightDp,
                pieces = pieces,
                draggingId = dragState.draggingId,
                nearCorrectSlot = dragState.nearCorrectSlot,
                knobFraction = knobFraction,
                slotStrokeNormalPx = slotStrokeNormalPx,
                slotStrokeMagnetPx = slotStrokeMagnetPx,
            )
        }

        PuzzlePlacedPieces(
            pieces = pieces,
            gridCols = gridCols,
            cellWidthPx = dragState.cellWidthPx,
            cellHeightPx = dragState.cellHeightPx,
            pieceWidthDp = pieceWidthDp,
            pieceHeightDp = pieceHeightDp,
            draggingId = dragState.draggingId,
            puzzleBitmap = puzzleBitmap,
            gridRows = gridRows,
            knobFraction = knobFraction,
            borderAnimDurationMs = borderAnimDurationMs,
            boardPieceZIndex = boardPieceZIndex,
            completedBoard = completedBoard,
        )
    }
}

@Composable
private fun PuzzleBoardGhostLayer(
    puzzleBitmap: ImageBitmap,
    ghostAlpha: Float,
) {
    Box(
        Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(PatientDimens.puzzleBoardCorner)),
    ) {
        PuzzleBoardGhost(
            fullImage = puzzleBitmap,
            modifier = Modifier
                .fillMaxSize()
                .alpha(ghostAlpha),
        )
    }
}

@Composable
private fun PuzzleSlotOutlines(
    slotCount: Int,
    gridRows: Int,
    gridCols: Int,
    cellWidthPx: Float,
    cellHeightPx: Float,
    pieceWidthDp: Dp,
    pieceHeightDp: Dp,
    pieces: List<PuzzlePiece>,
    draggingId: Int,
    nearCorrectSlot: Boolean,
    knobFraction: Float,
    slotStrokeNormalPx: Float,
    slotStrokeMagnetPx: Float,
) {
    val density = LocalDensity.current
    val slotStrokeColor = PatientColors.puzzleSlotStroke
    val magnetSlotStrokeColor = PatientColors.puzzleMagnetStroke

    for (slot in 0 until slotCount) {
        val (ox, oy) = slotOffsetDp(slot, gridCols, cellWidthPx, cellHeightPx, density)
        val draggingPiece = pieces.firstOrNull { it.id == draggingId }
        val isMagnetTarget =
            draggingId >= 0 && draggingPiece?.correctSlot == slot && nearCorrectSlot

        Box(
            modifier = Modifier
                .size(pieceWidthDp, pieceHeightDp)
                .offset(x = ox, y = oy),
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val path =
                    piecePath(size.width, size.height, slot, gridRows, gridCols, knobFraction)
                drawPath(
                    path = path,
                    color = if (isMagnetTarget) magnetSlotStrokeColor else slotStrokeColor,
                    style = Stroke(
                        width = if (isMagnetTarget) slotStrokeMagnetPx else slotStrokeNormalPx,
                        cap = StrokeCap.Round,
                        join = StrokeJoin.Round,
                    ),
                )
            }
        }
    }
}

@Composable
private fun PuzzlePlacedPieces(
    pieces: List<PuzzlePiece>,
    gridCols: Int,
    cellWidthPx: Float,
    cellHeightPx: Float,
    pieceWidthDp: Dp,
    pieceHeightDp: Dp,
    draggingId: Int,
    puzzleBitmap: ImageBitmap,
    gridRows: Int,
    knobFraction: Float,
    borderAnimDurationMs: Int,
    boardPieceZIndex: Float,
    completedBoard: Boolean = false,
) {
    val density = LocalDensity.current

    for (piece in pieces.filter { it.currentSlot >= 0 && it.id != draggingId }) {
        val (ox, oy) = slotOffsetDp(piece.currentSlot, gridCols, cellWidthPx, cellHeightPx, density)
        val borderCol by animateColorAsState(
            targetValue = if (piece.isPlaced) PatientColors.puzzleSuccess else PatientColors.surface,
            animationSpec = if (completedBoard) snap() else tween(borderAnimDurationMs),
            label = "puzzle_piece_border_${piece.id}",
        )

        Box(
            Modifier
                .size(pieceWidthDp, pieceHeightDp)
                .offset(x = ox, y = oy)
                .graphicsLayer { clip = false }
                .zIndex(boardPieceZIndex),
        ) {
            PuzzlePieceView(
                fullImage = puzzleBitmap,
                pieceId = piece.id,
                rows = gridRows,
                cols = gridCols,
                knobFraction = knobFraction,
                borderColor = borderCol,
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}

private fun slotOffsetDp(
    slot: Int,
    gridCols: Int,
    cellWidthPx: Float,
    cellHeightPx: Float,
    density: androidx.compose.ui.unit.Density,
): Pair<Dp, Dp> {
    val row = slot / gridCols
    val col = slot % gridCols
    return with(density) {
        (col * cellWidthPx).toDp() to (row * cellHeightPx).toDp()
    }
}
