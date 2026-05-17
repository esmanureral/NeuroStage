package com.esmanureral.neurostage.ui.theme

import androidx.compose.ui.graphics.Color

val NsBgTop = Color(0xFF18141F)
val NsBgBottom = Color(0xFF2A2035)

val NsCard = Color(0xFF2A2035)
val NsPanel = Color(0xFF1E1828)

val NsRose = Color(0xFFE87DA8)
val NsLavender = Color(0xFFC8A8E9)

val NsTextDark = Color(0xFF1A1A2E)
val NsTextMid = Color(0xFF8B8BA7)
val NsWhite = Color(0xFFFFFFFF)
val NsNavy = NsTextDark
val NsSlate = NsTextMid

val NsDivider = Color(0xFF2E2840)
val NsCoral = Color(0xFFF5A462)
val NsRoseLight = Color(0xFFFDE8F2)
val NsGreenLight = NsRoseLight
val NeurostageBrandBlue = Color(0xFF1A3E8F)

val NsDoctorScaffoldBg = Color(0xFFF5F6FA)
val NsDoctorAccentBlue = Color(0xFF2D6CDF)
val NsDoctorAvatarSoftBg = Color(0xFFE8EEF9)

val NsOrangeHot = Color(0xFFE37400)
val NsPurpleAvatar = Color(0xFF7A5CFF)
val NsRedAlertStrong = Color(0xFFDB4437)

val NsGraySlateBar = Color(0xFFCBD5E1)
val NsGray300 = Color(0xFFD1D5DB)
val NsGray400 = Color(0xFF9CA3AF)
val NsGray450 = Color(0xFF94A3B8)
val NsGray500 = Color(0xFF64748F)
val NsGray600 = Color(0xFF6B7280)
val NsGray700 = Color(0xFF475569)
val NsGray800 = Color(0xFF334155)
val NsGray900 = Color(0xFF111827)

val NsBlue50 = Color(0xFFEFF6FF)
val NsBlue100 = Color(0xFFBFDBFE)
val NsBlue700 = Color(0xFF1D4ED8)
val NsBlue800 = Color(0xFF1E40AF)

val NsAmber900 = Color(0xFF78350F)
val NsAmber800 = Color(0xFF92400E)
val NsAmber50 = Color(0xFFFFFBEB)

val NsRose50 = Color(0xFFFEF2F2)
val NsSlate50 = Color(0xFFF8FAFC)
val NsSlate100 = Color(0xFFF1F5F9)

val NsIndigo500 = Color(0xFF6366F1)
val NsViolet500 = Color(0xFF7C3AED)

val NsCompareGold = Color(0xFFFFD700)

object NsPatientStageBadge {
    val healthy = Pair(Color(0xFFDCFCE7), Color(0xFF16A34A))
    val veryMild = Pair(Color(0xFFFFFBEB), Color(0xFFD97706))
    val mild = Pair(Color(0xFFFFF7ED), Color(0xFFEA580C))
    val moderateOrUnknown = Pair(Color(0xFFFEF2F2), Color(0xFFDC2626))
}

val NsPatientScoreBarColors = listOf(
    Color(0xFFEA580C),
    Color(0xFFDC2626),
    Color(0xFF16A34A),
    Color(0xFFD97706),
)

val NsDoctorLoginTrackBg = Color(0xFFF3F4F6)
val NsDoctorLoginFieldBorderIdle = Color(0xFFE5E7EB)
val NsDoctorLoginFieldBgIdle = Color(0xFFF9FAFB)

val NsChipIndigoBg = Color(0xFFEEF2FF)
val NsChipIndigoFg = Color(0xFF4F46E5)
val NsChipGreenBg = Color(0xFFF0FDF4)
val NsChipGreenFg = Color(0xFF16A34A)
val NsStatusSuccess = Color(0xFF0F9D58)
val NsStatusError = Color(0xFFDC2626)

object StageColors {
    val Healthy = Color(0xFF7DC88A)
    val VeryMild = Color(0xFFF5C842)
    val Mild = Color(0xFFF5A462)
    val Moderate = Color(0xFFE87DA8)
    val Severe = Color(0xFFC84070)

    val HealthyBg = Color(0xFFEAF7EC)
    val VeryMildBg = Color(0xFFFFF8DC)
    val MildBg = Color(0xFFFFF0E5)
    val ModerateBg = Color(0xFFFDE8F2)
    val SevereBg = Color(0xFFFDE0E8)
}

/** Hasta MR sonuç ekranı — sakin, pastel tonlar (alarm kırmızısı yok). */
object PatientResultStageColors {
    val Healthy = Color(0xFF6BA88A)
    val VeryMild = Color(0xFF7A9CC9)
    val Mild = Color(0xFFB8956E)
    val Moderate = Color(0xFF8B9FD4)

    val HealthyBg = Color(0xFFEEF6F1)
    val VeryMildBg = Color(0xFFEEF4FA)
    val MildBg = Color(0xFFF7F2EB)
    val ModerateBg = Color(0xFFF0F2FA)

    fun accent(stageIndex: Int): Color = when (stageIndex) {
        0 -> Mild
        1 -> Moderate
        2 -> Healthy
        3 -> VeryMild
        else -> Moderate
    }

    fun background(stageIndex: Int): Color = when (stageIndex) {
        0 -> MildBg
        1 -> ModerateBg
        2 -> HealthyBg
        3 -> VeryMildBg
        else -> ModerateBg
    }
}