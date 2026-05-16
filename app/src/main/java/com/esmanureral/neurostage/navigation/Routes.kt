package com.esmanureral.neurostage.navigation

object Routes {
    const val ROLE_GATE = "role_gate"
    const val ROLE_PICK = "role_pick"

    const val DOCTOR_LOGIN = "doctor_login"
    const val DOCTOR_HOME = "doctor_home"
    const val DOCTOR_HISTORY = "doctor_history"
    const val DOCTOR_RESULT_DETAIL = "doctor_result_detail"
    const val DOCTOR_PATIENT_INTAKE = "doctor_patient_intake"
    const val DOCTOR_SCAN = "doctor_scan"
    const val DOCTOR_PATIENTS = "doctor_patients"
    const val DOCTOR_PATIENT_HISTORY = "doctor_patient_history"

    const val PATIENT_HOME = "patient_home"
    const val PATIENT_SCAN = "patient_scan"

    const val PATIENT_GAMES = "patient_games"
    const val PATIENT_GAME_ROUTINE = "patient_game_routine"
    const val PATIENT_GAME_MEMORY = "patient_game_memory"
    const val PATIENT_GAME_MEMORY_MATCH = "patient_game_memory_match"
    const val PATIENT_GAME_PUZZLE = "patient_game_puzzle"
    const val PATIENT_GAME_PUZZLE_MRI_MODERATE = "patient_game_puzzle_mri_moderate"
}

object RouteArgs {
    const val SCAN_TS = "scanTs"
    const val PATIENT_ID = "patientId"
}