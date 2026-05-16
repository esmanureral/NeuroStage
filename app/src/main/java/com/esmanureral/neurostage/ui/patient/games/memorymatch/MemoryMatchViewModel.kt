package com.esmanureral.neurostage.ui.patient.games.memorymatch

import androidx.lifecycle.ViewModel
import com.esmanureral.neurostage.data.patient.BrainExerciseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MemoryMatchViewModel @Inject constructor(
    private val repository: BrainExerciseRepository,
) : ViewModel() {

    fun currentLevelIndex(): Int = repository.memoryMatchLevel.value

    fun isAllComplete(): Boolean = repository.memoryMatchAllComplete.value

    fun saveLevel(levelIndex: Int) {
        repository.setMemoryMatchLevel(levelIndex)
    }

    fun onLevelCompleted(completedLevelIndex: Int) {
        if (completedLevelIndex >= memoryMatchAllLevels.lastIndex) {
            repository.setMemoryMatchAllComplete()
        } else {
            repository.setMemoryMatchLevel(completedLevelIndex + 1)
        }
    }

    fun resetProgress() {
        repository.resetMemoryMatchProgress()
    }
}
