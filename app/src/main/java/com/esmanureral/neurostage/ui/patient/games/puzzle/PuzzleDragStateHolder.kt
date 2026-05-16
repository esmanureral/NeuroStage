package com.esmanureral.neurostage.ui.patient.games.puzzle

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset

@Stable
class PuzzleDragStateHolder {

    val slotCenters = mutableStateMapOf<Int, Offset>()
    val pieceRoots = mutableStateMapOf<Int, Offset>()

    var cellWidthPx by mutableFloatStateOf(0f)
    var cellHeightPx by mutableFloatStateOf(0f)
    var draggingId by mutableIntStateOf(PuzzleLayoutMetrics.NO_DRAG_PIECE_ID)
    var dragFingerRoot by mutableStateOf(Offset.Zero)
    var dragGrabOffset by mutableStateOf(Offset.Zero)
    var nearCorrectSlot by mutableStateOf(false)
    var dragAreaOrigin by mutableStateOf(Offset.Zero)
    var boardRoot by mutableStateOf(Offset.Zero)

    fun refreshSlotCenters(slotCount: Int, gridCols: Int) {
        if (cellWidthPx <= 0f || cellHeightPx <= 0f) return
        val centers = PuzzleMagnetCalculator.populateSlotCenters(
            boardRoot = boardRoot,
            slotCount = slotCount,
            gridCols = gridCols,
            cellWidthPx = cellWidthPx,
            cellHeightPx = cellHeightPx,
        )
        slotCenters.clear()
        slotCenters.putAll(centers)
    }

    fun beginDrag(
        pieceId: Int,
        touchInPiece: Offset,
        visualWidthPx: Float,
        visualHeightPx: Float,
    ) {
        draggingId = pieceId
        val topLeft = pieceRoots[pieceId] ?: Offset.Zero
        dragGrabOffset = PuzzleMagnetCalculator.grabOffset(
            touchInPiece = touchInPiece,
            visualWidthPx = visualWidthPx,
            visualHeightPx = visualHeightPx,
            cellWidthPx = cellWidthPx,
            cellHeightPx = cellHeightPx,
        )
        dragFingerRoot = topLeft + dragGrabOffset
    }

    fun moveDrag(
        dragAmount: Offset,
        pieceId: Int,
        pieces: List<PuzzlePiece>,
        snapRadiusFraction: Float
    ) {
        dragFingerRoot += dragAmount
        updateMagnetHint(pieceId, pieces, snapRadiusFraction)
    }

    fun endDrag(
        pieceId: Int,
        viewModel: PuzzleGameViewModel,
        snapRadiusFraction: Float,
        onSnapped: () -> Unit,
    ) {
        val snapRadiusPx = PuzzleMagnetCalculator.snapRadiusPx(
            cellWidthPx = cellWidthPx,
            cellHeightPx = cellHeightPx,
            radiusFraction = snapRadiusFraction,
        )
        val pieceCenter = PuzzleMagnetCalculator.pieceCenterFromDrag(
            fingerRoot = dragFingerRoot,
            grabOffset = dragGrabOffset,
            cellWidthPx = cellWidthPx,
            cellHeightPx = cellHeightPx,
        )
        val snapped = viewModel.tryMagneticSnap(
            pieceId = pieceId,
            fingerRoot = dragFingerRoot,
            pieceCenterRoot = pieceCenter,
            slotCenters = slotCenters,
            snapRadiusPx = snapRadiusPx,
        )
        if (snapped) onSnapped()
        clearDrag()
    }

    fun cancelDrag() {
        clearDrag()
    }

    private fun updateMagnetHint(
        pieceId: Int,
        pieces: List<PuzzlePiece>,
        snapRadiusFraction: Float
    ) {
        if (pieceId < 0) {
            nearCorrectSlot = false
            return
        }
        val correctSlot = pieces.firstOrNull { it.id == pieceId }?.correctSlot ?: run {
            nearCorrectSlot = false
            return
        }
        val center = slotCenters[correctSlot] ?: run {
            nearCorrectSlot = false
            return
        }
        val snapRadiusPx = PuzzleMagnetCalculator.snapRadiusPx(
            cellWidthPx = cellWidthPx,
            cellHeightPx = cellHeightPx,
            radiusFraction = snapRadiusFraction,
        )
        nearCorrectSlot = PuzzleMagnetCalculator.isNearCorrectSlot(
            fingerRoot = dragFingerRoot,
            correctSlotCenter = center,
            snapRadiusPx = snapRadiusPx,
        )
    }

    private fun clearDrag() {
        draggingId = PuzzleLayoutMetrics.NO_DRAG_PIECE_ID
        nearCorrectSlot = false
    }
}