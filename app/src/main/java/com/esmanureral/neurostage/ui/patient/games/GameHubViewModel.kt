package com.esmanureral.neurostage.ui.patient.games

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.esmanureral.neurostage.R
import com.esmanureral.neurostage.data.AppPreferences
import com.esmanureral.neurostage.data.MrScanRecord
import com.esmanureral.neurostage.data.UserRepository
import com.esmanureral.neurostage.domain.patient.DementiaStageLabels
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class GameHubUiState(
    val stageIndex: Int? = null,
    val diagnosisLabel: String? = null,
    val stageDescription: String? = null,
    val stageChipLabel: String? = null,
    val latestConfidencePercent: Int? = null,
    val scanCount: Int = 0,
)

@HiltViewModel
class GameHubViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val prefs: AppPreferences,
    private val userRepository: UserRepository,
) : ViewModel() {

    val uiState: StateFlow<GameHubUiState> = combine(
        prefs.patientStage,
        userRepository.scanHistory,
    ) { stageIndex, history ->
        buildUiState(stageIndex, history)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS),
        initialValue = buildUiState(
            stageIndex = prefs.patientStage.value,
            history = userRepository.scanHistory.value,
        ),
    )

    private fun buildUiState(stageIndex: Int?, history: List<MrScanRecord>): GameHubUiState {
        val labels = DementiaStageLabels.labels(context)
        val descriptions = context.resources.getStringArray(R.array.home_screen_class_descriptions)
        val latest = history.firstOrNull()
        val resolvedStage = latest?.stageIndex ?: stageIndex

        val diagnosisLabel = resolvedStage?.let { labels.getOrNull(it) }
            ?: latest?.label

        return GameHubUiState(
            stageIndex = resolvedStage,
            diagnosisLabel = diagnosisLabel,
            stageDescription = resolvedStage?.let { descriptions.getOrNull(it) },
            stageChipLabel = resolvedStage?.let { stageChipLabel(it) },
            latestConfidencePercent = latest?.let {
                (it.confidence * 100f).toInt().coerceIn(0, 100)
            },
            scanCount = history.size,
        )
    }

    private fun stageChipLabel(stageIndex: Int): String? =
        DementiaStageLabels.labelAt(context, stageIndex).takeIf { it.isNotEmpty() }

    private companion object {
        const val STOP_TIMEOUT_MS = 5_000L
    }
}
