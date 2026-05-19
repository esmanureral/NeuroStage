package com.esmanureral.neurostage.ui.patient.games.colormatch

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.esmanureral.neurostage.data.AppPreferences
import com.esmanureral.neurostage.domain.patient.colormatch.ColorMatchCatalog
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ColorMatchViewModel @Inject constructor(
    private val prefs: AppPreferences,
) : ViewModel() {

    var levelIndex by mutableIntStateOf(loadLevelIndex())
        private set

    val currentLevel get() = ColorMatchCatalog.levels[levelIndex]

    private fun loadLevelIndex(): Int {
        val max = ColorMatchCatalog.levels.lastIndex
        val stored = prefs.colorMatchLevelIndex
        return stored.coerceIn(0, max)
    }

    fun restartFromLevelOne() {
        levelIndex = 0
        prefs.setColorMatchLevelIndex(0)
    }
}
