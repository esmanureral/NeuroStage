package com.esmanureral.neurostage.ui.patient

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.ui.unit.dp
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import com.esmanureral.neurostage.ui.theme.PatientColors

@Composable
fun PatientAlertDialog(
    onDismissRequest: () -> Unit,
    title: @Composable () -> Unit,
    text: @Composable () -> Unit,
    confirmButton: @Composable () -> Unit,
    dismissButton: @Composable () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        containerColor = PatientColors.surface,
        titleContentColor = PatientColors.textPrimary,
        textContentColor = PatientColors.textSecondary,
        title = title,
        text = text,
        confirmButton = confirmButton,
        dismissButton = dismissButton,
        tonalElevation = 2.dp,
    )
}

@Composable
fun PatientDialogTextButton(
    text: String,
    onClick: () -> Unit,
    emphasized: Boolean = false,
) {
    TextButton(onClick = onClick) {
        Text(
            text = text,
            color = PatientColors.primary,
            fontWeight = if (emphasized) FontWeight.SemiBold else FontWeight.Medium,
        )
    }
}
