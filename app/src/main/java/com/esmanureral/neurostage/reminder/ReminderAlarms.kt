package com.esmanureral.neurostage.reminder

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.esmanureral.neurostage.data.patient.PatientReminder
import com.esmanureral.neurostage.data.patient.ReminderRepeatMode
import com.esmanureral.neurostage.data.patient.ReminderWeekdays
import java.util.Calendar

object ReminderAlarms {

    fun schedule(context: Context, reminder: PatientReminder) {
        if (!reminder.enabled) {
            cancel(context, reminder.id)
            return
        }
        val triggerAt = nextTriggerMillis(reminder)
        if (triggerAt == Long.MAX_VALUE) {
            cancel(context, reminder.id)
            return
        }
        val appContext = context.applicationContext
        val alarmManager = appContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val pending = pendingIntent(
            appContext,
            reminder,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        ) ?: return

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && alarmManager.canScheduleExactAlarms()) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pending)
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pending)
            } else {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerAt, pending)
            }
        } catch (_: SecurityException) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pending)
            } else {
                alarmManager.set(AlarmManager.RTC_WAKEUP, triggerAt, pending)
            }
        }
    }

    fun cancel(context: Context, reminderId: Long) {
        val appContext = context.applicationContext
        val alarmManager = appContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val pending = pendingIntent(
            appContext,
            PatientReminder(reminderId, "", 0, 0),
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE,
        ) ?: return
        alarmManager.cancel(pending)
        pending.cancel()
    }

    fun rescheduleAllEnabled(context: Context) {
        ReminderPrefs.loadAll(context)
            .filter { it.enabled }
            .forEach { schedule(context, it) }
    }

    private fun pendingIntent(context: Context, reminder: PatientReminder, flags: Int): PendingIntent? {
        val intent = Intent(context, ReminderAlarmReceiver::class.java).apply {
            putExtra(ReminderAlarmReceiver.EXTRA_REMINDER_ID, reminder.id)
        }
        return PendingIntent.getBroadcast(
            context,
            reminder.id.hashCode(),
            intent,
            flags,
        )
    }

    fun nextTriggerMillis(reminder: PatientReminder): Long {
        val now = Calendar.getInstance()
        return when (reminder.repeatMode) {
            ReminderRepeatMode.DAILY -> nextDailyTrigger(reminder.hour, reminder.minute, now)
            ReminderRepeatMode.WEEKLY -> {
                val days = reminder.daysOfWeek.ifEmpty {
                    setOf(now.get(Calendar.DAY_OF_WEEK))
                }
                nextWeeklyTrigger(days, reminder.hour, reminder.minute, now)
            }
            ReminderRepeatMode.ONCE -> nextOneTimeTrigger(reminder, now)
        }
    }

    private fun nextDailyTrigger(hour: Int, minute: Int, now: Calendar): Long {
        val cal = Calendar.getInstance().apply {
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
        }
        if (cal.timeInMillis <= now.timeInMillis) {
            cal.add(Calendar.DAY_OF_YEAR, 1)
        }
        return cal.timeInMillis
    }

    private fun nextWeeklyTrigger(
        days: Set<Int>,
        hour: Int,
        minute: Int,
        now: Calendar,
    ): Long {
        var best = Long.MAX_VALUE
        for (offset in 0..13) {
            val candidate = Calendar.getInstance().apply {
                timeInMillis = now.timeInMillis
                add(Calendar.DAY_OF_YEAR, offset)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
                set(Calendar.HOUR_OF_DAY, hour)
                set(Calendar.MINUTE, minute)
            }
            if (candidate.get(Calendar.DAY_OF_WEEK) in days && candidate.timeInMillis > now.timeInMillis) {
                if (candidate.timeInMillis < best) best = candidate.timeInMillis
            }
        }
        return best
    }

    private fun nextOneTimeTrigger(reminder: PatientReminder, now: Calendar): Long {
        val base = reminder.oneTimeDateMillis ?: startOfDay(now.timeInMillis)
        val cal = Calendar.getInstance().apply {
            timeInMillis = base
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            set(Calendar.HOUR_OF_DAY, reminder.hour)
            set(Calendar.MINUTE, reminder.minute)
        }
        return if (cal.timeInMillis > now.timeInMillis) cal.timeInMillis else Long.MAX_VALUE
    }

    fun startOfDay(timeMillis: Long): Long {
        return Calendar.getInstance().apply {
            timeInMillis = timeMillis
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }

    fun defaultWeekdaysSelection(): Set<Int> = ReminderWeekdays.toSet()
}
