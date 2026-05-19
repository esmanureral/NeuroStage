package com.esmanureral.neurostage.ui.patient.games.colormatch

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.esmanureral.neurostage.R
import com.esmanureral.neurostage.ui.theme.PatientColors

@Composable
fun ColorMatchLevelCompleteDialog(
    visible: Boolean,
    onContinue: () -> Unit,
    onReplay: () -> Unit,
    onDismiss: () -> Unit,
) {
    if (!visible) return
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = stringResource(R.string.color_match_dialog_title),
                fontWeight = FontWeight.Bold,
            )
        },
        text = { Text(stringResource(R.string.color_match_dialog_body)) },
        confirmButton = {
            TextButton(onClick = onContinue) {
                Text(
                    text = stringResource(R.string.color_match_dialog_continue),
                    fontWeight = FontWeight.Bold,
                    color = PatientColors.gameSuccess,
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onReplay) {
                Text(stringResource(R.string.color_match_dialog_replay))
            }
        },
    )
}
