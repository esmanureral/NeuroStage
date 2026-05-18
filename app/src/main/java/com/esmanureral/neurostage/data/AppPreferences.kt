package com.esmanureral.neurostage.data

import android.content.SharedPreferences
import androidx.core.content.edit
import com.esmanureral.neurostage.domain.patient.puzzle.mild.MildPuzzleCatalog
import com.esmanureral.neurostage.domain.patient.PatientStage
import com.esmanureral.neurostage.domain.patient.memorymatch.moderate.ModerateMemoryMatchCatalog
import com.esmanureral.neurostage.domain.patient.puzzle.moderate.ModeratePuzzleCatalog
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
        const val KEY_MRI_MODERATE_PUZZLE_PROGRESS = "mri_moderate_puzzle_progress"
        const val KEY_MEMORY_MATCH_LEVEL = "memory_match_level"
        const val KEY_MEMORY_MATCH_ALL_COMPLETE = "memory_match_all_complete"
        const val MEMORY_MATCH_MAX_LEVEL_INDEX = 3
        const val KEY_MODERATE_MEMORY_MATCH_LEVEL = "moderate_memory_match_level"
        const val KEY_MODERATE_MEMORY_MATCH_ALL_COMPLETE = "moderate_memory_match_all_complete"
        const val KEY_COLOR_MATCH_LEVEL = "color_match_level"
        const val KEY_COLOR_MATCH_ALL_COMPLETE = "color_match_all_complete"
        const val KEY_HUB_MOTIVATION_QUOTE = "hub_motivation_quote"
        const val KEY_HUB_MOTIVATION_PERIOD = "hub_motivation_period"
        const val KEY_HUB_MOTIVATION_SAVED_AT = "hub_motivation_saved_at"
        const val HUB_MOTIVATION_QUOTE_TTL_MS = 2L * 60L * 60L * 1000L
    }

    data class CachedHubMotivationQuote(
        val text: String,
        val periodOrdinal: Int,
    )

    fun readCachedHubMotivationQuote(): CachedHubMotivationQuote? {
        val savedAt = prefs.getLong(KEY_HUB_MOTIVATION_SAVED_AT, 0L)
        if (savedAt == 0L) return null
        if (System.currentTimeMillis() - savedAt >= HUB_MOTIVATION_QUOTE_TTL_MS) return null
        val text = prefs.getString(KEY_HUB_MOTIVATION_QUOTE, null).orEmpty()
        if (text.isBlank()) return null
        return CachedHubMotivationQuote(
            text = text,
            periodOrdinal = prefs.getInt(KEY_HUB_MOTIVATION_PERIOD, 0),
        )
    }

    fun writeCachedHubMotivationQuote(text: String, periodOrdinal: Int) {
        prefs.edit {
            putString(KEY_HUB_MOTIVATION_QUOTE, text)
            putInt(KEY_HUB_MOTIVATION_PERIOD, periodOrdinal)
            putLong(KEY_HUB_MOTIVATION_SAVED_AT, System.currentTimeMillis())
        }
    }

    private val _userWorld = MutableStateFlow(loadWorld())
    val userWorld: StateFlow<UserWorld?> = _userWorld.asStateFlow()

    private val _patientStage = MutableStateFlow(loadPatientStage())
    val patientStage: StateFlow<Int?> = _patientStage.asStateFlow()

    private val _mildPuzzleProgress = MutableStateFlow(loadMildPuzzleProgress())
    val mildPuzzleProgress: StateFlow<Int> = _mildPuzzleProgress.asStateFlow()

    private val _mriModeratePuzzleProgress = MutableStateFlow(loadMriModeratePuzzleProgress())
    val mriModeratePuzzleProgress: StateFlow<Int> = _mriModeratePuzzleProgress.asStateFlow()

    private val _memoryMatchLevel = MutableStateFlow(loadMemoryMatchLevel())
    val memoryMatchLevel: StateFlow<Int> = _memoryMatchLevel.asStateFlow()

    private val _memoryMatchAllComplete = MutableStateFlow(loadMemoryMatchAllComplete())
    val memoryMatchAllComplete: StateFlow<Boolean> = _memoryMatchAllComplete.asStateFlow()

    private val _moderateMemoryMatchLevel = MutableStateFlow(loadModerateMemoryMatchLevel())
    val moderateMemoryMatchLevel: StateFlow<Int> = _moderateMemoryMatchLevel.asStateFlow()

    private val _moderateMemoryMatchAllComplete =
        MutableStateFlow(loadModerateMemoryMatchAllComplete())
    val moderateMemoryMatchAllComplete: StateFlow<Boolean> =
        _moderateMemoryMatchAllComplete.asStateFlow()

    fun setWorld(world: UserWorld) {
        prefs.edit { putString(KEY_USER_WORLD, world.name) }
        _userWorld.value = world
    }

    fun setPatientStage(stage: Int) {
        val previous = _patientStage.value
        prefs.edit { putInt(KEY_PATIENT_STAGE, stage) }
        _patientStage.value = stage
        if (previous != null && previous != stage) {
            prefs.edit {
                remove(KEY_MILD_PUZZLE_PROGRESS)
                remove(KEY_MEMORY_MATCH_LEVEL)
                remove(KEY_MEMORY_MATCH_ALL_COMPLETE)
                remove(KEY_MODERATE_MEMORY_MATCH_LEVEL)
                remove(KEY_MODERATE_MEMORY_MATCH_ALL_COMPLETE)
                remove(KEY_COLOR_MATCH_LEVEL)
                remove(KEY_COLOR_MATCH_ALL_COMPLETE)
            }
            _mildPuzzleProgress.value = 0
            _memoryMatchLevel.value = 0
            _memoryMatchAllComplete.value = false
            _moderateMemoryMatchLevel.value = 0
            _moderateMemoryMatchAllComplete.value = false
        }
        if (stage != PatientStage.MODERATE_DEMENTIA) {
            prefs.edit {
                remove(KEY_MRI_MODERATE_PUZZLE_PROGRESS)
                remove(KEY_MODERATE_MEMORY_MATCH_LEVEL)
                remove(KEY_MODERATE_MEMORY_MATCH_ALL_COMPLETE)
            }
            _mriModeratePuzzleProgress.value = 0
            _moderateMemoryMatchLevel.value = 0
            _moderateMemoryMatchAllComplete.value = false
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

    fun advanceMriModeratePuzzle() {
        val maxIndex = ModeratePuzzleCatalog.maxProgressIndex()
        if (_mriModeratePuzzleProgress.value < maxIndex) {
            val next = _mriModeratePuzzleProgress.value + 1
            prefs.edit { putInt(KEY_MRI_MODERATE_PUZZLE_PROGRESS, next) }
            _mriModeratePuzzleProgress.value = next
        }
    }

    fun setMemoryMatchLevel(levelIndex: Int) {
        val coerced = levelIndex.coerceIn(0, memoryMatchMaxLevelIndex())
        prefs.edit {
            putInt(KEY_MEMORY_MATCH_LEVEL, coerced)
            putBoolean(KEY_MEMORY_MATCH_ALL_COMPLETE, false)
        }
        _memoryMatchLevel.value = coerced
        _memoryMatchAllComplete.value = false
    }

    fun setMemoryMatchAllComplete() {
        prefs.edit {
            putInt(KEY_MEMORY_MATCH_LEVEL, memoryMatchMaxLevelIndex())
            putBoolean(KEY_MEMORY_MATCH_ALL_COMPLETE, true)
        }
        _memoryMatchLevel.value = memoryMatchMaxLevelIndex()
        _memoryMatchAllComplete.value = true
    }

    fun resetMemoryMatchProgress() {
        prefs.edit {
            putInt(KEY_MEMORY_MATCH_LEVEL, 0)
            putBoolean(KEY_MEMORY_MATCH_ALL_COMPLETE, false)
        }
        _memoryMatchLevel.value = 0
        _memoryMatchAllComplete.value = false
    }

    fun setModerateMemoryMatchLevel(levelIndex: Int) {
        val coerced = levelIndex.coerceIn(0, moderateMemoryMatchMaxLevelIndex())
        prefs.edit {
            putInt(KEY_MODERATE_MEMORY_MATCH_LEVEL, coerced)
            putBoolean(KEY_MODERATE_MEMORY_MATCH_ALL_COMPLETE, false)
        }
        _moderateMemoryMatchLevel.value = coerced
        _moderateMemoryMatchAllComplete.value = false
    }

    fun setModerateMemoryMatchAllComplete() {
        prefs.edit {
            putInt(KEY_MODERATE_MEMORY_MATCH_LEVEL, moderateMemoryMatchMaxLevelIndex())
            putBoolean(KEY_MODERATE_MEMORY_MATCH_ALL_COMPLETE, true)
        }
        _moderateMemoryMatchLevel.value = moderateMemoryMatchMaxLevelIndex()
        _moderateMemoryMatchAllComplete.value = true
    }

    fun resetModerateMemoryMatchProgress() {
        prefs.edit {
            putInt(KEY_MODERATE_MEMORY_MATCH_LEVEL, 0)
            putBoolean(KEY_MODERATE_MEMORY_MATCH_ALL_COMPLETE, false)
        }
        _moderateMemoryMatchLevel.value = 0
        _moderateMemoryMatchAllComplete.value = false
    }

    val colorMatchLevelIndex: Int
        get() = prefs.getInt(KEY_COLOR_MATCH_LEVEL, 0).coerceIn(0, 3)

    fun setColorMatchLevelIndex(levelIndex: Int) {
        prefs.edit {
            putInt(KEY_COLOR_MATCH_LEVEL, levelIndex.coerceAtLeast(0))
            putBoolean(KEY_COLOR_MATCH_ALL_COMPLETE, false)
        }
    }

    fun clearWorld() {
        prefs.edit {
            remove(KEY_USER_WORLD)
            remove(KEY_PATIENT_STAGE)
            remove(KEY_MILD_PUZZLE_PROGRESS)
            remove(KEY_MRI_MODERATE_PUZZLE_PROGRESS)
            remove(KEY_MEMORY_MATCH_LEVEL)
            remove(KEY_MEMORY_MATCH_ALL_COMPLETE)
            remove(KEY_MODERATE_MEMORY_MATCH_LEVEL)
            remove(KEY_MODERATE_MEMORY_MATCH_ALL_COMPLETE)
            remove(KEY_COLOR_MATCH_LEVEL)
            remove(KEY_COLOR_MATCH_ALL_COMPLETE)
        }
        _userWorld.value = null
        _patientStage.value = null
        _mildPuzzleProgress.value = 0
        _mriModeratePuzzleProgress.value = 0
        _memoryMatchLevel.value = 0
        _memoryMatchAllComplete.value = false
        _moderateMemoryMatchLevel.value = 0
        _moderateMemoryMatchAllComplete.value = false
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

    private fun loadMriModeratePuzzleProgress(): Int =
        prefs.getInt(KEY_MRI_MODERATE_PUZZLE_PROGRESS, 0)
            .coerceIn(0, ModeratePuzzleCatalog.maxProgressIndex())

    private fun memoryMatchMaxLevelIndex(): Int = MEMORY_MATCH_MAX_LEVEL_INDEX

    private fun loadMemoryMatchLevel(): Int =
        prefs.getInt(KEY_MEMORY_MATCH_LEVEL, 0)
            .coerceIn(0, memoryMatchMaxLevelIndex())

    private fun loadMemoryMatchAllComplete(): Boolean =
        prefs.getBoolean(KEY_MEMORY_MATCH_ALL_COMPLETE, false)

    private fun moderateMemoryMatchMaxLevelIndex(): Int = ModerateMemoryMatchCatalog.MAX_LEVEL_INDEX

    private fun loadModerateMemoryMatchLevel(): Int =
        prefs.getInt(KEY_MODERATE_MEMORY_MATCH_LEVEL, 0)
            .coerceIn(0, moderateMemoryMatchMaxLevelIndex())

    private fun loadModerateMemoryMatchAllComplete(): Boolean =
        prefs.getBoolean(KEY_MODERATE_MEMORY_MATCH_ALL_COMPLETE, false)
}