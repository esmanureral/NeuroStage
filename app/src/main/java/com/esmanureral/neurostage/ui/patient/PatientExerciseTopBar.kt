package com.esmanureral.neurostage.ui.patient

import com.esmanureral.neurostage.ui.theme.PatientColors
import com.esmanureral.neurostage.ui.theme.PatientDimens
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.esmanureral.neurostage.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatientExerciseTopBar(
    title: String,
    subtitle: String? = null,
    onBack: () -> Unit,
    backLabel: String = stringResource(R.string.patient_home_cd_back),
) {
    BackHandler(onBack = onBack)

    Column {
        CenterAlignedTopAppBar(
            title = {
                Column(horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally) {
                    Text(
                        text = title,
                        fontWeight = FontWeight.Bold,
                        fontSize = PatientDimens.exerciseTopBarTitleSoloSize,
                        textAlign = TextAlign.Center,
                    )
                    if (subtitle != null) {
                        Text(
                            text = subtitle,
                            fontSize = PatientDimens.exerciseTopBarSubtitleSize,
                            color = PatientColors.textSecondary,
                            textAlign = TextAlign.Center,
                        )
                    }
                }
            },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = backLabel,
                    )
                }
            },
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = PatientColors.surface,
                titleContentColor = PatientColors.textPrimary,
                navigationIconContentColor = PatientColors.primary,
            ),
        )
        HorizontalDivider(color = PatientColors.divider)
    }
}
