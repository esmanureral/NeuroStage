package com.esmanureral.neurostage.ui.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.esmanureral.neurostage.auth.AuthRepository
import com.esmanureral.neurostage.auth.AuthStatus
import com.esmanureral.neurostage.data.AppPreferences
import com.esmanureral.neurostage.data.UserWorld
import com.esmanureral.neurostage.domain.patient.PatientStage
import com.esmanureral.neurostage.navigation.Routes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RoleGateViewModel @Inject constructor(
    private val prefs: AppPreferences,
    private val auth: AuthRepository,
) : ViewModel() {

    private val _startDestination = MutableStateFlow<String?>(null)
    val startDestination: StateFlow<String?> = _startDestination.asStateFlow()

    init {
        viewModelScope.launch {
            val world = prefs.userWorld.value

            if (world == null) {
                _startDestination.value = Routes.ROLE_PICK
                return@launch
            }

            _startDestination.value = when (world) {
                UserWorld.DOCTOR -> resolveDoctorDestination()
                UserWorld.PATIENT -> resolvePatientDestination(prefs.patientStage.value)
            }
        }
    }

    private suspend fun resolveDoctorDestination(): String {
        val authStatus = auth.status
            .filter { it !is AuthStatus.Unknown }
            .first()
        return if (authStatus is AuthStatus.SignedIn) {
            Routes.DOCTOR_HOME
        } else {
            Routes.DOCTOR_LOGIN
        }
    }

    private fun resolvePatientDestination(patientStage: Int?): String = when {
        patientStage == null -> Routes.PATIENT_SCAN
        PatientStage.canAccessPatientExerciseHub(patientStage) -> Routes.PATIENT_GAMES
        else -> Routes.PATIENT_HOME
    }
}
