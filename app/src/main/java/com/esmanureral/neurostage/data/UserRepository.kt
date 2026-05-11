package com.esmanureral.neurostage.data

import android.content.SharedPreferences
import androidx.core.content.edit
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONArray
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val prefs: SharedPreferences,
) {
    companion object {
        private const val KEY_LAST_MR_STAGE = "last_mr_stage_index"
        private const val KEY_SCAN_HISTORY = "scan_history_json"
        private const val NO_STAGE = -1
        private const val MAX_HISTORY = 50
    }

    private val _lastMrStageIndex = MutableStateFlow(
        prefs.getInt(KEY_LAST_MR_STAGE, NO_STAGE).takeIf { it != NO_STAGE }
    )
    val lastMrStageIndex: StateFlow<Int?> = _lastMrStageIndex.asStateFlow()

    private val _scanHistory = MutableStateFlow(loadHistory())
    val scanHistory: StateFlow<List<MrScanRecord>> = _scanHistory.asStateFlow()

    fun saveMrStageIndex(index: Int) {
        prefs.edit { putInt(KEY_LAST_MR_STAGE, index) }
        _lastMrStageIndex.value = index
    }

    fun addScanRecord(record: MrScanRecord) {
        val updated = (listOf(record) + _scanHistory.value).take(MAX_HISTORY)
        persistHistory(updated)
        _scanHistory.value = updated
        saveMrStageIndex(record.stageIndex)
    }

    private fun loadHistory(): List<MrScanRecord> {
        val json = prefs.getString(KEY_SCAN_HISTORY, null) ?: return emptyList()
        return try {
            val arr = JSONArray(json)
            (0 until arr.length()).map { i ->
                val obj = arr.getJSONObject(i)
                val scores = obj.optJSONArray("scores")?.let { sArr ->
                    FloatArray(sArr.length()) { idx -> sArr.optDouble(idx, 0.0).toFloat() }
                }
                MrScanRecord(
                    timestamp = obj.getLong("ts"),
                    stageIndex = obj.getInt("stage"),
                    label = obj.getString("label"),
                    confidence = obj.getDouble("conf").toFloat(),
                    scores = scores,
                )
            }
        } catch (_: Exception) {
            emptyList()
        }
    }

    private fun persistHistory(list: List<MrScanRecord>) {
        val arr = JSONArray()
        list.forEach { r ->
            arr.put(JSONObject().apply {
                put("ts", r.timestamp)
                put("stage", r.stageIndex)
                put("label", r.label)
                put("conf", r.confidence.toDouble())
                r.scores?.let { sc ->
                    val scArr = JSONArray()
                    sc.forEach { v -> scArr.put(v.toDouble()) }
                    put("scores", scArr)
                }
            })
        }
        prefs.edit { putString(KEY_SCAN_HISTORY, arr.toString()) }
    }
}