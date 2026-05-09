package com.esmanureral.neurostage.data

import android.content.SharedPreferences
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
        private const val KEY_USER_NAME     = "user_name"
        private const val KEY_LAST_MR_STAGE = "last_mr_stage_index"
        private const val KEY_SCAN_HISTORY  = "scan_history_json"
        private const val NO_STAGE          = -1
        private const val MAX_HISTORY       = 50
    }

    private val _userName = MutableStateFlow(prefs.getString(KEY_USER_NAME, "") ?: "")
    val userName: StateFlow<String> = _userName.asStateFlow()

    private val _lastMrStageIndex = MutableStateFlow(
        prefs.getInt(KEY_LAST_MR_STAGE, NO_STAGE).takeIf { it != NO_STAGE }
    )
    val lastMrStageIndex: StateFlow<Int?> = _lastMrStageIndex.asStateFlow()

    private val _scanHistory = MutableStateFlow(loadHistory())
    val scanHistory: StateFlow<List<MrScanRecord>> = _scanHistory.asStateFlow()

    fun saveUserName(name: String) {
        prefs.edit().putString(KEY_USER_NAME, name.trim()).apply()
        _userName.value = name.trim()
    }

    fun saveMrStageIndex(index: Int) {
        prefs.edit().putInt(KEY_LAST_MR_STAGE, index).apply()
        _lastMrStageIndex.value = index
    }

    fun addScanRecord(record: MrScanRecord) {
        val updated = (listOf(record) + _scanHistory.value).take(MAX_HISTORY)
        persistHistory(updated)
        _scanHistory.value = updated
        saveMrStageIndex(record.stageIndex)
    }

    fun isFirstLaunch(): Boolean = prefs.getString(KEY_USER_NAME, null) == null

    private fun loadHistory(): List<MrScanRecord> {
        val json = prefs.getString(KEY_SCAN_HISTORY, null) ?: return emptyList()
        return try {
            val arr = JSONArray(json)
            (0 until arr.length()).map { i ->
                val obj = arr.getJSONObject(i)
                MrScanRecord(
                    timestamp  = obj.getLong("ts"),
                    stageIndex = obj.getInt("stage"),
                    label      = obj.getString("label"),
                    confidence = obj.getDouble("conf").toFloat(),
                )
            }
        } catch (_: Exception) { emptyList() }
    }

    private fun persistHistory(list: List<MrScanRecord>) {
        val arr = JSONArray()
        list.forEach { r ->
            arr.put(JSONObject().apply {
                put("ts",    r.timestamp)
                put("stage", r.stageIndex)
                put("label", r.label)
                put("conf",  r.confidence.toDouble())
            })
        }
        prefs.edit().putString(KEY_SCAN_HISTORY, arr.toString()).apply()
    }
}