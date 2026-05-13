package com.esmanureral.neurostage.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

private val NeuroText = FontFamily.SansSerif

val Typography = Typography(
    displayLarge = TextStyle(
        fontFamily = NeuroText,
        fontWeight = FontWeight.Bold,
        fontSize = 36.sp,
        lineHeight = 44.sp,
        letterSpacing = (-0.75).sp,
    ),
    displayMedium = TextStyle(
        fontFamily = NeuroText,
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp,
        lineHeight = 40.sp,
        letterSpacing = (-0.5).sp,
    ),
    displaySmall = TextStyle(
        fontFamily = NeuroText,
        fontWeight = FontWeight.Bold,
        fontSize = 30.sp,
        lineHeight = 38.sp,
        letterSpacing = (-0.35).sp,
    ),
    headlineLarge = TextStyle(
        fontFamily = NeuroText,
        fontWeight = FontWeight.SemiBold,
        fontSize = 28.sp,
        lineHeight = 36.sp,
        letterSpacing = (-0.35).sp,
    ),
    headlineMedium = TextStyle(
        fontFamily = NeuroText,
        fontWeight = FontWeight.SemiBold,
        fontSize = 24.sp,
        lineHeight = 32.sp,
        letterSpacing = (-0.2).sp,
    ),
    headlineSmall = TextStyle(
        fontFamily = NeuroText,
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp,
    ),
    titleLarge = TextStyle(
        fontFamily = NeuroText,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,
        lineHeight = 30.sp,
        letterSpacing = 0.sp,
    ),
    titleMedium = TextStyle(
        fontFamily = NeuroText,
        fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp,
        lineHeight = 26.sp,
        letterSpacing = (-0.1).sp,
    ),
    titleSmall = TextStyle(
        fontFamily = NeuroText,
        fontWeight = FontWeight.SemiBold,
        fontSize = 15.sp,
        lineHeight = 22.sp,
        letterSpacing = 0.sp,
    ),
    bodyLarge = TextStyle(
        fontFamily = NeuroText,
        fontWeight = FontWeight.Normal,
        fontSize = 17.sp,
        lineHeight = 26.sp,
        letterSpacing = 0.2.sp,
    ),
    bodyMedium = TextStyle(
        fontFamily = NeuroText,
        fontWeight = FontWeight.Normal,
        fontSize = 15.sp,
        lineHeight = 22.sp,
        letterSpacing = 0.25.sp,
    ),
    bodySmall = TextStyle(
        fontFamily = NeuroText,
        fontWeight = FontWeight.Medium,
        fontSize = 13.sp,
        lineHeight = 18.sp,
        letterSpacing = 0.35.sp,
    ),
    labelLarge = TextStyle(
        fontFamily = NeuroText,
        fontWeight = FontWeight.SemiBold,
        fontSize = 15.sp,
        lineHeight = 22.sp,
        letterSpacing = 0.05.sp,
    ),
    labelMedium = TextStyle(
        fontFamily = NeuroText,
        fontWeight = FontWeight.Medium,
        fontSize = 13.sp,
        lineHeight = 18.sp,
        letterSpacing = 0.3.sp,
    ),
    labelSmall = TextStyle(
        fontFamily = NeuroText,
        fontWeight = FontWeight.SemiBold,
        fontSize = 11.sp,
        lineHeight = 14.sp,
        letterSpacing = 0.6.sp,
    ),
)