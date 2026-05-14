package com.esmanureral.neurostage.scans

data class ScanRecord(
    val id: String,
    val doctorUid: String,
    val patientId: String,
    val patientName: String,
    val timestampMs: Long,
    val stageIndex: Int,
    val label: String,
    val confidence: Float,
    val scores: List<Float>,
    val aiReport: String? = null,
)