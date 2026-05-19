package com.esmanureral.neurostage.ui.patient.reminders

import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.esmanureral.neurostage.R
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReminderDatePickerDialog(
    visible: Boolean,
    initialDateMillis: Long,
    onDismiss: () -> Unit,
    onConfirm: (Long) -> Unit,
) {
    if (!visible) return
    val state = rememberDatePickerState(initialSelectedDateMillis = initialDateMillis)
    val accent = colorResource(R.color.patient_reminder_navy)
    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    state.selectedDateMillis?.let { millis ->
                        onConfirm(
                            startOfDayMillis(
                                Calendar.getInstance().apply { timeInMillis = millis })
                        )
                    }
                },
            ) {
                Text(
                    text = stringResource(R.string.patient_reminder_ok),
                    color = accent,
                    fontWeight = FontWeight.Bold,
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.patient_reminder_cancel), color = accent)
            }
        },
    ) {
        DatePicker(state = state)
    }
}

fun startOfDayMillis(cal: Calendar): Long {
    val c = cal.clone() as Calendar
    c.set(Calendar.HOUR_OF_DAY, 0)
    c.set(Calendar.MINUTE, 0)
    c.set(Calendar.SECOND, 0)
    c.set(Calendar.MILLISECOND, 0)
    return c.timeInMillis
}

fun formatTimeLabel(hour: Int, minute: Int): String {
    val locale = Locale.forLanguageTag("tr")
    return String.format(locale, "%02d:%02d", hour, minute)
}
