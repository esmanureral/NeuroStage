package com.esmanureral.neurostage.reminder

import android.content.Context
import com.esmanureral.neurostage.data.patient.PatientReminder
import com.esmanureral.neurostage.data.patient.ReminderRepeatMode
import com.esmanureral.neurostage.util.Constants
import org.json.JSONArray

object ReminderPrefs {
    const val KEY_REMINDERS = "patient_reminders_json"

    fun loadAll(context: Context): List<PatientReminder> {
        val json = context.getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE)
            .getString(KEY_REMINDERS, null) ?: return emptyList()
        return try {
            val arr = JSONArray(json)
            (0 until arr.length()).mapNotNull { i ->
                parseReminder(arr.getJSONObject(i))
            }
        } catch (_: Exception) {
            emptyList()
        }
    }

    fun loadById(context: Context, id: Long): PatientReminder? =
        loadAll(context).firstOrNull { it.id == id }

    private fun parseReminder(obj: org.json.JSONObject): PatientReminder? = try {
        val modeStr = obj.optString("repeatMode", ReminderRepeatMode.DAILY.name)
        val mode = runCatching { ReminderRepeatMode.valueOf(modeStr) }
            .getOrDefault(ReminderRepeatMode.DAILY)
        val daysArr = obj.optJSONArray("daysOfWeek")
        val days = buildSet {
            if (daysArr != null) {
                for (i in 0 until daysArr.length()) add(daysArr.getInt(i))
            }
        }
        val oneTime = obj.optLong("oneTimeDateMillis", -1L).takeIf { it > 0L }
        PatientReminder(
            id = obj.getLong("id"),
            title = obj.getString("title"),
            hour = obj.getInt("hour"),
            minute = obj.getInt("minute"),
            enabled = obj.optBoolean("enabled", true),
            repeatMode = mode,
            daysOfWeek = days,
            oneTimeDateMillis = oneTime,
        )
    } catch (_: Exception) {
        null
    }
}
