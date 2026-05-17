package com.esmanureral.neurostage

import android.app.Application
import com.esmanureral.neurostage.reminder.ReminderAlarms
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class NeuroStageApp : Application() {
    override fun onCreate() {
        super.onCreate()
        ReminderAlarms.rescheduleAllEnabled(this)
    }
}