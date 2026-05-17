package com.esmanureral.neurostage.ui.patient.reminders

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.DatePicker
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.esmanureral.neurostage.R
import com.esmanureral.neurostage.ui.patient.games.GameScreenTopBar
import com.esmanureral.neurostage.ui.theme.PatientColors
import com.esmanureral.neurostage.data.patient.PatientReminder
import com.esmanureral.neurostage.data.patient.ReminderRepeatMode
import com.esmanureral.neurostage.data.patient.ReminderWeekdays
import com.esmanureral.neurostage.reminder.ReminderAlarms
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.concurrent.TimeUnit

@Composable
fun SamsungReminderListScreen(
    reminders: List<PatientReminder>,
    onBack: () -> Unit,
    onAdd: () -> Unit,
    onOpenReminder: (PatientReminder) -> Unit,
    onToggle: (PatientReminder, Boolean) -> Unit,
    onDelete: (PatientReminder) -> Unit,
    modifier: Modifier = Modifier,
) {
    val navy = colorResource(R.color.patient_reminder_navy)
    val muted = colorResource(R.color.patient_reminder_day_unselected)
    val nextTrigger = remember(reminders) { findNextEnabledTrigger(reminders) }

    Scaffold(
        modifier = modifier,
        containerColor = PatientColors.gameBackgroundCream,
        topBar = {
            GameScreenTopBar(
                title = stringResource(R.string.patient_reminder_screen_title),
                onBack = onBack,
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
            ) {
                IconButton(onClick = onAdd) {
                    Icon(
                        Icons.Filled.Add,
                        contentDescription = stringResource(R.string.patient_reminder_cd_add),
                        tint = navy,
                        modifier = Modifier.size(28.dp),
                    )
                }
            }
            if (nextTrigger != null) {
                val (hours, minutes, dateLabel) = formatNextAlarmParts(nextTrigger)
                Text(
                    text = stringResource(R.string.patient_reminder_next_in, hours, minutes),
                    color = navy,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.SemiBold,
                    lineHeight = 30.sp,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = stringResource(R.string.patient_reminder_next_at, dateLabel),
                    color = muted,
                    fontSize = 14.sp,
                )
            } else {
                Text(
                    text = stringResource(R.string.patient_reminder_no_upcoming),
                    color = muted,
                    fontSize = 15.sp,
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (reminders.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = stringResource(R.string.patient_reminder_empty),
                        color = muted,
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center,
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 24.dp),
                ) {
                    items(reminders, key = { it.id }) { reminder ->
                        SamsungReminderListCard(
                            reminder = reminder,
                            onClick = { onOpenReminder(reminder) },
                            onToggle = { onToggle(reminder, it) },
                            onDelete = { onDelete(reminder) },
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun SamsungReminderListCard(
    reminder: PatientReminder,
    onClick: () -> Unit,
    onToggle: (Boolean) -> Unit,
    onDelete: () -> Unit,
) {
    val context = LocalContext.current
    val navy = colorResource(R.color.patient_reminder_navy)
    val muted = colorResource(R.color.patient_reminder_day_unselected)
    val timeLabel = formatTimeLabel(reminder.hour, reminder.minute)
    val alphaTitle = if (reminder.enabled) 1f else 0.45f

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = onClick,
                onLongClick = onDelete,
            ),
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
        shadowElevation = 2.dp,
    ) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 18.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = timeLabel,
            fontSize = 38.sp,
            fontWeight = FontWeight.Light,
            color = navy.copy(alpha = alphaTitle),
            modifier = Modifier.padding(end = 16.dp),
        )
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center,
        ) {
            if (reminder.repeatMode == ReminderRepeatMode.DAILY) {
                Text(
                    text = stringResource(R.string.patient_reminder_every_day_label),
                    color = navy.copy(alpha = alphaTitle * 0.85f),
                    fontSize = 15.sp,
                )
            } else {
                SamsungInlineDayLetters(
                    reminder = reminder,
                    enabled = reminder.enabled,
                )
            }
            if (reminder.title.isNotBlank() &&
                reminder.title != context.getString(R.string.patient_reminder_default_title)
            ) {
                Text(
                    text = reminder.title,
                    color = muted.copy(alpha = alphaTitle),
                    fontSize = 13.sp,
                    modifier = Modifier.padding(top = 4.dp),
                )
            }
        }
        Switch(
            checked = reminder.enabled,
            onCheckedChange = onToggle,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = navy,
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = muted.copy(alpha = 0.35f),
                uncheckedBorderColor = Color.Transparent,
            ),
        )
    }
    }
}

