package com.esmanureral.neurostage.xai

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.channels.FileChannel
import com.esmanureral.neurostage.util.Constants
import kotlin.random.Random
import androidx.core.graphics.scale
import androidx.core.graphics.createBitmap
import androidx.core.graphics.set

class GradCamRunner(private val context: Context) {

    private val interpreter: Interpreter = run {
        val afd = context.assets.openFd(Constants.Model.ALZHEIMER_FILE_NAME)
        val ch = FileInputStream(afd.fileDescriptor).channel
        val buf = ch.map(FileChannel.MapMode.READ_ONLY, afd.startOffset, afd.declaredLength)
        val opt = Interpreter.Options().apply {
            numThreads = 1
            useNNAPI = false
        }
        Interpreter(buf, opt)
    }

    fun run(bitmap: Bitmap, stageIndex: Int): GradCamResult {
        val scaled = bitmap.scale(INPUT_SIZE, INPUT_SIZE)
        val pixels = IntArray(INPUT_SIZE * INPUT_SIZE)
        scaled.getPixels(pixels, 0, INPUT_SIZE, 0, 0, INPUT_SIZE, INPUT_SIZE)

        var rSum = 0L
        var gSum = 0L
        var bSum = 0L
        for (p in pixels) {
            rSum += (p shr 16) and 0xFF
            gSum += (p shr 8) and 0xFF
            bSum += p and 0xFF
        }
        val rMean = (rSum / pixels.size).toFloat()
        val gMean = (gSum / pixels.size).toFloat()
        val bMean = (bSum / pixels.size).toFloat()

        val saliency = FloatArray(GRID * GRID)

        repeat(NUM_SAMPLES) {
            val gridMask = BooleanArray(GRID * GRID) { Random.nextFloat() > MASK_PROB }
            val fullMask = upsampleMask(gridMask)
            val buffer = ByteBuffer.allocateDirect(1 * INPUT_SIZE * INPUT_SIZE * 3 * 4)
            buffer.order(ByteOrder.nativeOrder())
            for (idx in pixels.indices) {
                val m = fullMask[idx]
                val p = pixels[idx]
                buffer.putFloat(if (m) ((p shr 16) and 0xFF).toFloat() else rMean)
                buffer.putFloat(if (m) ((p shr 8) and 0xFF).toFloat() else gMean)
                buffer.putFloat(if (m) (p and 0xFF).toFloat() else bMean)
            }

            val output = Array(1) { FloatArray(4) }
            interpreter.run(buffer, output)
            val score = output[0][stageIndex].coerceAtLeast(0f)

            for (i in saliency.indices) {
                if (gridMask[i]) saliency[i] += score
            }
        }

        val maxVal = saliency.max().let { if (it > 0f) it else 1f }
        for (i in saliency.indices) saliency[i] /= maxVal

        val blurred = boxBlur(saliency)

        val minB = blurred.min()
        val maxB = blurred.max().let { if (it > minB) it else minB + 1e-6f }
        for (i in blurred.indices) {
            blurred[i] = ((blurred[i] - minB) / (maxB - minB))
                .coerceIn(0f, 1f)
                .let { it * it }
        }

        val activeRegion = detectActiveRegion(blurred)
        val heatmapBitmap = renderOverlay(scaled, blurred)

        return GradCamResult(
            heatmapBitmap = heatmapBitmap,
            activeRegion = activeRegion,
            peakActivation = blurred.maxOrNull() ?: 0f,
            rawCam = blurred,
        )
    }

    private fun boxBlur(src: FloatArray): FloatArray {
        val size = GRID
        val radius = 2
        val dst = FloatArray(size * size)
        for (y in 0 until size) {
            for (x in 0 until size) {
                var sum = 0f
                var count = 0
                for (dy in -radius..radius) {
                    for (dx in -radius..radius) {
                        val ny = (y + dy).coerceIn(0, size - 1)
                        val nx = (x + dx).coerceIn(0, size - 1)
                        sum += src[ny * size + nx]
                        count++
                    }
                }
                dst[y * size + x] = sum / count
            }
        }
        return dst
    }

