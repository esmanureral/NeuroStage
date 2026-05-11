package com.esmanureral.neurostage.data

import android.content.SharedPreferences
import androidx.core.content.edit
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

enum class UserWorld { DOCTOR, PATIENT }

@Singleton
class AppPreferences @Inject constructor(
    private val prefs: SharedPreferences,
) {
    private companion object {
        const val KEY_USER_WORLD = "user_world"
    }

    private val _userWorld = MutableStateFlow(loadWorld())
    val userWorld: StateFlow<UserWorld?> = _userWorld.asStateFlow()

    fun setWorld(world: UserWorld) {
        prefs.edit { putString(KEY_USER_WORLD, world.name) }
        _userWorld.value = world
    }

    fun clearWorld() {
        prefs.edit { remove(KEY_USER_WORLD) }
        _userWorld.value = null
    }

    private fun loadWorld(): UserWorld? {
        val raw = prefs.getString(KEY_USER_WORLD, null) ?: return null
        return runCatching { UserWorld.valueOf(raw) }.getOrNull()
    }
}