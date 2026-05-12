package com.esmanureral.neurostage.profile

import com.esmanureral.neurostage.data.UserWorld
import com.esmanureral.neurostage.util.Constants
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirestoreUserProfileRepository @Inject constructor() : UserProfileRepository {
    private val firestore: FirebaseFirestore? = runCatching {
        // FirebaseApp init zaten Auth tarafında deneniyor; burada da güvenli kalalım.
        FirebaseApp.getInstance()
        FirebaseFirestore.getInstance()
    }.getOrNull()

    override suspend fun upsert(uid: String, profile: UserProfile): Result<Unit> {
        val db = firestore ?: return Result.failure(
            IllegalStateException("Firestore başlatılamadı. Firebase kurulumunu kontrol edin.")
        )
        val data = hashMapOf<String, Any?>(
            Constants.Firestore.UserProfileFields.FIRST_NAME to profile.firstName.trim(),
            Constants.Firestore.UserProfileFields.LAST_NAME to profile.lastName.trim(),
            Constants.Firestore.UserProfileFields.EMAIL to profile.email,
            Constants.Firestore.UserProfileFields.WORLD to profile.world.name,
            Constants.Firestore.UserProfileFields.UPDATED_AT to FieldValue.serverTimestamp(),
            Constants.Firestore.UserProfileFields.CREATED_AT to FieldValue.serverTimestamp(),
        )
        return runCatching {
            db.collection(Constants.Firestore.COLLECTION_USERS)
                .document(uid)
                .set(data)
                .await()
        }.map { Unit }
    }

    override suspend fun get(uid: String): Result<UserProfile> {
        val db = firestore ?: return Result.failure(
            IllegalStateException("Firestore başlatılamadı. Firebase kurulumunu kontrol edin.")
        )
        return runCatching {
            val snap = db.collection(Constants.Firestore.COLLECTION_USERS).document(uid).get().await()
            val first = snap.getString(Constants.Firestore.UserProfileFields.FIRST_NAME) ?: ""
            val last = snap.getString(Constants.Firestore.UserProfileFields.LAST_NAME) ?: ""
            val email = snap.getString(Constants.Firestore.UserProfileFields.EMAIL)
            val worldRaw = snap.getString(Constants.Firestore.UserProfileFields.WORLD) ?: UserWorld.DOCTOR.name
            val world = runCatching { UserWorld.valueOf(worldRaw) }.getOrDefault(UserWorld.DOCTOR)
            UserProfile(
                firstName = first,
                lastName = last,
                email = email,
                world = world,
            )
        }
    }
}
