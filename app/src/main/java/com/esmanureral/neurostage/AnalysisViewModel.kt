package com.esmanureral.neurostage

import android.content.Context
import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.esmanureral.neurostage.data.MrScanRecord
import com.esmanureral.neurostage.data.AppPreferences
import com.esmanureral.neurostage.data.UserRepository
import com.esmanureral.neurostage.patients.PatientRepository
import com.esmanureral.neurostage.scans.ScanRecord
import com.esmanureral.neurostage.scans.ScanRepository
import com.esmanureral.neurostage.auth.AuthRepository
import com.esmanureral.neurostage.auth.AuthStatus
import com.esmanureral.neurostage.xai.GeminiReportGenerator
import com.esmanureral.neurostage.xai.GradCamRunner
import com.esmanureral.neurostage.xai.McDropoutRunner
import com.esmanureral.neurostage.xai.XaiUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExecutorCoroutineDispatcher
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.withContext
import javax.inject.Inject

sealed class AnalysisState {
    data object Idle : AnalysisState()
    data object Loading : AnalysisState()
    data class Success(
        val label: String,
        val confidence: Float,
        val allScores: FloatArray,
        val description: String,
        val stageIndex: Int,
        val bitmap: Bitmap? = null,
    ) : AnalysisState() {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Success

            if (label != other.label) return false
            if (confidence != other.confidence) return false
            if (!allScores.contentEquals(other.allScores)) return false
            if (description != other.description) return false
            if (stageIndex != other.stageIndex) return false
            if (bitmap != other.bitmap) return false

            return true
        }

        override fun hashCode(): Int {
            var result = label.hashCode()
            result = 31 * result + confidence.hashCode()
            result = 31 * result + allScores.contentHashCode()
            result = 31 * result + description.hashCode()
            result = 31 * result + stageIndex.hashCode()
            result = 31 * result + (bitmap?.hashCode() ?: 0)
            return result
        }
    }

    data class Error(val message: String) : AnalysisState()
}

