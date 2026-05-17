package com.esmanureral.neurostage.ui.doctor

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.esmanureral.neurostage.R
import com.esmanureral.neurostage.data.MrScanRecord
import com.esmanureral.neurostage.ui.theme.NsWhite
import com.esmanureral.neurostage.util.Constants
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val detailDateFormat = SimpleDateFormat(
    Constants.Format.SCAN_DATETIME,
    Locale(Constants.LocaleConfig.LANG_TR, Constants.LocaleConfig.REGION_TR),
)

@Composable
fun DoctorResultDetailScreen(
    onBack: () -> Unit,
    viewModel: DoctorResultDetailViewModel = hiltViewModel(),
) {
    val record by viewModel.selected.collectAsStateWithLifecycle()

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        Icons.Outlined.ArrowBack,
                        contentDescription = stringResource(R.string.doctor_history_cd_back),
                    )
                }
                Text(
                    text = stringResource(R.string.doctor_result_detail_title),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 4.dp),
                )
            }

            val r = record
            if (r == null) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = stringResource(R.string.doctor_result_not_found),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            } else {
                DetailContent(record = r)
            }
        }
    }
}

@Composable
private fun DetailContent(record: MrScanRecord) {
    val confidencePct = (record.confidence * 100f).toInt().coerceIn(0, 100)
    val scores = record.scores
    val formatter = remember { detailDateFormat }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Text(
                    text = stringArrayResource(R.array.dementia_stage_labels)
                        .getOrNull(record.stageIndex) ?: record.label,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = formatter.format(Date(record.timestamp)),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    text = stringResource(R.string.doctor_result_confidence_score, confidencePct),
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }

        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Text(
                    text = stringResource(R.string.doctor_result_class_scores_title),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                )
                if (scores == null) {
                    Text(
                        text = stringResource(R.string.doctor_result_scores_legacy),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                } else {
                    ScoreBars(scores = scores)
                    Spacer(Modifier.height(4.dp))
                    TinyLineChart(values = scores)
                }
            }
        }
    }
}

@Composable
private fun ScoreBars(scores: FloatArray) {
    val labels = stringArrayResource(R.array.dementia_stage_labels)
    val max = (scores.maxOrNull() ?: 1f).coerceAtLeast(0.0001f)
    val trackColor = MaterialTheme.colorScheme.surfaceVariant
    val barColor = MaterialTheme.colorScheme.primary
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
                    )
                    Text(
                        text = stringResource(R.string.doctor_result_score_percent, pct),
                        fontWeight = FontWeight.SemiBold,
                    )
                }
                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp)
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

@Composable
private fun TinyLineChart(values: FloatArray) {
    val points = remember(values) { values.map { it.coerceIn(0f, 1f) } }
    val stroke = MaterialTheme.colorScheme.primary
    val grid = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(110.dp)
    ) {
        val w = size.width
        val h = size.height
        repeat(3) { i ->
            val y = h * (i + 1) / 4f
            drawLine(grid, start = Offset(0f, y), end = Offset(w, y), strokeWidth = 2f)
        }
        if (points.isEmpty()) return@Canvas
        val stepX = if (points.size == 1) 0f else w / (points.size - 1)
        var last: Offset? = null
        points.forEachIndexed { i, v ->
            val x = stepX * i
            val y = h - (h * v)
            val p = Offset(x, y)
            last?.let { prev ->
                drawLine(stroke, start = prev, end = p, strokeWidth = 6f)
            }
            drawCircle(color = NsWhite, radius = 10f, center = p)
            drawCircle(color = stroke, radius = 7f, center = p)
            last = p
        }
    }
}