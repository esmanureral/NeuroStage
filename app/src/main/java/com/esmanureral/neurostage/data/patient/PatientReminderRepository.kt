package com.esmanureral.neurostage.data.patient

import android.content.SharedPreferences
import androidx.core.content.edit
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.esmanureral.neurostage.reminder.ReminderPrefs
import org.json.JSONArray
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PatientReminderRepository @Inject constructor(
    private val prefs: SharedPreferences,
) {
    private val _reminders = MutableStateFlow(loadReminders())
    val reminders: StateFlow<List<PatientReminder>> = _reminders.asStateFlow()

    fun addReminder(
        title: String,
        hour: Int,
        minute: Int,
        repeatMode: ReminderRepeatMode,
        daysOfWeek: Set<Int>,
        oneTimeDateMillis: Long?,
    ): PatientReminder {
        val reminder = PatientReminder(
            id = System.currentTimeMillis(),
            title = title.trim(),
            hour = hour,
            minute = minute,
            repeatMode = repeatMode,
            daysOfWeek = daysOfWeek,
            oneTimeDateMillis = oneTimeDateMillis,
        )
        val updated = (listOf(reminder) + _reminders.value).take(MAX_REMINDERS)
        persist(updated)
        _reminders.value = updated
        return reminder
    }

    fun findById(id: Long): PatientReminder? = _reminders.value.firstOrNull { it.id == id }

    fun removeReminder(id: Long) {
        val updated = _reminders.value.filterNot { it.id == id }
        persist(updated)
        _reminders.value = updated
    }

    fun toggleReminder(id: Long, enabled: Boolean) {
        val updated = _reminders.value.map {
            if (it.id == id) it.copy(enabled = enabled) else it
        }
        persist(updated)
        _reminders.value = updated
    }

    fun updateReminder(reminder: PatientReminder) {
        val updated = _reminders.value.map {
            if (it.id == reminder.id) reminder else it
        }
        persist(updated)
        _reminders.value = updated
    }

    private fun loadReminders(): List<PatientReminder> {
        val json = prefs.getString(KEY_REMINDERS, null) ?: return emptyList()
        return try {
            val arr = JSONArray(json)
            (0 until arr.length()).map { i -> parseReminder(arr.getJSONObject(i)) }
        } catch (_: Exception) {
            emptyList()
        }
    }

    private fun parseReminder(obj: JSONObject): PatientReminder {
        val modeStr = obj.optString("repeatMode", ReminderRepeatMode.DAILY.name)
        val mode = runCatching { ReminderRepeatMode.valueOf(modeStr) }
            .getOrDefault(ReminderRepeatMode.DAILY)
        val daysArr = obj.optJSONArray("daysOfWeek")
        val days = buildSet {
            if (daysArr != null) {
                for (i in 0 until daysArr.length()) {
                    add(daysArr.getInt(i))
                }
            }
        }
        val oneTime = obj.optLong("oneTimeDateMillis", -1L).takeIf { it > 0L }
        return PatientReminder(
            id = obj.getLong("id"),
            title = obj.getString("title"),
            hour = obj.getInt("hour"),
            minute = obj.getInt("minute"),
            enabled = obj.optBoolean("enabled", true),
            repeatMode = mode,
            daysOfWeek = days,
            oneTimeDateMillis = oneTime,
        )
    }

    private fun persist(list: List<PatientReminder>) {
        val arr = JSONArray()
        list.forEach { r ->
            arr.put(
                JSONObject().apply {
                    put("id", r.id)
                    put("title", r.title)
                    put("hour", r.hour)
                    put("minute", r.minute)
                    put("enabled", r.enabled)
                    put("repeatMode", r.repeatMode.name)
                    put("daysOfWeek", JSONArray(r.daysOfWeek.toList()))
                    r.oneTimeDateMillis?.let { put("oneTimeDateMillis", it) }
                },
            )
        }
        prefs.edit { putString(KEY_REMINDERS, arr.toString()) }
    }

    private companion object {
        const val KEY_REMINDERS = ReminderPrefs.KEY_REMINDERS
        const val MAX_REMINDERS = 20
    }
}
