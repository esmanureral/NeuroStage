package com.esmanureral.neurostage.ui.patient.games.colormatch

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import com.esmanureral.neurostage.R
import com.esmanureral.neurostage.domain.patient.colormatch.ColorMatchColor

@Composable
@ReadOnlyComposable
fun ColorMatchColor.displayColor(): Color = when (this) {
    ColorMatchColor.RED -> colorResource(R.color.color_match_red)
    ColorMatchColor.YELLOW -> colorResource(R.color.color_match_yellow)
    ColorMatchColor.GREEN -> colorResource(R.color.color_match_green)
    ColorMatchColor.BLUE -> colorResource(R.color.color_match_blue)
}
