package com.esmanureral.neurostage

import android.graphics.Bitmap

object ImageValidator {

    data class ValidationResult(val isValid: Boolean, val reason: String? = null)

    fun validate(bitmap: Bitmap): ValidationResult {
        val width = bitmap.width
        val height = bitmap.height
        val sampleSize = minOf(width, height)
        val step = maxOf(1, sampleSize / 100)

        var rSum = 0L; var gSum = 0L; var bSum = 0L
        var count = 0

        // Örnekleme: tüm pikselleri taramak yerine grid örnekleme
        var y = 0
        while (y < height) {
            var x = 0
            while (x < width) {
                val pixel = bitmap.getPixel(x, y)
                rSum += (pixel shr 16) and 0xFF
                gSum += (pixel shr 8) and 0xFF
                bSum += pixel and 0xFF
                count++
                x += step
            }
            y += step
        }

        val rMean = rSum.toFloat() / count
        val gMean = gSum.toFloat() / count
        val bMean = bSum.toFloat() / count
        val overallMean = (rMean + gMean + bMean) / 3f

        // 1. Gri tonlamalı kontrol: RGB kanalları birbirine yakın mı?
        val maxChannelDiff = maxOf(
            Math.abs(rMean - gMean),
            Math.abs(gMean - bMean),
            Math.abs(rMean - bMean)
        )
        if (maxChannelDiff > 30f) {
            return ValidationResult(false, "Geçerli bir beyin MRI görüntüsü yükleyin")
        }

        // 2. Ortalama parlaklık kontrolü (beyin MRI: 30-120 arası)
        if (overallMean < 20f || overallMean > 150f) {
            return ValidationResult(false, "Geçerli bir beyin MRI görüntüsü yükleyin")
        }

        // 3. Standart sapma kontrolü
        var varianceSum = 0f
        y = 0
        while (y < height) {
            var x = 0
            while (x < width) {
                val pixel = bitmap.getPixel(x, y)
                val r = ((pixel shr 16) and 0xFF).toFloat()
                val g = ((pixel shr 8) and 0xFF).toFloat()
                val b = (pixel and 0xFF).toFloat()
                val pixelMean = (r + g + b) / 3f
                val diff = pixelMean - overallMean
                varianceSum += diff * diff
                x += step
            }
            y += step
        }
        val std = Math.sqrt((varianceSum / count).toDouble()).toFloat()
        if (std < 25f || std > 110f) {
            return ValidationResult(false, "Geçerli bir beyin MRI görüntüsü yükleyin")
        }

        // 4. Merkez koyu bölge kontrolü (ventrikül / beyin dokusu)
        val cx = width / 2; val cy = height / 2
        val regionSize = minOf(width, height) / 6
        var centerSum = 0L; var centerCount = 0
        for (dy in -regionSize..regionSize step maxOf(1, regionSize / 10)) {
            for (dx in -regionSize..regionSize step maxOf(1, regionSize / 10)) {
                val px = (cx + dx).coerceIn(0, width - 1)
                val py = (cy + dy).coerceIn(0, height - 1)
                val pixel = bitmap.getPixel(px, py)
                val gray = ((pixel shr 16 and 0xFF) + (pixel shr 8 and 0xFF) + (pixel and 0xFF)) / 3
                centerSum += gray
                centerCount++
            }
        }
        val centerMean = centerSum.toFloat() / centerCount
        // Merkez, genel ortalamadan belirgin şekilde parlak olmamalı (renkli fotoğraf filtresi)
        if (centerMean > overallMean * 2.5f) {
            return ValidationResult(false, "Geçerli bir beyin MRI görüntüsü yükleyin")
        }

        return ValidationResult(isValid = true)
    }
}