package com.esmanureral.neurostage.ui.doctor

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.esmanureral.neurostage.R
import com.esmanureral.neurostage.auth.AuthRepository
import com.esmanureral.neurostage.auth.AuthStatus
import com.esmanureral.neurostage.data.AppPreferences
import com.esmanureral.neurostage.data.UserWorld
import com.esmanureral.neurostage.profile.UserProfile
import com.esmanureral.neurostage.profile.UserProfileRepository
import com.esmanureral.neurostage.util.Constants
import com.google.firebase.auth.FirebaseAuthException
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

data class DoctorLoginUiState(
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val password: String = "",
    val isSignUp: Boolean = false,
    val info: String? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val justSignedUp: Boolean = false,
)

@HiltViewModel
class DoctorAuthViewModel @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val auth: AuthRepository,
    private val prefs: AppPreferences,
    private val profiles: UserProfileRepository,
) : ViewModel() {
    val authStatus: StateFlow<AuthStatus> = auth.status

    private val _ui = MutableStateFlow(DoctorLoginUiState())
    val ui: StateFlow<DoctorLoginUiState> = _ui.asStateFlow()

    fun onFirstNameChange(v: String) {
        _ui.value = _ui.value.copy(firstName = v, error = null)
    }

    fun onLastNameChange(v: String) {
        _ui.value = _ui.value.copy(lastName = v, error = null)
    }

    fun onEmailChange(v: String) {
        _ui.value = _ui.value.copy(email = v, error = null)
    }

    fun onPasswordChange(v: String) {
        _ui.value = _ui.value.copy(password = v, error = null)
    }

    fun pickWorldDoctor() {
        prefs.setWorld(UserWorld.DOCTOR)
    }

    fun toggleMode() {
        _ui.value = _ui.value.copy(isSignUp = !_ui.value.isSignUp, error = null, info = null)
    }

    fun signIn() {
        val password = _ui.value.password
        val isSignUp = _ui.value.isSignUp
        val email = _ui.value.email.trim()
        val first = _ui.value.firstName.trim()
        val last = _ui.value.lastName.trim()
        val minLen = Constants.Auth.MIN_PASSWORD_LENGTH

        if (isSignUp) {
            if (first.isBlank() || last.isBlank()) {
                _ui.value = _ui.value.copy(error = context.getString(R.string.doctor_auth_error_name_required))
                return
            }
        }
        if (email.isBlank() || password.length < minLen) {
            _ui.value = _ui.value.copy(
                error = context.getString(R.string.doctor_auth_error_email_password, minLen),
            )
            return
        }

        viewModelScope.launch {
            _ui.value = _ui.value.copy(isLoading = true, error = null, info = null, justSignedUp = false)
            val res = if (isSignUp) auth.signUp(email, password) else auth.signIn(email, password)
            if (!res.isSuccess) {
                _ui.value = _ui.value.copy(
                    isLoading = false,
                    error = mapAuthError(res.exceptionOrNull()),
                )
                return@launch
            }
            if (!isSignUp) {
                _ui.value = _ui.value.copy(isLoading = false, error = null)
                return@launch
            }

            val uid = auth.currentUserUid()
            if (uid == null) {
                auth.signOut()
                _ui.value = _ui.value.copy(
                    isLoading = false,
                    error = context.getString(R.string.doctor_auth_error_signup_session),
                )
                return@launch
            }

            auth.updateDisplayName("$first $last".trim())

            val profileRes = profiles.upsert(
                uid = uid,
                profile = UserProfile(
                    firstName = first,
                    lastName = last,
                    email = email,
                    world = UserWorld.DOCTOR,
                ),
            )
            if (profileRes.isFailure) {
                val detail = profileRes.exceptionOrNull()?.message
                    ?: context.getString(R.string.doctor_auth_error_unknown)
                val base = context.getString(R.string.doctor_auth_error_profile_save)
                _ui.value = _ui.value.copy(
                    isLoading = false,
                    error = if (detail.isBlank()) base else "$base $detail",
                )
                return@launch
            }

            auth.signOut()
            _ui.value = _ui.value.copy(
                isLoading = false,
                isSignUp = false,
                password = "",
                info = context.getString(R.string.doctor_auth_info_signup_success),
                justSignedUp = true,
            )
        }
    }

    private fun mapAuthError(e: Throwable?): String {
        if (e == null) return context.getString(R.string.doctor_auth_error_unknown)
        val fc = Constants.Auth.FirebaseErrorCode
        findFirebaseAuthException(e)?.let { fe ->
            return when (fe.errorCode) {
                fc.EMAIL_ALREADY_IN_USE, fc.CREDENTIAL_ALREADY_IN_USE ->
                    context.getString(R.string.doctor_auth_error_email_in_use)
                fc.INVALID_EMAIL, fc.MISSING_EMAIL, fc.INVALID_PROVIDER_ID ->
                    context.getString(R.string.doctor_auth_error_invalid_email)
                fc.USER_NOT_FOUND, fc.USER_MISMATCH ->
                    context.getString(R.string.doctor_auth_error_user_not_found)
                fc.WRONG_PASSWORD, fc.INVALID_CREDENTIAL, fc.INVALID_USER_TOKEN ->
                    context.getString(R.string.doctor_auth_error_wrong_credentials)
                fc.NETWORK_REQUEST_FAILED ->
                    context.getString(R.string.doctor_auth_error_network)
                fc.TOO_MANY_REQUESTS ->
                    context.getString(R.string.doctor_auth_error_too_many_requests)
                fc.WEAK_PASSWORD ->
                    context.getString(R.string.doctor_auth_error_weak_password)
                else -> messageFallbackAuthError(fe.localizedMessage)
            }
        }
        return messageFallbackAuthError(e.message)
    }

    private fun messageFallbackAuthError(msg: String?): String {
        if (msg.isNullOrBlank()) return context.getString(R.string.doctor_auth_error_unknown)
        val lowerMsg = msg.lowercase(Locale.ROOT)
        val p = Constants.Auth.FirebaseMessagePattern
        return when {
            lowerMsg.contains(p.ALREADY_IN_USE) -> context.getString(R.string.doctor_auth_error_email_in_use)
            lowerMsg.contains(p.BADLY_FORMATTED) || lowerMsg.contains(p.INVALID_EMAIL) ->
                context.getString(R.string.doctor_auth_error_invalid_email)
            lowerMsg.contains(p.NO_USER_RECORD) || lowerMsg.contains(p.USER_NOT_FOUND) ->
                context.getString(R.string.doctor_auth_error_user_not_found)
            lowerMsg.contains(p.INVALID_LOGIN_CREDENTIALS) ||
                lowerMsg.contains(p.AUTH_CREDENTIAL) ||
                lowerMsg.contains(p.WRONG_PASSWORD) ||
                lowerMsg.contains(p.INVALID_PASSWORD) ->
                context.getString(R.string.doctor_auth_error_wrong_credentials)
            lowerMsg.contains(p.NETWORK_ERROR) ->
                context.getString(R.string.doctor_auth_error_network)
            lowerMsg.contains(p.TOO_MANY_REQUESTS) ->
                context.getString(R.string.doctor_auth_error_too_many_requests)
            else -> context.getString(R.string.doctor_auth_error_generic)
        }
    }

    private fun findFirebaseAuthException(e: Throwable): FirebaseAuthException? {
        var t: Throwable? = e
        while (t != null) {
            if (t is FirebaseAuthException) return t
            t = t.cause
        }
        return null
    }

    fun signOut() {
        viewModelScope.launch {
            auth.signOut()
        }
    }
}