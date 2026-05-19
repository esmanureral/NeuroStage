package com.esmanureral.neurostage.ui.patient.reminders

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.esmanureral.neurostage.R

@Composable
fun ReminderEditableTimeField(
    hour: Int,
    minute: Int,
    onTimeChange: (Int, Int) -> Unit,
    primaryColor: Color,
    mutedColor: Color,
    surfaceColor: Color,
    modifier: Modifier = Modifier,
) {
    val minuteFocus = remember { FocusRequester() }
    var hourFocused by remember { mutableStateOf(false) }
    var minuteFocused by remember { mutableStateOf(false) }

    var hourField by remember {
        mutableStateOf(TextFieldValue(hour.toString(), TextRange(hour.toString().length)))
    }
    var minuteField by remember {
        mutableStateOf(TextFieldValue(minute.toString(), TextRange(minute.toString().length)))
    }

    LaunchedEffect(hour, minute) {
        if (!hourFocused) {
            val h = hour.toString()
            if (hourField.text != h) {
                hourField = TextFieldValue(h, TextRange(h.length))
            }
        }
        if (!minuteFocused) {
            val m = minute.toString()
            if (minuteField.text != m) {
                minuteField = TextFieldValue(m, TextRange(m.length))
            }
        }
    }

    fun restoreHourFromParent() {
        val h = hour.toString()
        hourField = TextFieldValue(h, TextRange(h.length))
    }

    fun restoreMinuteFromParent() {
        val m = minute.toString()
        minuteField = TextFieldValue(m, TextRange(m.length))
    }

    fun commitHourOnBlur() {
        val digits = hourField.text.filter(Char::isDigit)
        if (digits.isEmpty()) {
            restoreHourFromParent()
            return
        }
        val h = digits.toIntOrNull()?.coerceIn(0, 23) ?: run {
            restoreHourFromParent()
            return
        }
        onTimeChange(h, minute)
        hourField = TextFieldValue(h.toString(), TextRange(h.toString().length))
    }

    fun commitMinuteOnBlur() {
        val digits = minuteField.text.filter(Char::isDigit)
        if (digits.isEmpty()) {
            restoreMinuteFromParent()
            return
        }
        val m = digits.toIntOrNull()?.coerceIn(0, 59) ?: run {
            restoreMinuteFromParent()
            return
        }
        onTimeChange(hour, m)
        minuteField = TextFieldValue(m.toString(), TextRange(m.toString().length))
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(vertical = 12.dp),
        ) {
            ReminderTimePartField(
                value = hourField,
                onValueChange = { updated ->
                    val digits = updated.text.filter(Char::isDigit).take(2)
                    hourField = updated.copy(text = digits, selection = TextRange(digits.length))
                },
                onFocusChanged = { focused ->
                    hourFocused = focused
                    if (!focused) commitHourOnBlur()
                },
                primaryColor = primaryColor,
                surfaceColor = surfaceColor,
                focusRequester = null,
                imeAction = ImeAction.Next,
                onImeNext = { minuteFocus.requestFocus() },
            )
            Text(
                text = ":",
                fontSize = 44.sp,
                fontWeight = FontWeight.Light,
                color = primaryColor.copy(alpha = 0.45f),
                modifier = Modifier.padding(horizontal = 10.dp),
            )
            ReminderTimePartField(
                value = minuteField,
                onValueChange = { updated ->
                    val digits = updated.text.filter(Char::isDigit).take(2)
                    minuteField = updated.copy(text = digits, selection = TextRange(digits.length))
                },
                onFocusChanged = { focused ->
                    minuteFocused = focused
                    if (!focused) commitMinuteOnBlur()
                },
                primaryColor = primaryColor,
                surfaceColor = surfaceColor,
                focusRequester = minuteFocus,
                imeAction = ImeAction.Done,
                onImeNext = { commitMinuteOnBlur() },
            )
        }
        Text(
            text = stringResource(R.string.patient_reminder_time_edit_hint),
            color = mutedColor,
            fontSize = 13.sp,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun ReminderTimePartField(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    onFocusChanged: (Boolean) -> Unit,
    primaryColor: Color,
    surfaceColor: Color,
    focusRequester: FocusRequester?,
    imeAction: ImeAction,
    onImeNext: () -> Unit,
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = surfaceColor,
        modifier = Modifier.width(88.dp),
    ) {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .then(if (focusRequester != null) Modifier.focusRequester(focusRequester) else Modifier)
                .onFocusChanged { onFocusChanged(it.isFocused) }
                .padding(vertical = 14.dp, horizontal = 8.dp),
            textStyle = TextStyle(
                color = primaryColor,
                fontSize = 44.sp,
                fontWeight = FontWeight.Light,
                textAlign = TextAlign.Center,
            ),
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.NumberPassword,
                imeAction = imeAction,
            ),
            keyboardActions = KeyboardActions(
                onNext = { onImeNext() },
                onDone = { onImeNext() },
            ),
            cursorBrush = SolidColor(primaryColor),
        )
    }
}
