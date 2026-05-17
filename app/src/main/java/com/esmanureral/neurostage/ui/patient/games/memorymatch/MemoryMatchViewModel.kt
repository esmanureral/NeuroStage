package com.esmanureral.neurostage.ui.patient.games.memorymatch

import androidx.lifecycle.ViewModel
import com.esmanureral.neurostage.data.patient.BrainExerciseRepository
import com.esmanureral.neurostage.domain.patient.PatientStage
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MemoryMatchViewModel @Inject constructor(
    private val repository: BrainExerciseRepository,
) : ViewModel() {


    fun maxLevelIndex(stageIndex: Int?): Int = memoryMatchMaxLevelIndex(stageIndex)

    fun currentLevelIndex(stageIndex: Int?): Int {
        val maxIndex = maxLevelIndex(stageIndex)
        val raw = if (stageIndex == PatientStage.MODERATE_DEMENTIA) {
            repository.moderateMemoryMatchLevel.value
        } else {
            repository.memoryMatchLevel.value
        }
        return raw.coerceIn(0, maxIndex)
    }

    fun isAllComplete(stageIndex: Int?): Boolean {
        return if (stageIndex == PatientStage.MODERATE_DEMENTIA) {
            repository.moderateMemoryMatchAllComplete.value
        } else {
            repository.memoryMatchAllComplete.value
        }
    }

    fun saveLevel(stageIndex: Int?, levelIndex: Int) {
        val coerced = levelIndex.coerceIn(0, maxLevelIndex(stageIndex))
        if (stageIndex == PatientStage.MODERATE_DEMENTIA) {
            repository.setModerateMemoryMatchLevel(coerced)
        } else {
            repository.setMemoryMatchLevel(coerced)
        }
    }

    fun onLevelCompleted(stageIndex: Int?, completedLevelIndex: Int) {
        val maxIndex = maxLevelIndex(stageIndex)
        if (completedLevelIndex >= maxIndex) {
            if (stageIndex == PatientStage.MODERATE_DEMENTIA) {
                repository.setModerateMemoryMatchAllComplete()
            } else {
                repository.setMemoryMatchAllComplete()
            }
        } else {
            saveLevel(stageIndex, completedLevelIndex + 1)
        }
    }

    fun resetProgress(stageIndex: Int?) {
        if (stageIndex == PatientStage.MODERATE_DEMENTIA) {
            repository.resetModerateMemoryMatchProgress()
        } else {
            repository.resetMemoryMatchProgress()
        }
    }

    fun coerceLevelIndex(stageIndex: Int?, levelIndex: Int): Int =
        levelIndex.coerceIn(0, maxLevelIndex(stageIndex))
}
