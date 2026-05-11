package com.esmanureral.neurostage.auth

data class AuthedUser(
    val uid: String,
    val email: String?,
)

sealed class AuthStatus {
    data object Unknown : AuthStatus()
    data object SignedOut : AuthStatus()
    data class SignedIn(val user: AuthedUser) : AuthStatus()
}