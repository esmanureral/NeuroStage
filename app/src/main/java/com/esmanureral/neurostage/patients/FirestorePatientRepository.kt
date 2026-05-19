package com.esmanureral.neurostage.patients

import com.esmanureral.neurostage.util.Constants
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirestorePatientRepository @Inject constructor() : PatientRepository {
    private val db: FirebaseFirestore? = runCatching {
        FirebaseApp.getInstance()
        FirebaseFirestore.getInstance()
    }.getOrNull()

    override suspend fun create(
        doctorUid: String,
        fullName: String,
        age: Int?,
        gender: String?
    ): Result<Patient> {
        val firestore =
            db ?: return Result.failure(IllegalStateException("Firestore başlatılamadı."))
        val name = fullName.trim()
        if (name.isBlank()) return Result.failure(IllegalArgumentException("Hasta adı boş olamaz."))
        return runCatching {
            val doc = firestore.collection(Constants.Firestore.COLLECTION_USERS).document(doctorUid)
                .collection(Constants.Firestore.COLLECTION_PATIENTS)
                .document()

            val data = hashMapOf<String, Any?>(
                Constants.Firestore.PatientFields.FULL_NAME to name,
                Constants.Firestore.PatientFields.AGE to age,
                Constants.Firestore.PatientFields.GENDER to gender,
                Constants.Firestore.PatientFields.CREATED_AT to FieldValue.serverTimestamp(),
            )
            doc.set(data).await()
            Patient(id = doc.id, fullName = name, age = age, gender = gender, createdAtMs = null)
        }
    }

    override suspend fun get(doctorUid: String, patientId: String): Result<Patient> {
        val firestore =
            db ?: return Result.failure(IllegalStateException("Firestore başlatılamadı."))
        return runCatching {
            val snap =
                firestore.collection(Constants.Firestore.COLLECTION_USERS).document(doctorUid)
                    .collection(Constants.Firestore.COLLECTION_PATIENTS)
                    .document(patientId)
                    .get()
                    .await()
            val name = snap.getString(Constants.Firestore.PatientFields.FULL_NAME) ?: ""
            val age = snap.getLong(Constants.Firestore.PatientFields.AGE)?.toInt()
            val gender = snap.getString(Constants.Firestore.PatientFields.GENDER)
            Patient(id = patientId, fullName = name, age = age, gender = gender, createdAtMs = null)
        }
    }

    override suspend fun list(doctorUid: String): Result<List<Patient>> {
        val firestore =
            db ?: return Result.failure(IllegalStateException("Firestore başlatılamadı."))
        return runCatching {
            val snaps =
                firestore.collection(Constants.Firestore.COLLECTION_USERS).document(doctorUid)
                    .collection(Constants.Firestore.COLLECTION_PATIENTS)
                    .orderBy(
                        Constants.Firestore.PatientFields.CREATED_AT,
                        Query.Direction.DESCENDING
                    )
                    .limit(100)
                    .get()
                    .await()
            snaps.documents.map { d ->
                Patient(
                    id = d.id,
                    fullName = d.getString(Constants.Firestore.PatientFields.FULL_NAME) ?: "",
                    age = d.getLong(Constants.Firestore.PatientFields.AGE)?.toInt(),
                    gender = d.getString(Constants.Firestore.PatientFields.GENDER),
                    createdAtMs = null,
                )
            }
        }
    }

    override suspend fun delete(doctorUid: String, patientId: String): Result<Unit> {
        val firestore =
            db ?: return Result.failure(IllegalStateException("Firestore başlatılamadı."))
        return runCatching {
            val patientRef = firestore.collection(Constants.Firestore.COLLECTION_USERS)
                .document(doctorUid)
                .collection(Constants.Firestore.COLLECTION_PATIENTS)
                .document(patientId)

            val scanSnaps = patientRef.collection(Constants.Firestore.COLLECTION_SCANS)
                .get()
                .await()

            val batch = firestore.batch()
            scanSnaps.documents.forEach { batch.delete(it.reference) }
            batch.delete(patientRef)
            batch.commit().await()
        }
    }

    override suspend fun deleteMany(doctorUid: String, patientIds: List<String>): Result<Unit> {
        if (patientIds.isEmpty()) return Result.success(Unit)
        patientIds.forEach { patientId ->
            delete(doctorUid, patientId).getOrElse { return Result.failure(it) }
        }
        return Result.success(Unit)
    }
}