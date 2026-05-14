

package com.esmanureral.neurostage.profile

import com.esmanureral.neurostage.data.UserWorld

data class UserProfile(
    val firstName: String,
    val lastName: String,
    val email: String?,
    val world: UserWorld,
)

interface UserProfileRepository {
    suspend fun upsert(uid: String, profile: UserProfile): Result<Unit>
    suspend fun get(uid: String): Result<UserProfile>
}