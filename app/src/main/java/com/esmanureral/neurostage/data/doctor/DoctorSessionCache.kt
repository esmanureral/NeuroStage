package com.esmanureral.neurostage.data.doctor

import com.esmanureral.neurostage.patients.Patient
import com.esmanureral.neurostage.scans.ScanRecord
import com.esmanureral.neurostage.ui.doctor.PatientSummary
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DoctorSessionCache @Inject constructor() {
    data class PatientHistoryEntry(
        val patient: Patient,
        val scans: List<ScanRecord>,
    )

    private var patientsDoctorUid: String? = null
    private var patients: List<PatientSummary> = emptyList()
    private val historyEntries = mutableMapOf<String, PatientHistoryEntry>()

    fun patientsFor(doctorUid: String): List<PatientSummary>? =
        if (patientsDoctorUid == doctorUid) patients else null

    fun savePatients(doctorUid: String, list: List<PatientSummary>) {
        patientsDoctorUid = doctorUid
        patients = list
    }

    fun historyFor(doctorUid: String, patientId: String): PatientHistoryEntry? =
        historyEntries[historyKey(doctorUid, patientId)]

    fun saveHistory(doctorUid: String, patientId: String, patient: Patient, scans: List<ScanRecord>) {
        historyEntries[historyKey(doctorUid, patientId)] = PatientHistoryEntry(patient, scans)
        val index = patients.indexOfFirst { it.patient.id == patientId }
        if (index >= 0) {
            val current = patients[index]
            val lastLabel = scans.maxByOrNull { it.timestampMs }?.label
            patients = patients.toMutableList().apply {
                set(index, current.copy(lastScanLabel = lastLabel ?: current.lastScanLabel))
            }
        }
    }

    fun clear() {
        patientsDoctorUid = null
        patients = emptyList()
        historyEntries.clear()
    }

    private fun historyKey(doctorUid: String, patientId: String) = "$doctorUid:$patientId"
}
