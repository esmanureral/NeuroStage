package com.esmanureral.neurostage.xai

import android.graphics.Bitmap

data class GradCamResult(
    val heatmapBitmap: Bitmap,
    val activeRegion: String,
    val peakActivation: Float = 0f,
    val rawCam: FloatArray = floatArrayOf(),
    /** Hugging Face Space predict_gradcam metin özeti (Tahmin / Güven / Açıklama) */
    val hfPredictionSummary: String? = null,
    val hfClassProbabilities: Map<String, Float> = emptyMap(),
    val hfPredictedStageLabel: String? = null,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as GradCamResult

        if (heatmapBitmap != other.heatmapBitmap) return false
        if (activeRegion != other.activeRegion) return false
        if (peakActivation != other.peakActivation) return false
        if (!rawCam.contentEquals(other.rawCam)) return false
        if (hfPredictionSummary != other.hfPredictionSummary) return false
        if (hfClassProbabilities != other.hfClassProbabilities) return false
        if (hfPredictedStageLabel != other.hfPredictedStageLabel) return false

        return true
    }

    override fun hashCode(): Int {
        var result = heatmapBitmap.hashCode()
        result = 31 * result + activeRegion.hashCode()
        result = 31 * result + peakActivation.hashCode()
        result = 31 * result + rawCam.contentHashCode()
        result = 31 * result + (hfPredictionSummary?.hashCode() ?: 0)
        result = 31 * result + hfClassProbabilities.hashCode()
        result = 31 * result + (hfPredictedStageLabel?.hashCode() ?: 0)
        return result
    }
}

data class GeminiReport(val text: String)

data class XaiUiState(
    val gradCamResult: GradCamResult? = null,
    val geminiReport: GeminiReport? = null,
    val isGradCamLoading: Boolean = false,
    val isGeminiLoading: Boolean = false,
    val gradCamError: String? = null,
    val geminiError: String? = null,
)

private val DEFAULT_SECTION_MARKERS = listOf(
    "Klinik Özet",
    "Model Karar Açıklaması",
    "MRI Bulguları",
    "Önerilen Sonraki Adımlar",
    "Sonraki Adımlar",
)

fun parseAiReportBlocks(
    reportText: String,
    knownHeadings: List<String>,
): List<Pair<String?, String>> {
    val text = reportText.trim()
    if (text.isBlank()) return emptyList()

    val markers = (knownHeadings + DEFAULT_SECTION_MARKERS)
        .map { it.trim() }
        .filter { it.isNotEmpty() }
        .distinctBy { it.uppercase() }
        .sortedByDescending { it.length }

    splitByHashHeadings(text)?.takeIf { it.size >= 2 }?.let { return it }
    splitBySectionMarkers(text, markers)?.takeIf { it.size >= 2 }?.let { return it }

    val lineParsed = parseAiReportBlocksLineByLine(text, knownHeadings)
    if (lineParsed.size >= 2) return lineParsed

    return lineParsed.ifEmpty { listOf(null to text) }
}

private fun splitByHashHeadings(text: String): List<Pair<String?, String>>? {
    val pattern = Regex("""(?m)^#{1,4}\s+(.+?)\s*$""")
    val matches = pattern.findAll(text).toList()
    if (matches.size < 2) return null

    val blocks = mutableListOf<Pair<String?, String>>()
    matches.forEachIndexed { index, match ->
        val title = match.groupValues[1]
            .replace("**", "")
            .removeSuffix(":")
            .trim()
        val contentStart = match.range.last + 1
        val contentEnd = matches.getOrNull(index + 1)?.range?.first ?: text.length
        val content = text.substring(contentStart, contentEnd).trim()
        if (content.isNotBlank() || title.isNotBlank()) {
            blocks.add(title to content)
        }
    }
    return blocks.takeIf { it.isNotEmpty() }
}

private fun splitBySectionMarkers(
    text: String,
    markers: List<String>,
): List<Pair<String?, String>>? {
    if (markers.isEmpty()) return null
    val escaped = markers.joinToString("|") { Regex.escape(it) }
    val pattern = Regex("""(?im)(?:^|\n)\s*(?:#{1,4}\s*|\*\*)?($escaped)\s*(?:\*\*)?\s*:?\s*(?=\n|$)""")
    val matches = pattern.findAll(text).toList()
    if (matches.size < 2) return null

    val blocks = mutableListOf<Pair<String?, String>>()
    val preamble = text.substring(0, matches.first().range.first).trim()
    if (preamble.isNotBlank()) {
        blocks.add(null to preamble)
    }
    matches.forEachIndexed { index, match ->
        val title = match.groupValues[1].trim()
        val contentStart = match.range.last + 1
        val contentEnd = matches.getOrNull(index + 1)?.range?.first ?: text.length
        val content = text.substring(contentStart, contentEnd).trim()
        if (content.isNotBlank()) {
            blocks.add(title to content)
        }
    }
    return blocks.takeIf { it.size >= 2 }
}

private fun parseAiReportBlocksLineByLine(
    reportText: String,
    knownHeadings: List<String>,
): List<Pair<String?, String>> {
    val lines = reportText.split("\n")
    val blocks = mutableListOf<Pair<String?, String>>()
    val headingHints = (knownHeadings + DEFAULT_SECTION_MARKERS).map { it.uppercase() }

    var currentTitle: String? = null
    val currentContent = StringBuilder()

    fun flush() {
        val body = currentContent.toString().trim()
        if (currentTitle != null || body.isNotBlank()) {
            blocks.add(currentTitle to body)
        }
        currentContent.clear()
    }

    for (line in lines) {
        val trimmed = line.trim()
        if (trimmed.isEmpty()) {
            currentContent.append('\n')
            continue
        }
        val upper = trimmed.uppercase()
        val isHashHeading = trimmed.startsWith("#")
        val isBoldHeading =
            trimmed.startsWith("**") && trimmed.endsWith("**") && trimmed.length < 80
        val isBoldColonHeading =
            trimmed.startsWith("**") && trimmed.endsWith("**:") && trimmed.length < 80
        val isKnownHeading = headingHints.any { hint ->
            upper.contains(hint) && trimmed.length < 90
        } && !trimmed.contains('.')

        if (isHashHeading || isBoldHeading || isBoldColonHeading || isKnownHeading) {
            flush()
            currentTitle = trimmed
                .replace(Regex("^#+\\s*"), "")
                .replace("**", "")
                .removeSuffix(":")
                .trim()
        } else {
            currentContent.append(trimmed).append('\n')
        }
    }
    flush()
    return blocks
}