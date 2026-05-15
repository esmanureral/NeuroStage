package com.esmanureral.neurostage.ui.patient.games.puzzle

import androidx.compose.ui.unit.Dp

data class TrayPieceSize(val width: Dp, val height: Dp)

fun trayPieceSizeFor(
    maxRowWidth: Dp,
    cellWidthPx: Float,
    cellHeightPx: Float,
    columns: Int,
    pieceCount: Int,
    gap: Dp,
    minWidthFourPieces: Dp,
    minWidthSixPlusPieces: Dp,
    maxHeight: Dp,
    fallbackSize: Dp,
): TrayPieceSize {
    if (columns <= 0) return TrayPieceSize(fallbackSize, fallbackSize)

    val slotWidth = (maxRowWidth - gap * (columns - 1)) / columns
    val aspect = if (cellWidthPx > 0f && cellHeightPx > 0f) {
        cellWidthPx / cellHeightPx
    } else {
        1f
    }
    val minWidth = if (pieceCount <= 4) minWidthFourPieces else minWidthSixPlusPieces

    var width = slotWidth * PuzzleLayoutConfig.TRAY_SCALE_OF_SLOT
    var height = width / aspect

    if (width < minWidth) {
        width = minOf(minWidth, slotWidth)
        height = width / aspect
    }
    if (height > maxHeight) {
        height = maxHeight
        width = minOf(height * aspect, slotWidth)
    }
    return TrayPieceSize(width, height)
}
