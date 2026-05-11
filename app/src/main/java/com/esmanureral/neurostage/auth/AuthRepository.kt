package com.esmanureral.neurostage.auth

import kotlinx.coroutines.flow.StateFlow

interface AuthRepository {
    val status: StateFlow<AuthStatus>

    suspend fun signIn(email: String, password: String): Result<Unit>
    suspend fun signUp(email: String, password: String): Result<Unit>
    suspend fun signOut()
}