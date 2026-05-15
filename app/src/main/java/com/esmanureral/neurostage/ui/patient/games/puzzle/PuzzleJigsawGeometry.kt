package com.esmanureral.neurostage.ui.patient.games.puzzle

import androidx.compose.ui.graphics.Path
import kotlin.math.min

internal data class PieceEdges(val top: Int, val right: Int, val bottom: Int, val left: Int)

private fun edges2x2(id: Int): PieceEdges = when (id) {
    0 -> PieceEdges(top = 0, right = 1, bottom = -1, left = 0)
    1 -> PieceEdges(top = 0, right = 0, bottom = 1, left = -1)
    2 -> PieceEdges(top = 1, right = 1, bottom = 0, left = 0)
    3 -> PieceEdges(top = -1, right = 0, bottom = 0, left = -1)
    else -> PieceEdges(0, 0, 0, 0)
}

private fun edgesForGrid(pieceId: Int, rows: Int, cols: Int): PieceEdges {
    val row = pieceId / cols
    val col = pieceId % cols
    return PieceEdges(
        top = if (row == 0) 0 else -verticalBoundary(row - 1, col),
        right = if (col == cols - 1) 0 else horizontalBoundary(row, col, cols),
        bottom = if (row == rows - 1) 0 else verticalBoundary(row, col),
        left = if (col == 0) 0 else -horizontalBoundary(row, col - 1, cols),
    )
}

private fun verticalBoundary(row: Int, col: Int): Int =
    if ((row + col) % 2 == 1) 1 else -1

private fun horizontalBoundary(row: Int, col: Int, cols: Int): Int =
    if ((row * cols + col) % 2 == 0) 1 else -1

private fun pieceEdges(pieceId: Int, rows: Int, cols: Int): PieceEdges =
    if (rows == 2 && cols == 2) edges2x2(pieceId) else edgesForGrid(pieceId, rows, cols)

fun piecePath(w: Float, h: Float, pieceId: Int, rows: Int, cols: Int): Path =
    jigsawPath(w, h, pieceId, rows, cols)

private fun jigsawPath(w: Float, h: Float, pieceId: Int, rows: Int, cols: Int): Path {
    val e = pieceEdges(pieceId, rows, cols)
    val knob = min(w, h) * PuzzleLayoutConfig.JIGSAW_KNOB_FRACTION
    return Path().apply {
        moveTo(0f, 0f)
        edgeTop(this, w, knob, e.top)
        edgeRight(this, w, h, knob, e.right)
        edgeBottom(this, w, h, knob, e.bottom)
        edgeLeft(this, h, knob, e.left)
        close()
    }
}

private fun edgeTop(p: Path, w: Float, knob: Float, type: Int) {
    val mid = w / 2f
    when (type) {
        0 -> p.lineTo(w, 0f)
        1 -> {
            p.lineTo(mid - knob, 0f)
            p.cubicTo(mid - knob * 0.35f, 0f, mid - knob * 0.35f, -knob * 1.15f, mid, -knob * 1.3f)
            p.cubicTo(mid + knob * 0.35f, -knob * 1.15f, mid + knob * 0.35f, 0f, mid + knob, 0f)
            p.lineTo(w, 0f)
        }
        -1 -> {
            p.lineTo(mid - knob, 0f)
            p.cubicTo(mid - knob * 0.35f, 0f, mid - knob * 0.35f, knob * 1.15f, mid, knob * 1.3f)
            p.cubicTo(mid + knob * 0.35f, knob * 1.15f, mid + knob * 0.35f, 0f, mid + knob, 0f)
            p.lineTo(w, 0f)
        }
    }
}

private fun edgeRight(p: Path, w: Float, h: Float, knob: Float, type: Int) {
    val mid = h / 2f
    when (type) {
        0 -> p.lineTo(w, h)
        1 -> {
            p.lineTo(w, mid - knob)
            p.cubicTo(w, mid - knob * 0.35f, w + knob * 1.15f, mid - knob * 0.35f, w + knob * 1.3f, mid)
            p.cubicTo(w + knob * 1.15f, mid + knob * 0.35f, w, mid + knob * 0.35f, w, mid + knob)
            p.lineTo(w, h)
        }
        -1 -> {
            p.lineTo(w, mid - knob)
            p.cubicTo(w, mid - knob * 0.35f, w - knob * 1.15f, mid - knob * 0.35f, w - knob * 1.3f, mid)
            p.cubicTo(w - knob * 1.15f, mid + knob * 0.35f, w, mid + knob * 0.35f, w, mid + knob)
            p.lineTo(w, h)
        }
    }
}

private fun edgeBottom(p: Path, w: Float, h: Float, knob: Float, type: Int) {
    val mid = w / 2f
    when (type) {
        0 -> p.lineTo(0f, h)
        1 -> {
            p.lineTo(mid + knob, h)
            p.cubicTo(mid + knob * 0.35f, h, mid + knob * 0.35f, h + knob * 1.15f, mid, h + knob * 1.3f)
            p.cubicTo(mid - knob * 0.35f, h + knob * 1.15f, mid - knob * 0.35f, h, mid - knob, h)
            p.lineTo(0f, h)
        }
        -1 -> {
            p.lineTo(mid + knob, h)
            p.cubicTo(mid + knob * 0.35f, h, mid + knob * 0.35f, h - knob * 1.15f, mid, h - knob * 1.3f)
            p.cubicTo(mid - knob * 0.35f, h - knob * 1.15f, mid - knob * 0.35f, h, mid - knob, h)
            p.lineTo(0f, h)
        }
    }
}

private fun edgeLeft(p: Path, h: Float, knob: Float, type: Int) {
    val mid = h / 2f
    when (type) {
        0 -> p.lineTo(0f, 0f)
        1 -> {
            p.lineTo(0f, mid + knob)
            p.cubicTo(0f, mid + knob * 0.35f, -knob * 1.15f, mid + knob * 0.35f, -knob * 1.3f, mid)
            p.cubicTo(-knob * 1.15f, mid - knob * 0.35f, 0f, mid - knob * 0.35f, 0f, mid - knob)
            p.lineTo(0f, 0f)
        }
        -1 -> {
            p.lineTo(0f, mid + knob)
            p.cubicTo(0f, mid + knob * 0.35f, knob * 1.15f, mid + knob * 0.35f, knob * 1.3f, mid)
            p.cubicTo(knob * 1.15f, mid - knob * 0.35f, 0f, mid - knob * 0.35f, 0f, mid - knob)
            p.lineTo(0f, 0f)
        }
    }
}
