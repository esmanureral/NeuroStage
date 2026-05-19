package com.esmanureral.neurostage.ui.patient.reminders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.esmanureral.neurostage.data.patient.PatientReminderRepository
import com.esmanureral.neurostage.data.patient.ReminderRepeatMode
import com.esmanureral.neurostage.reminder.ReminderScheduler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PatientRemindersViewModel @Inject constructor(
    private val repository: PatientReminderRepository,
    private val scheduler: ReminderScheduler,
) : ViewModel() {

    init {
        scheduler.rescheduleAllEnabled()
    }

    val reminders = repository.reminders.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = repository.reminders.value,
    )

    fun addReminder(
        title: String,
        hour: Int,
        minute: Int,
        repeatMode: ReminderRepeatMode,
        daysOfWeek: Set<Int>,
        oneTimeDateMillis: Long?,
    ) {
        viewModelScope.launch {
            val reminder = repository.addReminder(
                title = title,
                hour = hour,
                minute = minute,
                repeatMode = repeatMode,
                daysOfWeek = daysOfWeek,
                oneTimeDateMillis = oneTimeDateMillis,
            )
            scheduler.schedule(reminder)
        }
    }

    fun removeReminder(id: Long) {
        viewModelScope.launch {
            scheduler.cancel(id)
            repository.removeReminder(id)
        }
    }

    fun toggleReminder(id: Long, enabled: Boolean) {
        viewModelScope.launch {
            repository.toggleReminder(id, enabled)
            val reminder = repository.findById(id) ?: return@launch
            if (enabled) scheduler.schedule(reminder) else scheduler.cancel(id)
        }
    }

    fun updateReminder(
        id: Long,
        title: String,
        hour: Int,
        minute: Int,
        repeatMode: ReminderRepeatMode,
        daysOfWeek: Set<Int>,
        oneTimeDateMillis: Long?,
    ) {
        viewModelScope.launch {
            val existing = repository.findById(id) ?: return@launch
            val updated = existing.copy(
                title = title.trim(),
                hour = hour,
                minute = minute,
                repeatMode = repeatMode,
                daysOfWeek = daysOfWeek,
                oneTimeDateMillis = oneTimeDateMillis,
            )
            repository.updateReminder(updated)
            if (updated.enabled) scheduler.schedule(updated) else scheduler.cancel(id)
        }
    }
}
