package com.esmanureral.neurostage.ui.doctor

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.geometry.Size as GeometrySize
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.esmanureral.neurostage.R
import com.esmanureral.neurostage.ui.theme.NeurostageBrandBlue
import com.esmanureral.neurostage.ui.theme.NsBlue50
import com.esmanureral.neurostage.ui.theme.NsDoctorAccentBlue
import com.esmanureral.neurostage.ui.theme.NsDoctorAvatarSoftBg
import com.esmanureral.neurostage.ui.theme.NsDoctorLoginFieldBgIdle
import com.esmanureral.neurostage.ui.theme.NsDoctorLoginFieldBorderIdle
import com.esmanureral.neurostage.ui.theme.NsDoctorScaffoldBg
import com.esmanureral.neurostage.ui.theme.NsGray400
import com.esmanureral.neurostage.ui.theme.NsGray600
import com.esmanureral.neurostage.ui.theme.NsGray900
import com.esmanureral.neurostage.ui.theme.NsStatusError
import com.esmanureral.neurostage.ui.theme.NsWhite

private val waveBottomShape: Shape = object : Shape {
    override fun createOutline(
        size: GeometrySize,
        layoutDirection: LayoutDirection,
        density: Density,
    ): Outline {
        val waveDepth = with(density) { 24.dp.toPx() }
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

private val fieldShape = RoundedCornerShape(14.dp)
private val cardShape = RoundedCornerShape(20.dp)

private val intakeFieldColors
    @Composable get() = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = NsDoctorAccentBlue,
        unfocusedBorderColor = NsDoctorLoginFieldBorderIdle,
        focusedContainerColor = NsBlue50.copy(alpha = 0.55f),
        unfocusedContainerColor = NsDoctorLoginFieldBgIdle,
        cursorColor = NeurostageBrandBlue,
        focusedLabelColor = NeurostageBrandBlue,
        unfocusedLabelColor = NsGray600,
        focusedTextColor = NsGray900,
        unfocusedTextColor = NsGray900,
        focusedLeadingIconColor = NeurostageBrandBlue,
        unfocusedLeadingIconColor = NsGray400,
    )

@Composable
fun PatientIntakeScreen(
    onBack: () -> Unit,
    onSaved: (patientId: String) -> Unit,
    viewModel: PatientIntakeViewModel = hiltViewModel(),
) {
    val ui by viewModel.ui.collectAsStateWithLifecycle()

    BackHandler(onBack = onBack)

    val genderOptions = listOf(
        stringResource(R.string.gender_male),
        stringResource(R.string.gender_female),
        stringResource(R.string.gender_other),
    )

    Surface(modifier = Modifier.fillMaxSize(), color = NsDoctorScaffoldBg) {
        Column(modifier = Modifier.fillMaxSize()) {
            IntakeHeader(onBack = onBack)

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp)
                    .padding(top = 20.dp, bottom = 12.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                IntakeInfoBanner()

                Surface(
                    shape = cardShape,
                    color = NsWhite,
                    shadowElevation = 3.dp,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(18.dp),
                    ) {
                        Text(
                            text = stringResource(R.string.patient_intake_section),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = NeurostageBrandBlue,
                        )

                        OutlinedTextField(
                            value = ui.patientFullName,
                            onValueChange = viewModel::onPatientNameChange,
                            label = { Text(stringResource(R.string.patient_intake_name_label)) },
                            leadingIcon = {
                                Icon(
                                    Icons.Outlined.PersonOutline,
                                    contentDescription = null,
                                )
                            },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            shape = fieldShape,
                            colors = intakeFieldColors,
                        )

                        OutlinedTextField(
                            value = ui.age,
                            onValueChange = viewModel::onAgeChange,
                            label = { Text(stringResource(R.string.patient_intake_age_label)) },
                            leadingIcon = {
                                Icon(
                                    Icons.Outlined.CalendarMonth,
                                    contentDescription = null,
                                )
                            },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth(),
                            shape = fieldShape,
                            colors = intakeFieldColors,
                        )

                        HorizontalDivider(color = NsDoctorLoginFieldBorderIdle)

                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Text(
                                text = stringResource(R.string.patient_intake_gender_label),
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.SemiBold,
                                color = NsGray600,
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                            ) {
                                genderOptions.forEach { option ->
                                    val selected = ui.gender == option
                                    FilterChip(
                                        selected = selected,
                                        onClick = { viewModel.onGenderChange(option) },
                                        label = {
                                            Text(
                                                option,
                                                fontWeight = if (selected) {
                                                    FontWeight.SemiBold
                                                } else {
                                                    FontWeight.Normal
                                                },
                                            )
                                        },
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(12.dp),
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = NeurostageBrandBlue,
                                            selectedLabelColor = NsWhite,
                                            containerColor = NsDoctorLoginFieldBgIdle,
                                            labelColor = NsGray600,
                                        ),
                                        border = FilterChipDefaults.filterChipBorder(
                                            enabled = true,
                                            selected = selected,
                                            borderColor = NsDoctorLoginFieldBorderIdle,
                                            selectedBorderColor = NeurostageBrandBlue,
                                        ),
                                    )
                                }
                            }
                        }

                        ui.error?.let { error ->
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = NsStatusError.copy(alpha = 0.08f),
                                modifier = Modifier.fillMaxWidth(),
                            ) {
                                Text(
                                    text = error,
                                    color = NsStatusError,
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                )
                            }
                        }
                    }
                }
            }

            IntakeBottomBar(
                isSaving = ui.isSaving,
                onContinue = { viewModel.save(onSaved) },
            )
        }
    }
}

@Composable
private fun IntakeHeader(onBack: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(waveBottomShape)
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        NeurostageBrandBlue,
                        NeurostageBrandBlue.copy(alpha = 0.92f),
                    ),
                ),
            )
            .statusBarsPadding()
            .padding(bottom = 28.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        Icons.AutoMirrored.Outlined.ArrowBack,
                        contentDescription = stringResource(R.string.doctor_history_cd_back),
                        tint = NsWhite,
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.patient_intake_title),
                        style = MaterialTheme.typography.headlineSmall,
                        color = NsWhite,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = stringResource(R.string.patient_intake_subtitle),
                        style = MaterialTheme.typography.bodyMedium,
                        color = NsWhite.copy(alpha = 0.82f),
                        lineHeight = 20.sp,
                    )
                }
            }
        }
    }
}

@Composable
private fun IntakeInfoBanner() {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = NsDoctorAvatarSoftBg,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(NsWhite),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Outlined.Info,
                    contentDescription = null,
                    tint = NeurostageBrandBlue,
                    modifier = Modifier.size(20.dp),
                )
            }
            Text(
                text = stringResource(R.string.patient_intake_hint),
                style = MaterialTheme.typography.bodySmall,
                color = NsGray600,
                lineHeight = 18.sp,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun IntakeBottomBar(
    isSaving: Boolean,
    onContinue: () -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = NsWhite,
        shadowElevation = 8.dp,
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 20.dp, vertical = 16.dp),
        ) {
            Button(
                onClick = onContinue,
                enabled = !isSaving,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = NeurostageBrandBlue,
                    disabledContainerColor = NeurostageBrandBlue.copy(alpha = 0.4f),
                ),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 0.dp,
                    pressedElevation = 2.dp,
                ),
            ) {
                if (isSaving) {
                    CircularProgressIndicator(
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(24.dp),
                        color = NsWhite,
                    )
                } else {
                    Text(
                        text = stringResource(R.string.patient_intake_continue),
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                    )
                }
            }
        }
    }
}