    private fun upsampleMask(gridMask: BooleanArray): BooleanArray {
        val full = BooleanArray(INPUT_SIZE * INPUT_SIZE)
        val scale = INPUT_SIZE.toFloat() / GRID
        for (gy in 0 until GRID) {
            for (gx in 0 until GRID) {
                val on = gridMask[gy * GRID + gx]
                val pyStart = (gy * scale).toInt()
                val pyEnd = ((gy + 1) * scale).toInt().coerceAtMost(INPUT_SIZE)
                val pxStart = (gx * scale).toInt()
                val pxEnd = ((gx + 1) * scale).toInt().coerceAtMost(INPUT_SIZE)
                for (py in pyStart until pyEnd)
                    for (px in pxStart until pxEnd)
                        full[py * INPUT_SIZE + px] = on
            }
        }
        return full
    }

    private fun detectActiveRegion(cam: FloatArray): String {
        val peakIdx = cam.indices.maxByOrNull { cam[it] }
            ?: return context.getString(com.esmanureral.neurostage.R.string.brain_region_unknown)
        val row = peakIdx / GRID
        val col = peakIdx % GRID
        val t = GRID / 3
        return when {
            row < t -> when {
                col < t -> context.getString(com.esmanureral.neurostage.R.string.brain_region_left_frontal)
                col > GRID - t -> context.getString(com.esmanureral.neurostage.R.string.brain_region_right_frontal)
                else -> context.getString(com.esmanureral.neurostage.R.string.brain_region_prefrontal)
            }

            row <= GRID - t -> when {
                col < t -> context.getString(com.esmanureral.neurostage.R.string.brain_region_left_temporal)
                col > GRID - t -> context.getString(com.esmanureral.neurostage.R.string.brain_region_right_temporal)
                else -> context.getString(com.esmanureral.neurostage.R.string.brain_region_hippocampal)
            }

            else -> when {
                col < t -> context.getString(com.esmanureral.neurostage.R.string.brain_region_left_parietal)
                col > GRID - t -> context.getString(com.esmanureral.neurostage.R.string.brain_region_right_parietal)
                else -> context.getString(com.esmanureral.neurostage.R.string.brain_region_parietal)
            }
        }
    }

    private fun renderOverlay(scaledOriginal: Bitmap, saliency: FloatArray): Bitmap {
        val camBitmap = createBitmap(GRID, GRID)
        for (i in 0 until GRID) for (j in 0 until GRID)
            camBitmap[j, i] = jetColor(saliency[i * GRID + j])

        val scaledCam = camBitmap.scale(INPUT_SIZE, INPUT_SIZE)
        val result = scaledOriginal.copy(Bitmap.Config.ARGB_8888, true)
        Canvas(result).drawBitmap(scaledCam, 0f, 0f, Paint().apply { alpha = 140 })
        camBitmap.recycle()
        scaledCam.recycle()
        return result
    }

    private fun jetColor(t: Float): Int {
        val r = when {
            t < 0.375f -> 0f
            t < 0.625f -> (t - 0.375f) / 0.25f
            else -> 1f
        }.coerceIn(0f, 1f)
        val g = when {
            t < 0.125f -> 0f
            t < 0.375f -> (t - 0.125f) / 0.25f
            t < 0.625f -> 1f
            t < 0.875f -> 1f - (t - 0.625f) / 0.25f
            else -> 0f
        }.coerceIn(0f, 1f)
        val b = when {
            t < 0.125f -> 0.5f + t * 4f
            t < 0.375f -> 1f
            t < 0.625f -> 1f - (t - 0.375f) / 0.25f
            else -> 0f
        }.coerceIn(0f, 1f)
        return Color.argb(255, (r * 255).toInt(), (g * 255).toInt(), (b * 255).toInt())
    }

    fun close() = interpreter.close()

    companion object {
        private const val INPUT_SIZE = 260
        private const val GRID = 16
        private const val NUM_SAMPLES = 25
        private const val MASK_PROB = 0.5f
    }
}