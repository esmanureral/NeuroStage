package com.esmanureral.neurostage.ui.patient.games.puzzle

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

private const val MAX_PUZZLE_BITMAP_SIDE_PX = 2048

fun loadSquarePuzzleImage(context: Context, resId: Int): ImageBitmap {
    val src = decodePuzzleResource(context, resId)
    val side = min(src.width, src.height)
    val left = (src.width - side) / 2
    val top = (src.height - side) / 2
    val cropped = Bitmap.createBitmap(src, left, top, side, side)
    if (cropped !== src) {
        src.recycle()
    }
    return cropped.asImageBitmap()
}

fun loadRectPuzzleImage(
    context: Context,
    resId: Int,
    cols: Int,
    rows: Int,
): ImageBitmap {
    val src = decodePuzzleResource(context, resId)
    val targetRatio = cols.toFloat() / max(rows, 1)
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
    val cropped = Bitmap.createBitmap(src, left, top, cropW, cropH)
    if (cropped !== src) {
        src.recycle()
    }
    return cropped.asImageBitmap()
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

private fun decodePuzzleResource(context: Context, resId: Int): Bitmap {
    val resources = context.resources
    val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
    BitmapFactory.decodeResource(resources, resId, bounds)
    if (bounds.outWidth <= 0 || bounds.outHeight <= 0) {
        error("Puzzle drawable has invalid size: resId=$resId")
    }

    var sampleSize = 1
    while (
        bounds.outWidth / sampleSize > MAX_PUZZLE_BITMAP_SIDE_PX ||
        bounds.outHeight / sampleSize > MAX_PUZZLE_BITMAP_SIDE_PX
    ) {
        sampleSize *= 2
    }

    val decodeOptions = BitmapFactory.Options().apply {
        inSampleSize = sampleSize
        inPreferredConfig = Bitmap.Config.ARGB_8888
    }
    return BitmapFactory.decodeResource(resources, resId, decodeOptions)
        ?: error("Failed to decode puzzle drawable: resId=$resId")
}

/**
 * Oturum anahtarı değişince önceki yapboz görselini göstermez; yeni görsel hazır olana kadar null döner.
 */
@Composable
fun rememberSessionPuzzleBitmap(
    context: Context,
    sessionKey: String,
    drawableRes: Int,
    rows: Int,
    cols: Int,
): ImageBitmap? {
    var bitmap by remember { mutableStateOf<ImageBitmap?>(null) }
    var readyKey by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(sessionKey, drawableRes, rows, cols) {
        readyKey = null
        val loaded = withContext(Dispatchers.Default) {
            runCatching {
                loadPuzzleImage(context, drawableRes, rows, cols)
            }.getOrElse { error ->
                Log.e("PuzzleImageLoader", "Puzzle image load failed", error)
                ImageBitmap(1, 1)
            }
        }
        bitmap = loaded
        readyKey = sessionKey
    }

    return if (readyKey == sessionKey) bitmap else null
}
