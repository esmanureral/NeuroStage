package com.esmanureral.neurostage.ui.patient.scan

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.esmanureral.neurostage.R
import com.esmanureral.neurostage.data.MrScanRecord
import com.esmanureral.neurostage.ui.doctor.DoctorResultDetailViewModel
import com.esmanureral.neurostage.ui.theme.PatientColors
import com.esmanureral.neurostage.ui.theme.PatientDimens
import com.esmanureral.neurostage.util.Constants
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val detailDateFormat = SimpleDateFormat(
    Constants.Format.SCAN_DATETIME,
    Locale(Constants.LocaleConfig.LANG_TR, Constants.LocaleConfig.REGION_TR),
)

@Composable
fun PatientScanDetailScreen(
    onBack: () -> Unit,
    viewModel: DoctorResultDetailViewModel = hiltViewModel(),
) {
    val record by viewModel.selected.collectAsStateWithLifecycle()

    Scaffold(
        containerColor = PatientColors.gameBackgroundCream,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.patient_scan_history_back),
                        tint = PatientColors.primary,
                    )
                }
                Text(
                    text = stringResource(R.string.patient_scan_detail_title),
                    fontSize = PatientDimens.gameHubHeaderTitleSize,
                    fontWeight = FontWeight.Bold,
                    color = PatientColors.textPrimary,
                )
            }
        },
    ) { innerPadding ->
        val r = record
        if (r == null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = PatientDimens.gameHubScreenPadding),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = stringResource(R.string.doctor_result_not_found),
                    color = PatientColors.textSecondary,
                )
            }
        } else {
            PatientScanDetailContent(
                record = r,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = PatientDimens.gameHubScreenPadding),
            )
        }
    }
}

@Composable
private fun PatientScanDetailContent(
    record: MrScanRecord,
    modifier: Modifier = Modifier,
) {
    val confidencePct = (record.confidence * 100f).toInt().coerceIn(0, 100)
    val scores = record.scores
    val formatter = remember { detailDateFormat }
    val stageLabels = stringArrayResource(R.array.dementia_stage_labels)
    val descriptions = stringArrayResource(R.array.home_screen_class_descriptions)
    val stageLabel = stageLabels.getOrNull(record.stageIndex) ?: record.label
    val description = descriptions.getOrNull(record.stageIndex).orEmpty()

    Column(
        modifier = modifier.padding(vertical = PatientDimens.gameHubSectionGap),
        verticalArrangement = Arrangement.spacedBy(PatientDimens.gameHubCardGap),
    ) {
        Surface(
            shape = RoundedCornerShape(PatientDimens.gameHubPlayfulCardCorner),
            color = PatientColors.surface,
            shadowElevation = PatientDimens.gameHubCardElevation,
        ) {
            Column(
                modifier = Modifier.padding(PatientDimens.gameHubPlayfulCardPadding),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Text(
                    text = stageLabel,
                    fontSize = PatientDimens.gameHubGreetingSize,
                    fontWeight = FontWeight.Bold,
                    color = PatientColors.textPrimary,
                )
                Text(
                    text = formatter.format(Date(record.timestamp)),
                    fontSize = PatientDimens.gameHubGreetingSubtitleSize,
                    color = PatientColors.textSecondary,
                )
                Text(
                    text = stringResource(R.string.patient_scan_history_confidence, confidencePct),
                    fontSize = PatientDimens.gameHubPlayfulTitleSize,
                    fontWeight = FontWeight.SemiBold,
                    color = PatientColors.primary,
                )
                if (description.isNotBlank()) {
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = description,
                        fontSize = PatientDimens.gameHubGreetingSubtitleSize,
                        color = PatientColors.textSecondary,
                        lineHeight = PatientDimens.gameHubPlayfulTitleLineHeight,
                    )
                }
            }
        }

        if (scores != null) {
            Surface(
                shape = RoundedCornerShape(PatientDimens.gameHubPlayfulCardCorner),
                color = PatientColors.surface,
                shadowElevation = PatientDimens.gameHubCardElevation,
            ) {
                Column(
                    modifier = Modifier.padding(PatientDimens.gameHubPlayfulCardPadding),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Text(
                        text = stringResource(R.string.doctor_result_class_scores_title),
                        fontSize = PatientDimens.gameHubPlayfulTitleSize,
                        fontWeight = FontWeight.Bold,
                        color = PatientColors.textPrimary,
                    )
                    PatientScoreBars(scores = scores)
                }
            }
        }
    }
}

@Composable
private fun PatientScoreBars(scores: List<Float>) {
    val labels = stringArrayResource(R.array.dementia_stage_labels)
    val max = (scores.maxOrNull() ?: 1f).coerceAtLeast(0.0001f)
    val trackColor = PatientColors.primaryLight
    val barColor = PatientColors.primary

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        scores.forEachIndexed { idx, v ->
            val pct = (v * 100).toInt().coerceIn(0, 100)
            val rowLabel = labels.getOrNull(idx)
                ?: stringResource(R.string.doctor_result_class_fallback, idx)
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = rowLabel,
                        modifier = Modifier.weight(1f),
                        fontSize = PatientDimens.gameHubGreetingSubtitleSize,
                        color = PatientColors.textPrimary,
                    )
                    Text(
                        text = stringResource(R.string.doctor_result_score_percent, pct),
                        fontWeight = FontWeight.SemiBold,
                        fontSize = PatientDimens.gameHubGreetingSubtitleSize,
                        color = PatientColors.textPrimary,
                    )
                }
                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp),
                ) {
                    val w = size.width
                    val h = size.height
                    val ratio = (v / max).coerceIn(0f, 1f)
                    drawRoundRect(
                        color = trackColor,
                        cornerRadius = CornerRadius(99f, 99f),
                        size = Size(w, h),
                    )
                    drawRoundRect(
                        color = barColor,
                        cornerRadius = CornerRadius(99f, 99f),
                        size = Size(w * ratio, h),
                    )
                }
            }
        }
    }
}
