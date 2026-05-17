package com.esmanureral.neurostage.reminder

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.esmanureral.neurostage.R
import com.esmanureral.neurostage.data.patient.ReminderRepeatMode

class ReminderAlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val reminderId = intent.getLongExtra(EXTRA_REMINDER_ID, 0L)
        val reminder = ReminderPrefs.loadById(context, reminderId) ?: return

        showNotification(
            context = context,
            notificationId = reminder.id.hashCode(),
            title = reminder.title.ifBlank {
                context.getString(R.string.patient_reminder_default_title)
            },
        )

        if (reminder.repeatMode != ReminderRepeatMode.ONCE) {
            ReminderAlarms.schedule(context, reminder)
        } else {
            ReminderAlarms.cancel(context, reminder.id)
        }
    }

    private fun showNotification(context: Context, notificationId: Int, title: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val granted = ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.POST_NOTIFICATIONS,
            ) == PackageManager.PERMISSION_GRANTED
            if (!granted) return
        }

        ensureChannel(context)
        val body = context.getString(R.string.patient_reminder_notification_body, title)
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(context.getString(R.string.patient_reminder_notification_title))
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setAutoCancel(true)
            .setVibrate(longArrayOf(0, 300, 150, 300))
            .build()

        NotificationManagerCompat.from(context).notify(notificationId, notification)
    }

    private fun ensureChannel(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val channel = NotificationChannel(
            CHANNEL_ID,
            context.getString(R.string.patient_reminder_channel_name),
            NotificationManager.IMPORTANCE_HIGH,
        ).apply {
            description = context.getString(R.string.patient_reminder_channel_name)
            enableVibration(true)
        }
        context.getSystemService(NotificationManager::class.java)?.createNotificationChannel(channel)
    }

    companion object {
        const val CHANNEL_ID = "patient_reminders"
        const val EXTRA_REMINDER_ID = "reminder_id"
    }
}
