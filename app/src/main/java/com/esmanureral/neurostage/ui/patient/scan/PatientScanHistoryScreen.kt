package com.esmanureral.neurostage.ui.patient.scan

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.esmanureral.neurostage.R
import com.esmanureral.neurostage.data.MrScanRecord
import com.esmanureral.neurostage.ui.doctor.DoctorHistoryViewModel
import com.esmanureral.neurostage.ui.theme.PatientColors
import com.esmanureral.neurostage.ui.theme.PatientDimens
import com.esmanureral.neurostage.util.Constants
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val scanDateFormat = SimpleDateFormat(
    Constants.Format.SCAN_DATETIME,
    Locale(Constants.LocaleConfig.LANG_TR, Constants.LocaleConfig.REGION_TR),
)

@Composable
fun PatientScanHistoryScreen(
    onBack: () -> Unit,
    onOpenDetail: (Long) -> Unit,
    viewModel: DoctorHistoryViewModel = hiltViewModel(),
) {
    val history by viewModel.history.collectAsStateWithLifecycle()

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
                        tint = PatientColors.textPrimary,
                    )
                }
                Text(
                    text = stringResource(R.string.patient_scan_history_title),
                    fontSize = PatientDimens.gameHubHeaderTitleSize,
                    fontWeight = FontWeight.Bold,
                    color = PatientColors.textPrimary,
                )
            }
        },
    ) { innerPadding ->
        if (history.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = PatientDimens.gameHubScreenPadding),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = stringResource(R.string.patient_scan_history_empty),
                    fontSize = PatientDimens.gameHubGreetingSubtitleSize,
                    color = PatientColors.textSecondary,
                    textAlign = TextAlign.Center,
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(
                    horizontal = PatientDimens.gameHubScreenPadding,
                    vertical = PatientDimens.gameHubSectionGap,
                ),
                verticalArrangement = Arrangement.spacedBy(PatientDimens.gameHubCardGap),
            ) {
                items(history, key = { it.timestamp }) { item ->
                    PatientScanHistoryRow(
                        item = item,
                        onClick = { onOpenDetail(item.timestamp) },
                    )
                }
            }
        }
    }
}

@Composable
private fun PatientScanHistoryRow(
    item: MrScanRecord,
    onClick: () -> Unit,
) {
    val confidencePct = (item.confidence * 100f).toInt().coerceIn(0, 100)
    val stageLabels = stringArrayResource(R.array.dementia_stage_labels)
    val stageLabel = stageLabels.getOrNull(item.stageIndex) ?: item.label

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(PatientDimens.gameHubPlayfulCardCorner),
        color = PatientColors.surface,
        shadowElevation = PatientDimens.gameHubCardElevation,
        tonalElevation = PatientDimens.gameHubCardElevation,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(PatientDimens.gameHubPlayfulCardPadding),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    text = stageLabel,
                    fontSize = PatientDimens.gameHubPlayfulTitleSize,
                    fontWeight = FontWeight.Bold,
                    color = PatientColors.textPrimary,
                )
                Text(
                    text = scanDateFormat.format(Date(item.timestamp)),
                    fontSize = PatientDimens.gameHubGreetingSubtitleSize,
                    color = PatientColors.textSecondary,
                )
                Text(
                    text = stringResource(R.string.patient_scan_history_confidence, confidencePct),
                    fontSize = PatientDimens.gameHubStageChipTextSize,
                    color = PatientColors.textSecondary,
                )
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = stringResource(R.string.patient_scan_history_open_detail),
                tint = PatientColors.textSecondary,
                modifier = Modifier.size(PatientDimens.gameHubChevronSize),
            )
        }
    }
}
