package com.esmanureral.neurostage.scans

import kotlinx.coroutines.flow.StateFlow

interface ScanRepository {
    val doctorScans: StateFlow<List<ScanRecord>>
    suspend fun add(record: ScanRecord): Result<Unit>
    suspend fun refreshDoctor(doctorUid: String): Result<Unit>
    suspend fun refreshPatient(doctorUid: String, patientId: String): Result<List<ScanRecord>>
    suspend fun updateAiReport(
        doctorUid: String,
        patientId: String,
        id: String,
        report: String
    ): Result<Unit>

    suspend fun delete(
        doctorUid: String,
        patientId: String,
        scanId: String,
    ): Result<Unit>
}