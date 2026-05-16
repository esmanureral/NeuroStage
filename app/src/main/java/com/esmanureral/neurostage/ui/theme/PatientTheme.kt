package com.esmanureral.neurostage.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.res.colorResource
import com.esmanureral.neurostage.R

@Composable
@ReadOnlyComposable
private fun patientLightColorScheme() = lightColorScheme(
    primary = colorResource(R.color.patient_primary),
    onPrimary = colorResource(R.color.patient_on_surface),
    primaryContainer = colorResource(R.color.patient_primary_light),
    onPrimaryContainer = colorResource(R.color.patient_text_primary),
    background = colorResource(R.color.patient_background),
    onBackground = colorResource(R.color.patient_text_primary),
    surface = colorResource(R.color.patient_surface),
    onSurface = colorResource(R.color.patient_text_primary),
    onSurfaceVariant = colorResource(R.color.patient_text_secondary),
    outline = colorResource(R.color.patient_divider),
    error = colorResource(R.color.patient_moderate_notice_title_color),
    onError = colorResource(R.color.patient_on_surface),
)

@Composable
fun NeuroStagePatientTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = patientLightColorScheme(),
        typography = Typography,
        content = content,
    )
}