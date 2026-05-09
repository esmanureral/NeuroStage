package com.esmanureral.neurostage.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val NeuroColorScheme = darkColorScheme(
    primary            = NsRose,
    onPrimary          = NsWhite,
    primaryContainer   = NsCard,
    onPrimaryContainer = NsWhite,
    secondary          = NsLavender,
    onSecondary        = NsWhite,
    background         = NsBgTop,
    onBackground       = NsWhite,
    surface            = NsCard,
    onSurface          = NsWhite,
    surfaceVariant     = NsPanel,
    onSurfaceVariant   = NsTextMid,
    outline            = NsDivider,
    error              = NsCoral,
    onError            = NsWhite,
)

@Composable
fun NeuroStageTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = NeuroColorScheme,
        typography  = Typography,
        content     = content,
    )
}
