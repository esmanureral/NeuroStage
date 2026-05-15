package com.esmanureral.neurostage.ui.patient.games.puzzle

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

fun loadSquarePuzzleImage(context: Context, resId: Int): ImageBitmap {
    val src = BitmapFactory.decodeResource(context.resources, resId)
    val side = min(src.width, src.height)
    val left = (src.width - side) / 2
    val top = (src.height - side) / 2
    return Bitmap.createBitmap(src, left, top, side, side).asImageBitmap()
}

fun loadRectPuzzleImage(
    context: Context,
    resId: Int,
    cols: Int,
    rows: Int,
): ImageBitmap {
    val src = BitmapFactory.decodeResource(context.resources, resId)
    val targetRatio = cols.toFloat() / rows
    val srcRatio = src.width.toFloat() / max(src.height, 1)
    val cropW: Int
    val cropH: Int
    if (srcRatio > targetRatio) {
        cropH = src.height
        cropW = (src.height * targetRatio).roundToInt().coerceIn(1, src.width)
    } else {
        cropW = src.width
        cropH = (src.width / targetRatio).roundToInt().coerceIn(1, src.height)
    }
    val left = (src.width - cropW) / 2
    val top = (src.height - cropH) / 2
    return Bitmap.createBitmap(src, left, top, cropW, cropH).asImageBitmap()
}

fun loadPuzzleImage(
    context: Context,
    resId: Int,
    rows: Int,
    cols: Int,
): ImageBitmap = if (rows == cols) {
    loadSquarePuzzleImage(context, resId)
} else {
    loadRectPuzzleImage(context, resId, cols, rows)
}
