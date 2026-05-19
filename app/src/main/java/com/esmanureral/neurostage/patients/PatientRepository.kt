package com.esmanureral.neurostage.patients

interface PatientRepository {
    suspend fun create(
        doctorUid: String,
        fullName: String,
        age: Int? = null,
        gender: String? = null
    ): Result<Patient>

    suspend fun get(doctorUid: String, patientId: String): Result<Patient>
    suspend fun list(doctorUid: String): Result<List<Patient>>
    suspend fun delete(doctorUid: String, patientId: String): Result<Unit>
    suspend fun deleteMany(doctorUid: String, patientIds: List<String>): Result<Unit>
}