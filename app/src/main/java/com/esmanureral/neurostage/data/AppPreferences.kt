package com.esmanureral.neurostage.data

import android.content.SharedPreferences
import androidx.core.content.edit
import com.esmanureral.neurostage.domain.patient.MildPuzzleCatalog
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
        const val KEY_PATIENT_STAGE = "patient_stage"
        const val KEY_MILD_PUZZLE_PROGRESS = "mild_puzzle_progress"
    }

    private val _userWorld = MutableStateFlow(loadWorld())
    val userWorld: StateFlow<UserWorld?> = _userWorld.asStateFlow()

    private val _patientStage = MutableStateFlow(loadPatientStage())
    val patientStage: StateFlow<Int?> = _patientStage.asStateFlow()

    private val _mildPuzzleProgress = MutableStateFlow(loadMildPuzzleProgress())
    val mildPuzzleProgress: StateFlow<Int> = _mildPuzzleProgress.asStateFlow()

    fun setWorld(world: UserWorld) {
        prefs.edit { putString(KEY_USER_WORLD, world.name) }
        _userWorld.value = world
    }

    fun setPatientStage(stage: Int) {
        val previous = _patientStage.value
        prefs.edit { putInt(KEY_PATIENT_STAGE, stage) }
        _patientStage.value = stage
        if (previous != null && previous != stage) {
            prefs.edit { remove(KEY_MILD_PUZZLE_PROGRESS) }
            _mildPuzzleProgress.value = 0
        }
    }

    fun advanceMildPuzzle() {
        val maxIndex = MildPuzzleCatalog.maxProgressIndex()
        if (_mildPuzzleProgress.value < maxIndex) {
            val next = _mildPuzzleProgress.value + 1
            prefs.edit { putInt(KEY_MILD_PUZZLE_PROGRESS, next) }
            _mildPuzzleProgress.value = next
        }
    }

    fun clearWorld() {
        prefs.edit {
            remove(KEY_USER_WORLD)
            remove(KEY_PATIENT_STAGE)
            remove(KEY_MILD_PUZZLE_PROGRESS)
        }
        _userWorld.value = null
        _patientStage.value = null
        _mildPuzzleProgress.value = 0
    }

    private fun loadWorld(): UserWorld? {
        val raw = prefs.getString(KEY_USER_WORLD, null) ?: return null
        return runCatching { UserWorld.valueOf(raw) }.getOrNull()
    }

    private fun loadPatientStage(): Int? {
        return if (prefs.contains(KEY_PATIENT_STAGE)) {
            prefs.getInt(KEY_PATIENT_STAGE, -1).takeIf { it != -1 }
        } else null
    }

    private fun loadMildPuzzleProgress(): Int =
        prefs.getInt(KEY_MILD_PUZZLE_PROGRESS, 0)
            .coerceIn(0, MildPuzzleCatalog.maxProgressIndex())
}