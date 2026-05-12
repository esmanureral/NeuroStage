package com.esmanureral.neurostage.auth

import android.content.Context
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.esmanureral.neurostage.R
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseAuthRepository @Inject constructor(
    @param:ApplicationContext private val context: Context,
) : AuthRepository {

    private var initException: Throwable? = null

    private val firebaseAuth: FirebaseAuth? = runCatching {
        FirebaseApp.initializeApp(context)
        FirebaseAuth.getInstance()
    }.onFailure {
        initException = it
        Log.e("FirebaseAuthRepo", "Firebase başlatılamadı: ${it.message}", it)
    }.getOrNull()

    override val status: StateFlow<AuthStatus> = firebaseAuth?.let { auth ->
        callbackFlow {
            val initialUser = auth.currentUser
            trySend(
                if (initialUser == null) AuthStatus.SignedOut
                else AuthStatus.SignedIn(
                    AuthedUser(
                        uid = initialUser.uid,
                        email = initialUser.email
                    )
                )
            )

            val listener = FirebaseAuth.AuthStateListener { a ->
                val u = a.currentUser
                val newStatus = if (u == null) {
                    AuthStatus.SignedOut
                } else {
                    AuthStatus.SignedIn(AuthedUser(uid = u.uid, email = u.email))
                }
                trySend(newStatus)
            }
            auth.addAuthStateListener(listener)

            awaitClose {
                Log.d("FirebaseAuthRepo", "AuthStateListener temizlendi.")
                auth.removeAuthStateListener(listener)
            }
        }.stateIn(
            scope = CoroutineScope(Dispatchers.Default + SupervisorJob()),
            started = SharingStarted.Eagerly,
            initialValue = AuthStatus.Unknown
        )
    } ?: MutableStateFlow(AuthStatus.SignedOut).asStateFlow()

    override suspend fun signIn(email: String, password: String): Result<Unit> {
        val auth = firebaseAuth ?: return Result.failure(
            IllegalStateException(context.getString(R.string.error_firebase_config), initException)
        )
        return runCatching {
            auth.signInWithEmailAndPassword(email.trim(), password).await()
        }.map { }
    }

    override suspend fun signUp(email: String, password: String): Result<Unit> {
        val auth = firebaseAuth ?: return Result.failure(
            IllegalStateException(context.getString(R.string.error_firebase_config), initException)
        )
        return runCatching {
            auth.createUserWithEmailAndPassword(email.trim(), password).await()
        }.map { }
    }

    override suspend fun signOut() {
        firebaseAuth?.signOut()
    }
}