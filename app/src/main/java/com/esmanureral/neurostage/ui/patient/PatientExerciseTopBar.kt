package com.esmanureral.neurostage.ui.patient

import com.esmanureral.neurostage.ui.theme.PatientColors
import com.esmanureral.neurostage.ui.theme.PatientDimens
import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.esmanureral.neurostage.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatientExerciseTopBar(
    title: String,
    subtitle: String? = null,
    onBack: () -> Unit,
) {
    TopAppBar(
        title = {
            if (subtitle != null) {
                Column {
                    Text(
                        title,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = PatientDimens.exerciseTopBarTitleSize
                    )
                    Text(
                        subtitle,
                        fontSize = PatientDimens.exerciseTopBarSubtitleSize,
                        color = PatientColors.textSecondary
                    )
                }
            } else {
                Text(
                    title,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = PatientDimens.exerciseTopBarTitleSoloSize
                )
            }
        },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.patient_home_cd_back),
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = PatientColors.surface,
            titleContentColor = PatientColors.textPrimary,
            navigationIconContentColor = PatientColors.textPrimary,
        ),
    )
}
