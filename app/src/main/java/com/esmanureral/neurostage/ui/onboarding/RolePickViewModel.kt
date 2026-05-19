package com.esmanureral.neurostage.ui.onboarding

import androidx.lifecycle.ViewModel
import com.esmanureral.neurostage.data.AppPreferences
import com.esmanureral.neurostage.data.UserWorld
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RolePickViewModel @Inject constructor(
    private val prefs: AppPreferences,
) : ViewModel() {
    val patientStage = prefs.patientStage

    fun pickDoctor() = prefs.setWorld(UserWorld.DOCTOR)
    fun pickPatient() = prefs.setWorld(UserWorld.PATIENT)
    fun clear() = prefs.clearWorld()

    fun clearIfHadSession() {
        if (prefs.userWorld.value != null) {
            clear()
        }
    }
}