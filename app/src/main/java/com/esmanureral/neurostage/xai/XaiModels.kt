package com.esmanureral.neurostage.xai

import android.graphics.Bitmap

data class McDropoutResult(
    val meanScores: FloatArray,
    val stdScores: FloatArray,
    val topMean: Float,
    val topStd: Float,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as McDropoutResult

        if (!meanScores.contentEquals(other.meanScores)) return false
        if (!stdScores.contentEquals(other.stdScores)) return false
        if (topMean != other.topMean) return false
        if (topStd != other.topStd) return false

        return true
    }

    override fun hashCode(): Int {
        var result = meanScores.contentHashCode()
        result = 31 * result + stdScores.contentHashCode()
        result = 31 * result + topMean.hashCode()
        result = 31 * result + topStd.hashCode()
        return result
    }
}

data class GradCamResult(
    val heatmapBitmap: Bitmap,
    val activeRegion: String,
    val rawCam: FloatArray,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as GradCamResult

        if (heatmapBitmap != other.heatmapBitmap) return false
        if (activeRegion != other.activeRegion) return false
        if (!rawCam.contentEquals(other.rawCam)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = heatmapBitmap.hashCode()
        result = 31 * result + activeRegion.hashCode()
        result = 31 * result + rawCam.contentHashCode()
        return result
    }
}

data class GeminiReport(val text: String)

data class XaiUiState(
    val mcResult: McDropoutResult? = null,
    val gradCamResult: GradCamResult? = null,
    val geminiReport: GeminiReport? = null,
    val isMcLoading: Boolean = false,
    val isGradCamLoading: Boolean = false,
    val isGeminiLoading: Boolean = false,
    val mcError: String? = null,
    val gradCamError: String? = null,
    val geminiError: String? = null,
)

fun parseAiReportBlocks(
    reportText: String,
    knownHeadings: List<String>
): List<Pair<String?, String>> {
    val lines = reportText.split("\n")
    val blocks = mutableListOf<Pair<String?, String>>()

    var currentTitle: String? = null
    val currentContent = StringBuilder()

    for (line in lines) {
        val trimmed = line.trim()
        val upper = trimmed.uppercase()

        val isHashHeading = trimmed.startsWith("#")
        val isBoldHeading =
            trimmed.startsWith("**") && trimmed.endsWith("**") && trimmed.length < 80
        val isBoldColonHeading =
            trimmed.startsWith("**") && trimmed.endsWith("**:") && trimmed.length < 80

        val isKnownHeading = knownHeadings.any { upper.contains(it) } && trimmed.length < 80

        if (isHashHeading || isBoldHeading || isBoldColonHeading || isKnownHeading) {
            if (currentContent.isNotBlank() || currentTitle != null) {
                blocks.add(currentTitle to currentContent.toString().trim())
                currentContent.clear()
            }
            currentTitle = trimmed
                .replace(Regex("^#+\\s*"), "")
                .replace("**", "")
                .removeSuffix(":")
                .trim()
        } else {
            currentContent.append(trimmed).append("\n")
        }
    }
    if (currentContent.isNotBlank() || currentTitle != null) {
        blocks.add(currentTitle to currentContent.toString().trim())
    }
    return blocks
}