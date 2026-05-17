package com.esmanureral.neurostage.ui.patient.reminders

import android.Manifest
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.esmanureral.neurostage.R
import com.esmanureral.neurostage.data.patient.PatientReminder
import com.esmanureral.neurostage.data.patient.ReminderRepeatMode
import com.esmanureral.neurostage.reminder.ReminderAlarms
import java.util.Calendar

private enum class ReminderUiMode { LIST, EDITOR }

private sealed interface ReminderDeleteRequest {
    data class FromList(val reminder: PatientReminder) : ReminderDeleteRequest
    data class FromEditor(val reminderId: Long) : ReminderDeleteRequest
}

@Composable
fun PatientRemindersScreen(
    onBack: () -> Unit,
    viewModel: PatientRemindersViewModel = hiltViewModel(),
) {
    val reminders by viewModel.reminders.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val defaultReminderTitle = stringResource(R.string.patient_reminder_default_title)
    val deletedToastMessage = stringResource(R.string.patient_reminder_long_press_deleted)

    var uiMode by remember { mutableStateOf(ReminderUiMode.LIST) }
    var editingId by remember { mutableStateOf<Long?>(null) }
    var deleteRequest by remember { mutableStateOf<ReminderDeleteRequest?>(null) }

    var title by remember { mutableStateOf("") }
    var repeatMode by remember { mutableStateOf(ReminderRepeatMode.DAILY) }
    var selectedDays by remember { mutableStateOf(ReminderAlarms.defaultWeekdaysSelection()) }
    var timeHour by remember { mutableIntStateOf(9) }
    var timeMinute by remember { mutableIntStateOf(0) }
    var selectedDateMillis by remember {
        mutableLongStateOf(startOfDayMillis(Calendar.getInstance()))
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { }

    fun ensureNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    fun resetForm() {
        title = ""
        repeatMode = ReminderRepeatMode.DAILY
        selectedDays = ReminderAlarms.defaultWeekdaysSelection()
        val now = Calendar.getInstance()
        timeHour = now.get(Calendar.HOUR_OF_DAY)
        timeMinute = now.get(Calendar.MINUTE)
        selectedDateMillis = startOfDayMillis(now)
        editingId = null
    }

    fun loadReminder(reminder: PatientReminder) {
        editingId = reminder.id
        title = reminder.title
        repeatMode = reminder.repeatMode
        selectedDays = when (reminder.repeatMode) {
            ReminderRepeatMode.DAILY -> ReminderAlarms.defaultWeekdaysSelection()
            ReminderRepeatMode.WEEKLY -> reminder.daysOfWeek.ifEmpty {
                ReminderAlarms.defaultWeekdaysSelection()
            }
            ReminderRepeatMode.ONCE -> {
                reminder.oneTimeDateMillis?.let { millis ->
                    val dow = Calendar.getInstance().apply { timeInMillis = millis }
                        .get(Calendar.DAY_OF_WEEK)
                    setOf(dow)
                } ?: ReminderAlarms.defaultWeekdaysSelection()
            }
        }
        timeHour = reminder.hour
        timeMinute = reminder.minute
        selectedDateMillis = reminder.oneTimeDateMillis ?: startOfDayMillis(Calendar.getInstance())
    }

    fun openCreate() {
        resetForm()
        uiMode = ReminderUiMode.EDITOR
    }

    fun openEdit(reminder: PatientReminder) {
        loadReminder(reminder)
        uiMode = ReminderUiMode.EDITOR
    }

    fun closeEditor() {
        uiMode = ReminderUiMode.LIST
        resetForm()
    }

    fun performDelete(reminderId: Long) {
        viewModel.removeReminder(reminderId)
        Toast.makeText(context.applicationContext, deletedToastMessage, Toast.LENGTH_SHORT).show()
    }

    fun openDeleteDialog(request: ReminderDeleteRequest) {
        deleteRequest = request
    }

    fun dismissDeleteDialog() {
        deleteRequest = null
    }

    fun saveReminder() {
        ensureNotificationPermission()
        val label = title.ifBlank { defaultReminderTitle }
        val days = when (repeatMode) {
            ReminderRepeatMode.WEEKLY -> selectedDays.ifEmpty {
                setOf(Calendar.getInstance().get(Calendar.DAY_OF_WEEK))
            }
            ReminderRepeatMode.DAILY -> emptySet()
            ReminderRepeatMode.ONCE -> emptySet()
        }
        val oneTime = when (repeatMode) {
            ReminderRepeatMode.ONCE -> selectedDateMillis
            else -> null
        }
        val existingId = editingId
        if (existingId != null) {
            viewModel.updateReminder(
                id = existingId,
                title = label,
                hour = timeHour,
                minute = timeMinute,
                repeatMode = repeatMode,
                daysOfWeek = days,
                oneTimeDateMillis = oneTime,
            )
        } else {
            viewModel.addReminder(
                title = label,
                hour = timeHour,
                minute = timeMinute,
                repeatMode = repeatMode,
                daysOfWeek = days,
                oneTimeDateMillis = oneTime,
            )
        }
        closeEditor()
    }

    BackHandler(enabled = uiMode == ReminderUiMode.EDITOR) {
        closeEditor()
    }

    LaunchedEffect(Unit) {
        ensureNotificationPermission()
    }

    ReminderDeleteDialogHost(
        request = deleteRequest,
        onDismiss = ::dismissDeleteDialog,
        onConfirmList = { reminderId ->
            performDelete(reminderId)
            dismissDeleteDialog()
        },
        onConfirmEditor = { reminderId ->
            performDelete(reminderId)
            dismissDeleteDialog()
            closeEditor()
        },
    )

    when (uiMode) {
        ReminderUiMode.LIST -> {
            SamsungReminderListScreen(
                reminders = reminders,
                onBack = onBack,
                onAdd = { openCreate() },
                onOpenReminder = { openEdit(it) },
                onToggle = { reminder, enabled ->
                    viewModel.toggleReminder(reminder.id, enabled)
                },
                onDelete = { reminder ->
                    openDeleteDialog(ReminderDeleteRequest.FromList(reminder))
                },
                modifier = Modifier.fillMaxSize(),
            )
        }
        ReminderUiMode.EDITOR -> {
            val editorReminderId = editingId
            SamsungReminderEditorScreen(
                title = title,
                onTitleChange = { title = it },
                timeHour = timeHour,
                timeMinute = timeMinute,
                onTimeChange = { h, m ->
                    timeHour = h
                    timeMinute = m
                },
                selectedDays = selectedDays,
                onSelectedDaysChange = { selectedDays = it },
                repeatMode = repeatMode,
                onRepeatModeChange = { repeatMode = it },
                selectedDateMillis = selectedDateMillis,
                onSelectedDateChange = { selectedDateMillis = it },
                isEditing = editorReminderId != null,
                onSave = { saveReminder() },
                onCancel = { closeEditor() },
                onDelete = if (editorReminderId != null) {
                    {
                        openDeleteDialog(ReminderDeleteRequest.FromEditor(editorReminderId))
                    }
                } else {
                    null
                },
            )
        }
    }
}

@Composable
private fun ReminderDeleteDialogHost(
    request: ReminderDeleteRequest?,
    onDismiss: () -> Unit,
    onConfirmList: (Long) -> Unit,
    onConfirmEditor: (Long) -> Unit,
) {
    when (request) {
        is ReminderDeleteRequest.FromList -> {
            ReminderDeleteConfirmDialog(
                onDismiss = onDismiss,
                onConfirm = { onConfirmList(request.reminder.id) },
            )
        }
        is ReminderDeleteRequest.FromEditor -> {
            ReminderDeleteConfirmDialog(
                onDismiss = onDismiss,
                onConfirm = { onConfirmEditor(request.reminderId) },
            )
        }
        null -> Unit
    }
}

@Composable
private fun ReminderDeleteConfirmDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.patient_reminder_delete_dialog_title)) },
        text = { Text(stringResource(R.string.patient_reminder_delete_dialog_message)) },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(stringResource(R.string.patient_reminder_delete_dialog_confirm))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.patient_reminder_cancel))
            }
        },
    )
}