@Composable
private fun SamsungInlineDayLetters(
    reminder: PatientReminder,
    enabled: Boolean,
) {
    val letters = stringArrayResource(R.array.patient_reminder_weekday_letter)
    val navy = colorResource(R.color.patient_reminder_navy)
    val inactive = colorResource(R.color.patient_reminder_day_unselected)
    val saturdayColor = colorResource(R.color.patient_reminder_card_coral)
    val alpha = if (enabled) 1f else 0.45f
    val activeDays = when (reminder.repeatMode) {
        ReminderRepeatMode.DAILY -> ReminderWeekdays.toSet()
        ReminderRepeatMode.WEEKLY -> reminder.daysOfWeek
        ReminderRepeatMode.ONCE -> {
            val dow = reminder.oneTimeDateMillis?.let { millis ->
                Calendar.getInstance().apply { timeInMillis = millis }.get(Calendar.DAY_OF_WEEK)
            }
            if (dow != null) setOf(dow) else emptySet()
        }
    }

    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        ReminderWeekdays.forEachIndexed { index, day ->
            val selected = day in activeDays
            val isSaturday = day == Calendar.SATURDAY
            val color = when {
                selected -> navy
                isSaturday -> saturdayColor
                else -> inactive
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                if (selected) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(navy.copy(alpha = alpha)),
                    )
                } else {
                    Spacer(modifier = Modifier.height(6.dp))
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = letters.getOrElse(index) { "?" },
                    fontSize = 14.sp,
                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                    color = color.copy(alpha = alpha),
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SamsungReminderEditorScreen(
    title: String,
    onTitleChange: (String) -> Unit,
    timeHour: Int,
    timeMinute: Int,
    onTimeChange: (Int, Int) -> Unit,
    selectedDays: Set<Int>,
    onSelectedDaysChange: (Set<Int>) -> Unit,
    repeatMode: ReminderRepeatMode,
    onRepeatModeChange: (ReminderRepeatMode) -> Unit,
    selectedDateMillis: Long,
    onSelectedDateChange: (Long) -> Unit,
    isEditing: Boolean,
    onSave: () -> Unit,
    onCancel: () -> Unit,
    onDelete: (() -> Unit)?,
) {
    val context = LocalContext.current
    val navy = colorResource(R.color.patient_reminder_navy)
    val muted = colorResource(R.color.patient_reminder_day_unselected)
    var showDatePicker by remember { mutableStateOf(false) }

    ReminderDatePickerDialog(
        visible = showDatePicker,
        initialDateMillis = selectedDateMillis,
        onDismiss = { showDatePicker = false },
        onConfirm = { millis ->
            onSelectedDateChange(millis)
            onRepeatModeChange(ReminderRepeatMode.ONCE)
            val dow = Calendar.getInstance().apply { timeInMillis = millis }.get(Calendar.DAY_OF_WEEK)
            onSelectedDaysChange(setOf(dow))
            showDatePicker = false
        },
    )

    Scaffold(
        containerColor = PatientColors.gameBackgroundCream,
        topBar = {
            GameScreenTopBar(
                title = stringResource(
                    if (isEditing) R.string.patient_reminder_edit_title
                    else R.string.patient_reminder_form_title,
                ),
                onBack = onCancel,
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            ReminderEditableTimeField(
                hour = timeHour,
                minute = timeMinute,
                onTimeChange = onTimeChange,
                primaryColor = navy,
                mutedColor = muted,
                surfaceColor = Color.White,
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(modifier = Modifier.height(16.dp))

            Surface(
                modifier = Modifier
                    .widthIn(max = 400.dp)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                color = Color.White,
                shadowElevation = 2.dp,
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                    ) {
                        Text(
                            text = formatDaySummaryHeader(context, repeatMode, selectedDays, selectedDateMillis),
                            color = navy,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Center,
                        )
                        IconButton(onClick = { showDatePicker = true }) {
                            Icon(
                                Icons.Filled.CalendarMonth,
                                contentDescription = stringResource(R.string.patient_reminder_cd_pick_date),
                                tint = navy,
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    SamsungEditorDayRow(
                        selectedDays = selectedDays,
                        repeatMode = repeatMode,
                        onToggleDay = { day ->
                            if (repeatMode == ReminderRepeatMode.ONCE) {
                                onRepeatModeChange(ReminderRepeatMode.WEEKLY)
                            }
                            val next = if (day in selectedDays) selectedDays - day else selectedDays + day
                            val allDays = ReminderAlarms.defaultWeekdaysSelection()
                            when {
                                next.isEmpty() -> onSelectedDaysChange(setOf(day))
                                next.size == allDays.size -> {
                                    onRepeatModeChange(ReminderRepeatMode.DAILY)
                                    onSelectedDaysChange(allDays)
                                }
                                else -> {
                                    onRepeatModeChange(ReminderRepeatMode.WEEKLY)
                                    onSelectedDaysChange(next)
                                }
                            }
                        },
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    OutlinedTextField(
                        value = title,
                        onValueChange = onTitleChange,
                        modifier = Modifier.fillMaxWidth(),
                        label = {
                            Text(
                                stringResource(R.string.patient_reminder_title_label),
                                textAlign = TextAlign.Center,
                            )
                        },
                        placeholder = { Text(stringResource(R.string.patient_reminder_title_placeholder)) },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = navy,
                            focusedLabelColor = navy,
                            cursorColor = navy,
                        ),
                    )

                    if (isEditing && onDelete != null) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = stringResource(R.string.patient_reminder_delete_confirm),
                            color = colorResource(R.color.patient_reminder_card_coral),
                            fontSize = 15.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .clickable(onClick = onDelete)
                                .padding(vertical = 8.dp),
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                TextButton(onClick = onCancel) {
                    Text(
                        text = stringResource(R.string.patient_reminder_cancel_short),
                        color = navy,
                        fontSize = 16.sp,
                    )
                }
                TextButton(
                    onClick = {
                        onSave()
                        Toast.makeText(
                            context,
                            context.getString(R.string.patient_reminder_toast_saved),
                            Toast.LENGTH_SHORT,
                        ).show()
                    },
                ) {
                    Text(
                        text = stringResource(R.string.patient_reminder_save_short),
                        color = navy,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }
    }
}

@Composable
private fun SamsungEditorDayRow(
    selectedDays: Set<Int>,
    repeatMode: ReminderRepeatMode,
    onToggleDay: (Int) -> Unit,
) {
    val letters = stringArrayResource(R.array.patient_reminder_weekday_letter)
    val navy = colorResource(R.color.patient_reminder_navy)
    val inactive = colorResource(R.color.patient_reminder_day_unselected)
    val saturdayColor = colorResource(R.color.patient_reminder_card_coral)
    val activeDays = when (repeatMode) {
        ReminderRepeatMode.DAILY -> ReminderWeekdays.toSet()
        else -> selectedDays
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {
        ReminderWeekdays.forEachIndexed { index, day ->
            val selected = day in activeDays
            val isSaturday = day == Calendar.SATURDAY
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(if (selected) navy else Color.Transparent)
                    .clickable { onToggleDay(day) },
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = letters.getOrElse(index) { "?" },
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = when {
                        selected -> Color.White
                        isSaturday -> saturdayColor
                        else -> inactive
                    },
                )
            }
        }
    }
}

private fun formatDaySummaryHeader(
    context: android.content.Context,
    repeatMode: ReminderRepeatMode,
    selectedDays: Set<Int>,
    selectedDateMillis: Long,
): String {
    val weekdayNames = context.resources.getStringArray(R.array.patient_reminder_weekday_names)
    return when (repeatMode) {
        ReminderRepeatMode.DAILY -> context.getString(R.string.patient_reminder_every_day_label)
        ReminderRepeatMode.ONCE -> {
            val cal = Calendar.getInstance().apply { timeInMillis = selectedDateMillis }
            val dow = cal.get(Calendar.DAY_OF_WEEK)
            val idx = ReminderWeekdays.indexOf(dow).coerceAtLeast(0)
            context.getString(R.string.patient_reminder_days_summary, weekdayNames.getOrElse(idx) { "" })
        }
        ReminderRepeatMode.WEEKLY -> {
            if (selectedDays.size == 1) {
                val dow = selectedDays.first()
                val idx = ReminderWeekdays.indexOf(dow).coerceAtLeast(0)
                context.getString(R.string.patient_reminder_days_summary, weekdayNames.getOrElse(idx) { "" })
            } else {
                context.getString(R.string.patient_reminder_repeat_weekly)
            }
        }
    }
}

fun findNextEnabledTrigger(reminders: List<PatientReminder>): Long? =
    reminders
        .filter { it.enabled }
        .mapNotNull { r ->
            val t = ReminderAlarms.nextTriggerMillis(r)
            if (t == Long.MAX_VALUE) null else t
        }
        .minOrNull()

fun formatNextAlarmParts(triggerMillis: Long): Triple<Int, Int, String> {
    val diffMs = (triggerMillis - System.currentTimeMillis()).coerceAtLeast(0L)
    val hours = TimeUnit.MILLISECONDS.toHours(diffMs).toInt()
    val minutes = ((diffMs % TimeUnit.HOURS.toMillis(1)) / TimeUnit.MINUTES.toMillis(1)).toInt()
    val locale = Locale.forLanguageTag("tr")
    val label = SimpleDateFormat("d MMM EEE HH:mm", locale).format(triggerMillis)
    return Triple(hours, minutes, label)
}
