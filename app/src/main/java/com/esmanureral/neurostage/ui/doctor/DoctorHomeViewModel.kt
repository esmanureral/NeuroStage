package com.esmanureral.neurostage.ui.doctor

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.esmanureral.neurostage.R
import com.esmanureral.neurostage.auth.AuthRepository
import com.esmanureral.neurostage.auth.AuthStatus
import com.esmanureral.neurostage.auth.LocalAuthSnapshot
import com.esmanureral.neurostage.profile.UserProfile
import com.esmanureral.neurostage.profile.UserProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DoctorHeaderState(
    val displayName: String? = null,
    val email: String? = null,
    val error: String? = null,
)

@HiltViewModel
class DoctorHomeViewModel @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val auth: AuthRepository,
    private val profiles: UserProfileRepository,
) : ViewModel() {
    val authStatus: StateFlow<AuthStatus> = auth.status

    private val _header = MutableStateFlow(DoctorHeaderState())
    val header: StateFlow<DoctorHeaderState> = _header.asStateFlow()

    init {
        auth.localAuthSnapshot()?.let(::applyLocalSnapshotToHeader)
    }

    fun loadProfileIfNeeded() {
        val user = (auth.status.value as? AuthStatus.SignedIn)?.user ?: return

        val local = auth.localAuthSnapshot()
        val firebaseDisplay = local.blankToNullDisplayName()
        val firebaseEmail = local?.email

        if (_header.value.displayName == null && firebaseDisplay != null) {
            _header.value = DoctorHeaderState(displayName = firebaseDisplay, email = firebaseEmail)
        }

        viewModelScope.launch {
            val res = profiles.get(user.uid)
            res.onSuccess { p: UserProfile ->
                val name = mergedDisplayName(p, firebaseDisplay)
                _header.value = DoctorHeaderState(
                    displayName = name,
                    email = p.email?.takeIf { it.isNotBlank() } ?: firebaseEmail,
                )
            }.onFailure {
                _header.value = DoctorHeaderState(
                    displayName = firebaseDisplay,
                    email = firebaseEmail,
                    error = context.getString(R.string.doctor_home_profile_error),
                )
            }
        }
    }

    private fun applyLocalSnapshotToHeader(snapshot: LocalAuthSnapshot) {
        _header.value = DoctorHeaderState(
            displayName = snapshot.displayName?.takeIf { it.isNotBlank() },
            email = snapshot.email,
        )
    }

    private fun mergedDisplayName(
        profile: UserProfile,
        firebaseDisplayFallback: String?,
    ): String? {
        val combined = listOf(profile.firstName, profile.lastName)
            .map { it.trim() }
            .filter { it.isNotBlank() }
            .joinToString(" ")
            .takeIf { it.isNotBlank() }
        return combined ?: firebaseDisplayFallback?.takeIf { it.isNotBlank() }
    }

    fun signOut() {
        viewModelScope.launch {
            auth.signOut()
            _header.value = DoctorHeaderState()
        }
    }
}

private fun LocalAuthSnapshot?.blankToNullDisplayName(): String? =
    this?.displayName?.trim()?.takeIf { it.isNotBlank() }