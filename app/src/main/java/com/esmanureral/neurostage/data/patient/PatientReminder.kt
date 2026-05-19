package com.esmanureral.neurostage.data.patient

import java.util.Calendar

enum class ReminderRepeatMode {
    DAILY,
    WEEKLY,
    ONCE,
}

data class PatientReminder(
    val id: Long,
    val title: String,
    val hour: Int,
    val minute: Int,
    val enabled: Boolean = true,
    val repeatMode: ReminderRepeatMode = ReminderRepeatMode.DAILY,
    val daysOfWeek: Set<Int> = emptySet(),
    val oneTimeDateMillis: Long? = null,
)

val ReminderWeekdays: List<Int> = listOf(
    Calendar.MONDAY,
    Calendar.TUESDAY,
    Calendar.WEDNESDAY,
    Calendar.THURSDAY,
    Calendar.FRIDAY,
    Calendar.SATURDAY,
    Calendar.SUNDAY,
)
