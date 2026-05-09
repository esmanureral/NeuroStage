package com.esmanureral.neurostage.data

data class MrScanRecord(
    val timestamp: Long,
    val stageIndex: Int,
    val label: String,
    val confidence: Float,
)