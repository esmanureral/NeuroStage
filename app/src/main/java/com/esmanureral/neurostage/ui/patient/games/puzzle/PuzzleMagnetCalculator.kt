package com.esmanureral.neurostage.ui.patient.games.puzzle

import androidx.compose.ui.geometry.Offset
import kotlin.math.min
import kotlin.math.sqrt

object PuzzleMagnetCalculator {

    fun snapRadiusPx(cellWidthPx: Float, cellHeightPx: Float, radiusFraction: Float): Float {
        if (cellWidthPx <= 0f || cellHeightPx <= 0f) return 0f
        return min(cellWidthPx, cellHeightPx) * radiusFraction
    }

    fun slotCenter(
        boardRoot: Offset,
        slot: Int,
        gridCols: Int,
        cellWidthPx: Float,
        cellHeightPx: Float,
    ): Offset {
        val row = slot / gridCols
        val col = slot % gridCols
        return Offset(
            x = boardRoot.x + (col + 0.5f) * cellWidthPx,
            y = boardRoot.y + (row + 0.5f) * cellHeightPx,
        )
    }

    fun populateSlotCenters(
        boardRoot: Offset,
        slotCount: Int,
        gridCols: Int,
        cellWidthPx: Float,
        cellHeightPx: Float,
    ): Map<Int, Offset> = buildMap {
        for (slot in 0 until slotCount) {
            put(
                slot,
                slotCenter(boardRoot, slot, gridCols, cellWidthPx, cellHeightPx),
            )
        }
    }

    fun isNearCorrectSlot(
        fingerRoot: Offset,
        correctSlotCenter: Offset,
        snapRadiusPx: Float,
    ): Boolean {
        if (snapRadiusPx <= 0f) return false
        val dx = fingerRoot.x - correctSlotCenter.x
        val dy = fingerRoot.y - correctSlotCenter.y
        return sqrt(dx * dx + dy * dy) <= snapRadiusPx
    }

    fun grabOffset(
        touchInPiece: Offset,
        visualWidthPx: Float,
        visualHeightPx: Float,
        cellWidthPx: Float,
        cellHeightPx: Float,
    ): Offset {
        val scaleX = scaleFactor(visualWidthPx, cellWidthPx)
        val scaleY = scaleFactor(visualHeightPx, cellHeightPx)
        return Offset(touchInPiece.x * scaleX, touchInPiece.y * scaleY)
    }

    fun pieceCenterFromDrag(
        fingerRoot: Offset,
        grabOffset: Offset,
        cellWidthPx: Float,
        cellHeightPx: Float,
    ): Offset {
        val topLeft = fingerRoot - grabOffset
        return Offset(
            x = topLeft.x + cellWidthPx / 2f,
            y = topLeft.y + cellHeightPx / 2f,
        )
    }

    private fun scaleFactor(visualPx: Float, cellPx: Float): Float =
        if (visualPx > 0f && cellPx > 0f) cellPx / visualPx else 1f
}
