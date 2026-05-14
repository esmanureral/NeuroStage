package com.esmanureral.neurostage.util

object Constants {
    const val PREFS_NAME = "neurostage_prefs"

    object Format {
        const val SCAN_DATETIME = "d MMM yyyy · HH:mm"
    }

    object LocaleConfig {
        const val LANG_TR = "tr"
        const val REGION_TR = "TR"
    }

    object MriStageLabel {
        const val HEALTHY = "Sağlıklı"
        const val MILD = "Hafif evre"
        const val MODERATE = "Orta evre"
        const val VERY_MILD = "Çok hafif evre"
    }

    object Ui {
        const val STATE_FLOW_STOP_TIMEOUT_MS = 5_000L
    }

    object Auth {
        const val MIN_PASSWORD_LENGTH = 6

        object FirebaseErrorCode {
            const val EMAIL_ALREADY_IN_USE = "ERROR_EMAIL_ALREADY_IN_USE"
            const val CREDENTIAL_ALREADY_IN_USE = "ERROR_CREDENTIAL_ALREADY_IN_USE"
            const val INVALID_EMAIL = "ERROR_INVALID_EMAIL"
            const val MISSING_EMAIL = "ERROR_MISSING_EMAIL"
            const val INVALID_PROVIDER_ID = "ERROR_INVALID_PROVIDER_ID"
            const val USER_NOT_FOUND = "ERROR_USER_NOT_FOUND"
            const val USER_MISMATCH = "ERROR_USER_MISMATCH"
            const val WRONG_PASSWORD = "ERROR_WRONG_PASSWORD"
            const val INVALID_CREDENTIAL = "ERROR_INVALID_CREDENTIAL"
            const val INVALID_USER_TOKEN = "ERROR_INVALID_USER_TOKEN"
            const val NETWORK_REQUEST_FAILED = "ERROR_NETWORK_REQUEST_FAILED"
            const val TOO_MANY_REQUESTS = "ERROR_TOO_MANY_REQUESTS"
            const val WEAK_PASSWORD = "ERROR_WEAK_PASSWORD"
        }

        object FirebaseMessagePattern {
            const val ALREADY_IN_USE = "already in use"
            const val BADLY_FORMATTED = "badly formatted"
            const val INVALID_EMAIL = "invalid email"
            const val NO_USER_RECORD = "no user record"
            const val USER_NOT_FOUND = "user not found"
            const val INVALID_LOGIN_CREDENTIALS = "invalid_login_credentials"
            const val AUTH_CREDENTIAL = "auth credential"
            const val WRONG_PASSWORD = "wrong password"
            const val INVALID_PASSWORD = "invalid password"
            const val NETWORK_ERROR = "network error"
            const val TOO_MANY_REQUESTS = "too many requests"
        }
    }

    object Model {
        const val ALZHEIMER_FILE_NAME = "alzheimer_preprocessed.tflite"
        const val ALZHEIMER_INPUT_SIZE = 260

        const val MRI_FILTER_FILE_NAME = "mri_filter_v2_noquant.tflite"
        const val MRI_FILTER_INPUT_SIZE = 224
    }

    object Firestore {
        const val COLLECTION_USERS = "users"
        const val COLLECTION_PATIENTS = "patients"
        const val COLLECTION_SCANS = "scans"

        object PatientFields {
            const val FULL_NAME = "fullName"
            const val AGE = "age"
            const val GENDER = "gender"
            const val CREATED_AT = "createdAt"
        }

        object UserProfileFields {
            const val FIRST_NAME = "firstName"
            const val LAST_NAME = "lastName"
            const val EMAIL = "email"
            const val WORLD = "world"
            const val UPDATED_AT = "updatedAt"
            const val CREATED_AT = "createdAt"
        }

        object ScanFields {
            const val DOCTOR_UID = "doctorUid"
            const val PATIENT_ID = "patientId"
            const val PATIENT_NAME = "patientName"
            const val TIMESTAMP_MS = "timestampMs"
            const val STAGE_INDEX = "stageIndex"
            const val LABEL = "label"
            const val CONFIDENCE = "confidence"
            const val SCORES = "scores"
            const val AI_REPORT = "aiReport"
        }
    }
}