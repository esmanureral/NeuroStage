package com.esmanureral.neurostage

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.esmanureral.neurostage.data.MrScanRecord
import com.esmanureral.neurostage.data.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

sealed class AnalysisState {
    data object Idle    : AnalysisState()
    data object Loading : AnalysisState()
    data class Success(
        val label       : String,
        val confidence  : Float,
        val allScores   : FloatArray,
        val description : String,
        val stageIndex  : Int,
    ) : AnalysisState()
    data class Error(val message: String) : AnalysisState()
}

@HiltViewModel
class AnalysisViewModel @Inject constructor(
    @javax.inject.Named("alzheimer") private val classifier: TFLiteClassifier,
    @javax.inject.Named("mriFilter") private val mriFilter: TFLiteClassifier,
    private val userRepository: UserRepository,
) : ViewModel() {

    private val _state = MutableStateFlow<AnalysisState>(AnalysisState.Idle)
    val state: StateFlow<AnalysisState> = _state

    private val _analysisProgressSteps = MutableStateFlow(0)
    val analysisProgressSteps: StateFlow<Int> = _analysisProgressSteps.asStateFlow()

    private val _validationError = MutableStateFlow<String?>(null)
    val validationError: StateFlow<String?> = _validationError

    private val _isMriValidated = MutableStateFlow(false)
    val isMriValidated: StateFlow<Boolean> = _isMriValidated

    fun validateAndSetBitmap(bitmap: Bitmap) {
        _state.value = AnalysisState.Idle
        val heuristic = ImageValidator.validate(bitmap)
        if (!heuristic.isValid) {
            _validationError.value = heuristic.reason
            _isMriValidated.value = false
            return
        }
        viewModelScope.launch {
            _state.value = AnalysisState.Loading
            val isMri = withContext(Dispatchers.Default) {
                val scores = mriFilter.classify(bitmap)
                scores[0] >= MRI_FILTER_THRESHOLD
            }
            if (!isMri) {
                _validationError.value = "Lütfen geçerli bir beyin MR görüntüsü yükleyin."
                _isMriValidated.value = false
                _state.value = AnalysisState.Idle
            } else {
                _validationError.value = null
                _isMriValidated.value = true
                _state.value = AnalysisState.Idle
            }
        }
    }

    fun analyze(bitmap: Bitmap) {
        if (!_isMriValidated.value) {
            _validationError.value = "Önce geçerli bir beyin MR görüntüsü seçin."
            return
        }
        viewModelScope.launch {
            _state.value = AnalysisState.Loading
            _analysisProgressSteps.value = 0
            val stepTicker = launch {
                try {
                    repeat(STEPS_TOTAL) { step ->
                        delay(STEP_DELAY_MS)
                        _analysisProgressSteps.value = step + 1
                    }
                } catch (_: CancellationException) { }
            }
            try {
                val scores = withContext(Dispatchers.Default) {
                    classifier.classify(bitmap)
                }
                stepTicker.join()
                delay(STEP_HOLD_ALL_DONE_MS)
                val maxIndex = scores.indices.maxByOrNull { scores[it] } ?: 0
                val result = AnalysisState.Success(
                    label       = TR_LABELS[maxIndex],
                    confidence  = scores[maxIndex],
                    allScores   = scores,
                    description = DESCRIPTIONS[maxIndex],
                    stageIndex  = maxIndex,
                )
                userRepository.addScanRecord(
                    MrScanRecord(
                        timestamp  = System.currentTimeMillis(),
                        stageIndex = maxIndex,
                        label      = TR_LABELS[maxIndex],
                        confidence = scores[maxIndex],
                    )
                )
                _state.value = result
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                stepTicker.cancelAndJoin()
                _analysisProgressSteps.value = 0
                _state.value = AnalysisState.Error(e.message ?: "Sınıflandırma başarısız oldu.")
            }
        }
    }

    fun reset() {
        _state.value = AnalysisState.Idle
        _validationError.value = null
        _isMriValidated.value = false
        _analysisProgressSteps.value = 0
    }

    companion object {
        // Model çıktı sırası: Mild, Moderate, Non_Demented, Very_Mild_Demented
        val TR_LABELS    = listOf("Hafif evre", "Orta evre", "Sağlıklı", "Çok hafif evre")
        val DESCRIPTIONS = listOf(
            "Hafif derecede bilişsel gerileme tespit edilmiştir. Günlük aktivitelerde bazı güçlükler gözlemlenebilir.",
            "Orta derecede bilişsel gerileme tespit edilmiştir. Düzenli tıbbi takip önerilmektedir.",
            "Beyin fonksiyonları normal sınırlar içindedir. Koruyucu egzersizlerle bilişsel sağlığınızı destekleyebilirsiniz.",
            "Çok hafif bilişsel değişiklikler görülmektedir. Erken dönem takip faydalı olabilir.",
        )

        private const val MRI_FILTER_THRESHOLD  = 0.45f
        private const val STEPS_TOTAL           = 4
        private const val STEP_DELAY_MS         = 420L
        private const val STEP_HOLD_ALL_DONE_MS = 480L
    }
}