package com.esmanureral.neurostage.scans

import com.esmanureral.neurostage.util.Constants
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirestoreScanRepository @Inject constructor() : ScanRepository {
    private val db: FirebaseFirestore? = runCatching {
        FirebaseApp.getInstance()
        FirebaseFirestore.getInstance()
    }.getOrNull()

    private val _doctorScans = MutableStateFlow<List<ScanRecord>>(emptyList())
    override val doctorScans: StateFlow<List<ScanRecord>> = _doctorScans.asStateFlow()

    // Scans are stored under doctors/{doctorUid}/patients/{patientId}/scans/{id}
    override suspend fun add(record: ScanRecord): Result<Unit> {
        val firestore =
            db ?: return Result.failure(IllegalStateException("Firestore başlatılamadı."))
        return runCatching {
            val doc = firestore
                .collection(Constants.Firestore.COLLECTION_USERS).document(record.doctorUid)
                .collection(Constants.Firestore.COLLECTION_PATIENTS).document(record.patientId)
                .collection(Constants.Firestore.COLLECTION_SCANS).document(record.id)

            val data = hashMapOf<String, Any?>(
                Constants.Firestore.ScanFields.DOCTOR_UID to record.doctorUid,
                Constants.Firestore.ScanFields.PATIENT_ID to record.patientId,
                Constants.Firestore.ScanFields.PATIENT_NAME to record.patientName,
                Constants.Firestore.ScanFields.TIMESTAMP_MS to record.timestampMs,
                Constants.Firestore.ScanFields.STAGE_INDEX to record.stageIndex,
                Constants.Firestore.ScanFields.LABEL to record.label,
                Constants.Firestore.ScanFields.CONFIDENCE to record.confidence.toDouble(),
                Constants.Firestore.ScanFields.SCORES to record.scores.map { it.toDouble() },
                Constants.Firestore.ScanFields.AI_REPORT to record.aiReport,
            )
            doc.set(data).await()
        }.map { Unit }
    }

    override suspend fun refreshDoctor(doctorUid: String): Result<Unit> {
        val firestore =
            db ?: return Result.failure(IllegalStateException("Firestore başlatılamadı."))
        return runCatching {
            val snaps = firestore.collectionGroup(Constants.Firestore.COLLECTION_SCANS)
                .whereEqualTo(Constants.Firestore.ScanFields.DOCTOR_UID, doctorUid)
                .orderBy(Constants.Firestore.ScanFields.TIMESTAMP_MS, Query.Direction.DESCENDING)
                .limit(200)
                .get()
                .await()

            _doctorScans.value = snaps.documents.mapNotNull { d -> d.toScanRecord() }
        }
    }

    override suspend fun refreshPatient(
        doctorUid: String,
        patientId: String
    ): Result<List<ScanRecord>> {
        val firestore =
            db ?: return Result.failure(IllegalStateException("Firestore başlatılamadı."))
        return runCatching {
            val snaps = firestore
                .collection(Constants.Firestore.COLLECTION_USERS).document(doctorUid)
                .collection(Constants.Firestore.COLLECTION_PATIENTS).document(patientId)
                .collection(Constants.Firestore.COLLECTION_SCANS)
                .orderBy(Constants.Firestore.ScanFields.TIMESTAMP_MS, Query.Direction.DESCENDING)
                .get()
                .await()

            snaps.documents.mapNotNull { d -> d.toScanRecord() }
        }
    }

    override suspend fun updateAiReport(
        doctorUid: String,
        patientId: String,
        id: String,
        report: String
    ): Result<Unit> {
        val firestore =
            db ?: return Result.failure(IllegalStateException("Firestore başlatılamadı."))
        return runCatching {
            firestore
                .collection(Constants.Firestore.COLLECTION_USERS).document(doctorUid)
                .collection(Constants.Firestore.COLLECTION_PATIENTS).document(patientId)
                .collection(Constants.Firestore.COLLECTION_SCANS).document(id)
                .update(Constants.Firestore.ScanFields.AI_REPORT, report)
                .await()
        }.map { Unit }
    }

    override suspend fun delete(
        doctorUid: String,
        patientId: String,
        scanId: String,
    ): Result<Unit> {
        val firestore =
            db ?: return Result.failure(IllegalStateException("Firestore başlatılamadı."))
        return runCatching {
            firestore
                .collection(Constants.Firestore.COLLECTION_USERS).document(doctorUid)
                .collection(Constants.Firestore.COLLECTION_PATIENTS).document(patientId)
                .collection(Constants.Firestore.COLLECTION_SCANS).document(scanId)
                .delete()
                .await()
        }.map { Unit }
    }

    private fun com.google.firebase.firestore.DocumentSnapshot.toScanRecord(): ScanRecord? {
        val doctorUid = getString(Constants.Firestore.ScanFields.DOCTOR_UID) ?: return null
        val patientId = getString(Constants.Firestore.ScanFields.PATIENT_ID) ?: return null
        val patientName = getString(Constants.Firestore.ScanFields.PATIENT_NAME) ?: ""
        val ts = getLong(Constants.Firestore.ScanFields.TIMESTAMP_MS) ?: 0L
        val stageIndex = (getLong(Constants.Firestore.ScanFields.STAGE_INDEX) ?: 0L).toInt()
        val label = getString(Constants.Firestore.ScanFields.LABEL) ?: ""
        val conf = (getDouble(Constants.Firestore.ScanFields.CONFIDENCE) ?: 0.0).toFloat()
        val scores =
            (get(Constants.Firestore.ScanFields.SCORES) as? List<*>)?.mapNotNull { (it as? Number)?.toFloat() }
                ?: emptyList()
        val aiReport = getString(Constants.Firestore.ScanFields.AI_REPORT)
        return ScanRecord(
            id = id,
            doctorUid = doctorUid,
            patientId = patientId,
            patientName = patientName,
            timestampMs = ts,
            stageIndex = stageIndex,
            label = label,
            confidence = conf,
            scores = scores,
            aiReport = aiReport,
        )
    }
}