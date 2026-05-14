package com.esmanureral.neurostage.ui.doctor

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.esmanureral.neurostage.R
import com.esmanureral.neurostage.auth.AuthRepository
import com.esmanureral.neurostage.auth.AuthStatus
import com.esmanureral.neurostage.patients.PatientRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PatientIntakeUiState(
    val patientFullName: String = "",
    val age: String = "",
    val gender: String = "",
    val isSaving: Boolean = false,
    val error: String? = null,
)

@HiltViewModel
class PatientIntakeViewModel @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val auth: AuthRepository,
    private val patients: PatientRepository,
) : ViewModel() {
    private val _ui = MutableStateFlow(PatientIntakeUiState())
    val ui: StateFlow<PatientIntakeUiState> = _ui.asStateFlow()

    fun onPatientNameChange(v: String) {
        _ui.value = _ui.value.copy(patientFullName = v, error = null)
    }

    fun onAgeChange(v: String) {
        if (v.all { it.isDigit() } && v.length <= 3) {
            _ui.value = _ui.value.copy(age = v, error = null)
        }
    }

    fun onGenderChange(v: String) {
        _ui.value = _ui.value.copy(gender = v, error = null)
    }

    fun save(onSaved: (patientId: String) -> Unit) {
        val uid = (auth.status.value as? AuthStatus.SignedIn)?.user?.uid
        if (uid == null) {
            _ui.value = _ui.value.copy(error = context.getString(R.string.error_session_required))
            return
        }
        val name = _ui.value.patientFullName.trim()
        if (name.isBlank()) {
            _ui.value = _ui.value.copy(error = context.getString(R.string.patient_intake_error_name))
            return
        }
        val age = _ui.value.age.toIntOrNull()
        val gender = _ui.value.gender.ifBlank { null }
        viewModelScope.launch {
            _ui.value = _ui.value.copy(isSaving = true, error = null)
            val res = patients.create(uid, name, age, gender)
            val baseFailed = context.getString(R.string.patient_intake_save_failed)
            val errMsg = res.exceptionOrNull()?.message
            _ui.value = _ui.value.copy(
                isSaving = false,
                error = res.exceptionOrNull()?.let { ex ->
                    if (errMsg.isNullOrBlank()) baseFailed else "$baseFailed $errMsg"
                },
            )
            res.onSuccess { onSaved(it.id) }
        }
    }
}
