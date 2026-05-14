package com.esmanureral.neurostage.data

data class MrScanRecord(
    val timestamp: Long,
    val stageIndex: Int,
    val label: String,
    val confidence: Float,
    val scores: FloatArray? = null,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MrScanRecord

        if (timestamp != other.timestamp) return false
        if (stageIndex != other.stageIndex) return false
        if (label != other.label) return false
        if (confidence != other.confidence) return false
        if (scores != null) {
            if (other.scores == null) return false
            if (!scores.contentEquals(other.scores)) return false
        } else if (other.scores != null) return false

        return true
    }

    override fun hashCode(): Int {
        var result = timestamp.hashCode()
        result = 31 * result + stageIndex
        result = 31 * result + label.hashCode()
        result = 31 * result + confidence.hashCode()
        result = 31 * result + (scores?.contentHashCode() ?: 0)
        return result
    }
}