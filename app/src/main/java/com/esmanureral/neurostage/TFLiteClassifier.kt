package com.esmanureral.neurostage

import android.content.Context
import android.graphics.Bitmap
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

class TFLiteClassifier(
    private val context: Context,
    private val modelFileName: String,
    private val inputSize: Int,
    private val numClasses: Int,
    private val normalize: Boolean = false   // true → piksel/255f, false → ham [0,255]
) {

    private var interpreter: Interpreter = createInterpreter()
    private var isClosed = false

    private fun createInterpreter(): Interpreter = Interpreter(loadModelFile())

    private fun loadModelFile(): MappedByteBuffer {
        val assetFileDescriptor = context.assets.openFd(modelFileName)
        val inputStream = FileInputStream(assetFileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        return fileChannel.map(
            FileChannel.MapMode.READ_ONLY,
            assetFileDescriptor.startOffset,
            assetFileDescriptor.declaredLength
        )
    }

    fun ensureOpen() {
        if (isClosed) {
            interpreter = createInterpreter()
            isClosed = false
        }
    }

    fun classify(bitmap: Bitmap): FloatArray {
        ensureOpen()
        val inputBuffer = preprocessBitmap(bitmap)

        // Modelin gerçek çıktı şeklini oku; numClasses yanlışsa çökmez
        val outputShape = interpreter.getOutputTensor(0).shape() // örn. [1,2] veya [1,1]
        val outputSize = outputShape[outputShape.size - 1]

        val output = Array(1) { FloatArray(outputSize) }
        interpreter.run(inputBuffer, output)
        return output[0]
    }

    private fun preprocessBitmap(bitmap: Bitmap): ByteBuffer {
        val scaled = Bitmap.createScaledBitmap(bitmap, inputSize, inputSize, true)
        val buffer = ByteBuffer.allocateDirect(1 * inputSize * inputSize * 3 * FLOAT_SIZE)
        buffer.order(ByteOrder.nativeOrder())

        val pixels = IntArray(inputSize * inputSize)
        scaled.getPixels(pixels, 0, inputSize, 0, 0, inputSize, inputSize)

        for (pixel in pixels) {
            val r = ((pixel shr 16) and 0xFF).toFloat()
            val g = ((pixel shr 8) and 0xFF).toFloat()
            val b = (pixel and 0xFF).toFloat()
            if (normalize) {
                buffer.putFloat(r / 255f)
                buffer.putFloat(g / 255f)
                buffer.putFloat(b / 255f)
            } else {
                buffer.putFloat(r)
                buffer.putFloat(g)
                buffer.putFloat(b)
            }
        }

        return buffer
    }

    fun close() {
        if (!isClosed) {
            interpreter.close()
            isClosed = true
        }
    }

    companion object {
        private const val FLOAT_SIZE = 4
    }
}