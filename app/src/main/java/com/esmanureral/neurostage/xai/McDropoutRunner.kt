package com.esmanureral.neurostage.xai

import android.content.Context
import android.graphics.Bitmap
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.channels.FileChannel
import kotlin.math.cos
import kotlin.math.ln
import kotlin.math.PI
import kotlin.math.sqrt
import kotlin.random.Random
import com.esmanureral.neurostage.util.Constants
import androidx.core.graphics.scale

class McDropoutRunner(context: Context) {

    private val interpreter: Interpreter = run {
        val afd = context.assets.openFd(Constants.Model.ALZHEIMER_FILE_NAME)
        val channel = FileInputStream(afd.fileDescriptor).channel
        val buffer = channel.map(FileChannel.MapMode.READ_ONLY, afd.startOffset, afd.declaredLength)
        val options = Interpreter.Options().apply { numThreads = 1 }
        Interpreter(buffer, options)
    }

    fun run(bitmap: Bitmap, numSamples: Int = 10): McDropoutResult {
        val pixels = extractPixels(bitmap)
        val allSamples = Array(numSamples) { FloatArray(4) }

        repeat(numSamples) { i ->
            val buffer = buildNoisyBuffer(pixels)
            val output = Array(1) { FloatArray(4) }
            interpreter.run(buffer, output)
            allSamples[i] = output[0]
        }

        val meanScores = FloatArray(4) { cls ->
            allSamples.sumOf { it[cls].toDouble() }.toFloat() / numSamples
        }

        val stdScores = FloatArray(4) { cls ->
            val mean = meanScores[cls]
            val variance = allSamples.sumOf { sample ->
                val diff = (sample[cls] - mean).toDouble()
                diff * diff
            }.toFloat() / numSamples
            sqrt(variance)
        }

        val topClass = meanScores.indices.maxByOrNull { meanScores[it] } ?: 0

        return McDropoutResult(
            meanScores = meanScores,
            stdScores = stdScores,
            topMean = meanScores[topClass],
            topStd = stdScores[topClass],
        )
    }

    private fun extractPixels(bitmap: Bitmap): IntArray {
        val scaled = bitmap.scale(INPUT_SIZE, INPUT_SIZE)
        val pixels = IntArray(INPUT_SIZE * INPUT_SIZE)
        scaled.getPixels(pixels, 0, INPUT_SIZE, 0, 0, INPUT_SIZE, INPUT_SIZE)
        return pixels
    }

    private fun buildNoisyBuffer(pixels: IntArray): ByteBuffer {
        val buffer = ByteBuffer.allocateDirect(1 * INPUT_SIZE * INPUT_SIZE * 3 * 4)
        buffer.order(ByteOrder.nativeOrder())
        for (pixel in pixels) {
            val r = ((pixel shr 16) and 0xFF).toFloat() + gaussian()
            val g = ((pixel shr 8) and 0xFF).toFloat() + gaussian()
            val b = (pixel and 0xFF).toFloat() + gaussian()
            buffer.putFloat(r.coerceIn(0f, 255f))
            buffer.putFloat(g.coerceIn(0f, 255f))
            buffer.putFloat(b.coerceIn(0f, 255f))
        }
        return buffer
    }

    private fun gaussian(): Float {
        val u1 = Random.nextFloat().coerceAtLeast(1e-7f)
        val u2 = Random.nextFloat()
        val z = sqrt(-2.0 * ln(u1.toDouble())) *
                cos(2.0 * PI * u2)
        return (z * NOISE_STD).toFloat()
    }

    fun close() = interpreter.close()

    companion object {
        private const val INPUT_SIZE = 260
        private const val NOISE_STD = 8f
    }
}