@HiltViewModel
class AnalysisViewModel @Inject constructor(
    @param:ApplicationContext private val appContext: Context,
    @param:javax.inject.Named("alzheimer") private val classifier: TFLiteClassifier,
    @param:javax.inject.Named("mriFilter") private val mriFilter: TFLiteClassifier,
    private val userRepository: UserRepository,
    private val auth: AuthRepository,
    private val patients: PatientRepository,
    private val scans: ScanRepository,
    private val prefs: AppPreferences,
) : ViewModel() {

    private val _state = MutableStateFlow<AnalysisState>(AnalysisState.Idle)
    val state: StateFlow<AnalysisState> = _state

    private val _analysisProgressSteps = MutableStateFlow(0)
    val analysisProgressSteps: StateFlow<Int> = _analysisProgressSteps.asStateFlow()

    private val _validationError = MutableStateFlow<String?>(null)
    val validationError: StateFlow<String?> = _validationError

    private val _isMriValidated = MutableStateFlow(false)
    val isMriValidated: StateFlow<Boolean> = _isMriValidated

    private var activePatientId: String? = null

    private val _saveError = MutableStateFlow<String?>(null)
    val saveError: StateFlow<String?> = _saveError.asStateFlow()

    private val _xaiState = MutableStateFlow(XaiUiState())
    val xaiState: StateFlow<XaiUiState> = _xaiState.asStateFlow()

    private val mcRunner by lazy { McDropoutRunner(appContext) }
    private val gradCamRunner by lazy { GradCamRunner(appContext) }
    private val geminiGenerator by lazy { GeminiReportGenerator(appContext) }


    @OptIn(
        kotlinx.coroutines.ExperimentalCoroutinesApi::class,
        kotlinx.coroutines.DelicateCoroutinesApi::class
    )
    private val tfLiteDispatcher: ExecutorCoroutineDispatcher =
        newSingleThreadContext("TFLiteThread")

    fun setActivePatient(patientId: String?) {
        activePatientId = patientId
    }

    fun validateAndSetBitmap(bitmap: Bitmap) {
        _state.value = AnalysisState.Idle
        val heuristic = ImageValidator.validate(bitmap)
        if (!heuristic.isValid) {
            val errorRes = heuristic.reasonResId ?: R.string.error_invalid_mri
            _validationError.value = appContext.getString(errorRes)
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
                _validationError.value = appContext.getString(R.string.error_invalid_mri)
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
            _validationError.value = appContext.getString(R.string.error_select_mri_first)
            return
        }
        _xaiState.value = XaiUiState()
        viewModelScope.launch {
            _state.value = AnalysisState.Loading
            _analysisProgressSteps.value = 0
            val stepTicker = launch {
                try {
                    repeat(STEPS_TOTAL) { step ->
                        delay(STEP_DELAY_MS)
                        _analysisProgressSteps.value = step + 1
                    }
                } catch (_: CancellationException) {
                }
            }
            try {
                val scores = withContext(Dispatchers.Default) {
                    classifier.classify(bitmap)
                }
                stepTicker.join()
                delay(STEP_HOLD_ALL_DONE_MS)
                val labels = appContext.resources.getStringArray(R.array.dementia_stage_labels)
                val descriptions =
                    appContext.resources.getStringArray(R.array.home_screen_class_descriptions)
                val maxIndex = scores.indices.maxByOrNull { scores[it] } ?: 0
                val result = AnalysisState.Success(
                    label = labels[maxIndex],
                    confidence = scores[maxIndex],
                    allScores = scores,
                    description = descriptions[maxIndex],
                    stageIndex = maxIndex,
                    bitmap = bitmap,
                )

                prefs.setPatientStage(maxIndex)

                val ts = System.currentTimeMillis()
                userRepository.addScanRecord(
                    MrScanRecord(
                        timestamp = ts,
                        stageIndex = maxIndex,
                        label = labels[maxIndex],
                        confidence = scores[maxIndex],
                        scores = scores,
                    )
                )

                val uid = (auth.status.value as? AuthStatus.SignedIn)?.user?.uid
                val patientId = activePatientId
                if (uid != null && patientId != null) {
                    val patient = patients.get(uid, patientId).getOrNull()
                    val patientName = patient?.fullName ?: ""
                    val saveResult = scans.add(
                        ScanRecord(
                            id = ts.toString(),
                            doctorUid = uid,
                            patientId = patientId,
                            patientName = patientName,
                            timestampMs = ts,
                            stageIndex = maxIndex,
                            label = labels[maxIndex],
                            confidence = scores[maxIndex],
                            scores = scores.map { it },
                        )
                    )
                    if (saveResult.isFailure) {
                        _saveError.value = appContext.getString(
                            R.string.error_firebase_save,
                            saveResult.exceptionOrNull()?.message
                        )
                    } else {
                        _saveError.value = null
                    }
                }
                _state.value = result

                val patient = if (uid != null && patientId != null)
                    patients.get(uid, patientId).getOrNull() else null
                launchXaiPipeline(
                    bitmap,
                    maxIndex,
                    scores,
                    patient?.age,
                    patient?.gender,
                    patientId,
                    ts.toString()
                )
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                stepTicker.cancelAndJoin()
                _analysisProgressSteps.value = 0
                _state.value = AnalysisState.Error(
                    e.message ?: appContext.getString(R.string.error_classification_failed)
                )
            }
        }
    }

    private fun launchXaiPipeline(
        bitmap: Bitmap,
        stageIndex: Int,
        scores: FloatArray,
        patientAge: Int?,
        patientGender: String?,
        patientId: String?,
        scanId: String?,
    ) {
        viewModelScope.launch {
            _xaiState.value = _xaiState.value.copy(isMcLoading = true)
            val mcResult = withContext(tfLiteDispatcher) {
                runCatching { mcRunner.run(bitmap) }
            }
            val mc = mcResult.getOrNull()
            _xaiState.value = _xaiState.value.copy(
                isMcLoading = false,
                mcResult = mc,
                mcError = mcResult.exceptionOrNull()?.message,
            )

            _xaiState.value = _xaiState.value.copy(isGradCamLoading = true)
            val gradCamResult = withContext(tfLiteDispatcher) {
                runCatching { gradCamRunner.run(bitmap, stageIndex) }
            }
            val gradCam = gradCamResult.getOrNull()
            _xaiState.value = _xaiState.value.copy(
                isGradCamLoading = false,
                gradCamResult = gradCam,
                gradCamError = gradCamResult.exceptionOrNull()?.message,
            )

            _xaiState.value = _xaiState.value.copy(isGeminiLoading = true)
            val classLabels = appContext.resources.getStringArray(R.array.dementia_stage_labels)
            val stageLabel = classLabels.getOrNull(stageIndex) ?: ""
            val geminiResult = runCatching {
                geminiGenerator.generate(
                    bitmap = bitmap,
                    stageLabel = stageLabel,
                    topMean = scores[stageIndex],
                    topStd = mc?.topStd ?: 0f,
                    allScores = scores,
                    allStdScores = mc?.stdScores,
                    patientAge = patientAge,
                    patientGender = patientGender,
                    activeRegion = gradCam?.activeRegion,
                    saliencyPeakScore = gradCam?.rawCam?.max(),
                )
            }
            val report = geminiResult.getOrNull()
            _xaiState.value = _xaiState.value.copy(
                isGeminiLoading = false,
                geminiReport = report,
                geminiError = geminiResult.exceptionOrNull()?.message,
            )

            if (report != null && patientId != null && scanId != null) {
                val uid = (auth.status.value as? AuthStatus.SignedIn)?.user?.uid
                if (uid != null) {
                    scans.updateAiReport(uid, patientId, scanId, report.text)
                }
            }
        }
    }

    fun reset() {
        _state.value = AnalysisState.Idle
        _validationError.value = null
        _saveError.value = null
        _isMriValidated.value = false
        _analysisProgressSteps.value = 0
        _xaiState.value = XaiUiState()
    }

    override fun onCleared() {
        super.onCleared()
        runCatching { mcRunner.close() }
        runCatching { gradCamRunner.close() }
        tfLiteDispatcher.close()
    }

    companion object {
        private const val MRI_FILTER_THRESHOLD = 0.45f
        private const val STEPS_TOTAL = 4
        private const val STEP_DELAY_MS = 420L
        private const val STEP_HOLD_ALL_DONE_MS = 480L
    }
}