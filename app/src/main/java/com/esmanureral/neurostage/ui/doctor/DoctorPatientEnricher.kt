package com.esmanureral.neurostage.ui.doctor

import com.esmanureral.neurostage.patients.Patient
import com.esmanureral.neurostage.scans.ScanRecord

object DoctorPatientEnricher {
    fun summaries(
        patients: List<Patient>,
        scans: List<ScanRecord>,
        improvingLabel: String,
        stableLabel: String,
    ): List<PatientSummary> {
        val scansByPatient = scans.groupBy { it.patientId }
        return patients.map { patient ->
            val patientScans = scansByPatient[patient.id]
                .orEmpty()
                .sortedByDescending { it.timestampMs }
            val lastLabel = patientScans.firstOrNull()?.label
            val lastStatus = when {
                patientScans.size >= 2 ->
                    if (patientScans[0].stageIndex > patientScans[1].stageIndex) improvingLabel else stableLabel
                patientScans.size == 1 -> stableLabel
                else -> null
            }
            PatientSummary(patient = patient, lastScanLabel = lastLabel, lastStatus = lastStatus)
        }
    }
}
