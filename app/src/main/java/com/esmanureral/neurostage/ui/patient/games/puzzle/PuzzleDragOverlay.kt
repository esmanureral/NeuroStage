package com.esmanureral.neurostage.ui.patient.games.puzzle

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.zIndex
import com.esmanureral.neurostage.ui.theme.PatientColors
import com.esmanureral.neurostage.ui.theme.PatientDimens
import kotlin.math.roundToInt

@Composable
fun PuzzleDragOverlay(
    puzzleBitmap: ImageBitmap,
    gridRows: Int,
    gridCols: Int,
    knobFraction: Float,
    dragState: PuzzleDragStateHolder,
    dragOverlayZIndex: Float,
) {
    if (dragState.draggingId < 0) return

    val density = LocalDensity.current
    val dragWidthDp = if (dragState.cellWidthPx > 0f) {
        with(density) { dragState.cellWidthPx.toDp() }
    } else {
        PatientDimens.puzzleDragFallbackSize
    }
    val dragHeightDp = if (dragState.cellHeightPx > 0f) {
        with(density) { dragState.cellHeightPx.toDp() }
    } else {
        PatientDimens.puzzleDragFallbackSize
    }
    val topLeft = dragState.dragFingerRoot - dragState.dragGrabOffset - dragState.dragAreaOrigin

    Box(
        Modifier
            .size(dragWidthDp, dragHeightDp)
            .offset {
                IntOffset(topLeft.x.roundToInt(), topLeft.y.roundToInt())
            }
            .graphicsLayer { clip = false }
            .zIndex(dragOverlayZIndex),
    ) {
        PuzzlePieceView(
            fullImage = puzzleBitmap,
            pieceId = dragState.draggingId,
            rows = gridRows,
            cols = gridCols,
            knobFraction = knobFraction,
            borderColor = if (dragState.nearCorrectSlot) {
                PatientColors.puzzleSuccess
            } else {
                PatientColors.puzzleAccent
            },
            modifier = Modifier.fillMaxSize(),
        )
    }
}