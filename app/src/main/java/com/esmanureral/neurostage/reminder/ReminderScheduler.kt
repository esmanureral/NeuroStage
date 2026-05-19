package com.esmanureral.neurostage.reminder

import android.content.Context
import com.esmanureral.neurostage.data.patient.PatientReminder
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReminderScheduler @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    fun schedule(reminder: PatientReminder) = ReminderAlarms.schedule(context, reminder)

    fun cancel(reminderId: Long) = ReminderAlarms.cancel(context, reminderId)

    fun rescheduleAllEnabled() = ReminderAlarms.rescheduleAllEnabled(context)
}
