package com.esmanureral.neurostage.ui.patient.games.puzzle

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import kotlin.math.roundToInt

@Composable
fun PuzzlePieceView(
    fullImage: ImageBitmap,
    pieceId: Int,
    rows: Int,
    cols: Int,
    modifier: Modifier = Modifier,
    borderColor: Color = Color.White,
    showBorder: Boolean = true,
) {
    val row = pieceId / cols
    val col = pieceId % cols
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val path = piecePath(w, h, pieceId, rows, cols)
        val boardW = (w * cols).roundToInt().coerceAtLeast(1)
        val boardH = (h * rows).roundToInt().coerceAtLeast(1)
        clipPath(path) {
            drawImage(
                image = fullImage,
                dstOffset = IntOffset((-col * w).roundToInt(), (-row * h).roundToInt()),
                dstSize = IntSize(boardW, boardH),
            )
        }
        if (showBorder) {
            drawPath(
                path = path,
                color = borderColor,
                style = Stroke(
                    width = 2.5f,
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round,
                ),
            )
        }
    }
}

@Composable
fun PuzzleBoardGhost(
    fullImage: ImageBitmap,
    modifier: Modifier = Modifier,
) {
    Canvas(modifier = modifier) {
        drawImage(
            image = fullImage,
            dstOffset = IntOffset.Zero,
            dstSize = IntSize(
                size.width.roundToInt().coerceAtLeast(1),
                size.height.roundToInt().coerceAtLeast(1),
            ),
        )
    }
}
