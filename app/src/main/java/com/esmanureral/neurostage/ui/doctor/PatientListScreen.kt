package com.esmanureral.neurostage.ui.doctor

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.geometry.Size as GeometrySize
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.esmanureral.neurostage.R
import com.esmanureral.neurostage.ui.theme.NeurostageBrandBlue
import com.esmanureral.neurostage.ui.theme.NsChipGreenBg
import com.esmanureral.neurostage.ui.theme.NsChipGreenFg
import com.esmanureral.neurostage.ui.theme.NsChipIndigoBg
import com.esmanureral.neurostage.ui.theme.NsChipIndigoFg
import com.esmanureral.neurostage.ui.theme.NsDoctorAccentBlue
import com.esmanureral.neurostage.ui.theme.NsDoctorAvatarSoftBg
import com.esmanureral.neurostage.ui.theme.NsDoctorScaffoldBg
import com.esmanureral.neurostage.ui.theme.NsGray300
import com.esmanureral.neurostage.ui.theme.NsGray400
import com.esmanureral.neurostage.ui.theme.NsOrangeHot
import com.esmanureral.neurostage.ui.theme.NsPurpleAvatar
import com.esmanureral.neurostage.ui.theme.NsRedAlertStrong
import com.esmanureral.neurostage.ui.theme.NsStatusSuccess
import com.esmanureral.neurostage.ui.theme.NsWhite
import com.esmanureral.neurostage.util.Constants.MriStageLabel

private val waveBottomShape: Shape = object : Shape {
    override fun createOutline(
        size: GeometrySize,
        layoutDirection: LayoutDirection,
        density: Density,
    ): Outline {
        val waveDepth = with(density) { 20.dp.toPx() }
        val path = Path().apply {
            moveTo(0f, 0f)
            lineTo(size.width, 0f)
            lineTo(size.width, size.height - waveDepth)
            quadraticTo(size.width / 2f, size.height + waveDepth, 0f, size.height - waveDepth)
            close()
        }
        return Outline.Generic(path)
    }
}

private val patientAvatarPalette = listOf(
    NsDoctorAccentBlue,
    NsStatusSuccess,
    NsPurpleAvatar,
    NsOrangeHot,
    NsRedAlertStrong,
)

private fun scanLabelAccent(label: String): Color = when (label) {
    MriStageLabel.HEALTHY, "Sağlıklı", "Demanssız" -> NsStatusSuccess
    MriStageLabel.MILD, MriStageLabel.VERY_MILD,
    "Hafif evre", "Hafif Alzheimer",
    "Çok hafif evre", "Çok Hafif Alzheimer" -> NsOrangeHot
    MriStageLabel.MODERATE, "Orta evre", "Orta Evre Alzheimer" -> NsRedAlertStrong
    else -> NsGray400
}

@Composable
fun PatientListScreen(
    onBack: () -> Unit,
    onPickPatient: (patientId: String) -> Unit,
    viewModel: PatientListViewModel = hiltViewModel(),
) {
    val ui by viewModel.ui.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.refresh()
    }

    Surface(modifier = Modifier.fillMaxSize(), color = NsDoctorScaffoldBg) {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(waveBottomShape)
                    .background(NeurostageBrandBlue)
                    .statusBarsPadding()
                    .padding(horizontal = 8.dp, vertical = 20.dp),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Outlined.ArrowBack,
                            contentDescription = stringResource(R.string.doctor_history_cd_back),
                            tint = NsWhite,
                        )
                    }
                    Column(modifier = Modifier.padding(start = 4.dp)) {
                        Text(
                            text = stringResource(R.string.patient_list_title),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = NsWhite,
                        )
                        Text(
                            text = stringResource(R.string.patient_list_registered_count, ui.patients.size),
                            style = MaterialTheme.typography.bodySmall,
                            color = NsWhite.copy(alpha = 0.75f),
                        )
                    }
                }
            }

            ui.error?.let {
                Text(
                    it,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodySmall,
                )
            }

            if (ui.patients.isEmpty() && !ui.isLoading) {
                Spacer(Modifier.weight(1f))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(NsDoctorAvatarSoftBg),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            Icons.Outlined.PersonOutline,
                            contentDescription = null,
                            tint = NsDoctorAccentBlue,
                            modifier = Modifier.size(36.dp),
                        )
                    }
                    Text(
                        stringResource(R.string.patient_list_empty_title),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Text(
                        stringResource(R.string.patient_list_empty_hint),
                        style = MaterialTheme.typography.bodySmall,
                        color = NsGray400,
                    )
                }
                Spacer(Modifier.weight(1f))
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    items(ui.patients, key = { it.patient.id }) { summary ->
                        PatientCard(summary) { onPickPatient(summary.patient.id) }
                    }
                }
            }
        }
    }
}

@Composable
private fun PatientCard(summary: PatientSummary, onClick: () -> Unit) {
    val patient = summary.patient
    val unknownInitial = stringResource(R.string.doctor_ui_initials_unknown)
    val initials = patient.fullName
        .split(" ")
        .mapNotNull { it.firstOrNull()?.uppercaseChar() }
        .take(2)
        .joinToString("")
        .ifBlank { unknownInitial }

    val avatarColor = patientAvatarPalette[patient.id.hashCode().and(0x7FFFFFFF) % patientAvatarPalette.size]

    val statusColor = summary.lastScanLabel?.let { scanLabelAccent(it) } ?: NsGray400

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = NsWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(avatarColor),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = initials,
                    color = NsWhite,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                )
            }

            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Text(
                    text = patient.fullName,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                )
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    patient.age?.let { age ->
                        Chip(
                            text = stringResource(R.string.patient_age_years_format, age),
                            bgColor = NsChipIndigoBg,
                            textColor = NsChipIndigoFg,
                        )
                    }
                    patient.gender?.let { g ->
                        Chip(text = g, bgColor = NsChipGreenBg, textColor = NsChipGreenFg)
                    }
                }
                summary.lastScanLabel?.let { label ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        Box(
                            modifier = Modifier
                                .size(7.dp)
                                .clip(CircleShape)
                                .background(statusColor),
                        )
                        Text(
                            text = stringResource(R.string.patient_list_last_scan_format, label),
                            style = MaterialTheme.typography.bodySmall,
                            color = statusColor,
                            fontWeight = FontWeight.Medium,
                        )
                    }
                }
            }

            Icon(
                Icons.Outlined.ChevronRight,
                contentDescription = stringResource(R.string.patient_list_cd_open_patient),
                tint = NsGray300,
            )
        }
    }
}

@Composable
private fun Chip(text: String, bgColor: Color, textColor: Color) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(bgColor)
            .padding(horizontal = 6.dp, vertical = 2.dp),
    ) {
        Text(
            text,
            style = MaterialTheme.typography.labelSmall,
            color = textColor,
            fontWeight = FontWeight.Medium,
        )
    }
}
