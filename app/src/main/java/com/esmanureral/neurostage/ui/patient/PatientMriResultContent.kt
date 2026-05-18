package com.esmanureral.neurostage.ui.patient

import android.graphics.Bitmap
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material.icons.outlined.ZoomIn
import androidx.compose.foundation.BorderStroke
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.Canvas
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.esmanureral.neurostage.R
import com.esmanureral.neurostage.ui.theme.PatientResultColors
import com.esmanureral.neurostage.ui.theme.PatientResultDimens
@Composable
fun PatientMriResultContent(
    bitmap: Bitmap?,
    stageIndex: Int,
    confidencePercent: Int,
    scores: List<Float>,
    onOpenGames: (() -> Unit)?,
    onImageClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    val stageLabels = stringArrayResource(R.array.dementia_stage_labels)
    val detectedLabel = stageLabels.getOrNull(stageIndex).orEmpty()
    val patientMessages = stringArrayResource(R.array.patient_stage_patient_messages)
    val stageInfos = stringArrayResource(R.array.patient_stage_education)
    val patientMessage = patientMessages.getOrNull(stageIndex).orEmpty()
    val stageEducation = stageInfos.getOrNull(stageIndex).orEmpty()
    val guidance = PatientScanGuidanceMapper.from(stageIndex)

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        PatientMriResultHeaderCard(
            bitmap = bitmap,
            confidencePercent = confidencePercent,
            detectedClassLabel = detectedLabel,
            patientMessage = patientMessage,
            onImageClick = onImageClick,
        )

        PatientProbabilitySection(
            scores = scores,
            selectedStageIndex = stageIndex,
        )

        if (stageEducation.isNotBlank()) {
            PatientStageEducationCard(text = stageEducation)
        }

        if (guidance.showGamesButton && onOpenGames != null) {
            PatientExerciseLink(
                label = stringResource(guidance.buttonLabelRes ?: R.string.guidance_mild_button),
                onClick = onOpenGames,
            )
        } else if (!guidance.showGamesButton) {
            PatientStageEducationCard(
                title = stringResource(guidance.titleRes),
                text = stringResource(guidance.bodyRes),
            )
        }
    }
}

@Composable
private fun PatientMriResultHeaderCard(
    bitmap: Bitmap?,
    confidencePercent: Int,
    detectedClassLabel: String,
    patientMessage: String,
    onImageClick: (() -> Unit)?,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(PatientResultColors.cardBg)
            .border(1.dp, PatientResultColors.primaryLight, RoundedCornerShape(16.dp))
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            PatientMriThumbnail(
                bitmap = bitmap,
                onImageClick = onImageClick,
            )

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = stringResource(R.string.patient_scan_scanned_badge),
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(PatientResultColors.primary)
                        .padding(horizontal = 10.dp, vertical = 5.dp),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                )

                Text(
                    text = stringResource(R.string.patient_scan_confidence_percent, confidencePercent),
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold,
                    color = PatientResultColors.primary,
                    lineHeight = 38.sp,
                )

                Text(
                    text = stringResource(R.string.patient_scan_model_confidence),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Medium,
                    color = PatientResultColors.textSecondary,
                )

                if (detectedClassLabel.isNotBlank()) {
                    Text(
                        text = stringResource(R.string.patient_scan_detected_class, detectedClassLabel),
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold,
                        color = PatientResultColors.textPrimary,
                        lineHeight = 20.sp,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }

        if (patientMessage.isNotBlank()) {
            Text(
                text = patientMessage,
                style = MaterialTheme.typography.bodyMedium,
                color = PatientResultColors.textPrimary,
                lineHeight = 22.sp,
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(PatientResultColors.primaryLight)
                .padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Icon(
                imageVector = Icons.Outlined.Shield,
                contentDescription = null,
                tint = PatientResultColors.primary,
                modifier = Modifier.size(14.dp),
            )
            Text(
                text = stringResource(R.string.patient_scan_not_diagnosis_badge),
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Medium,
                color = PatientResultColors.primary,
                lineHeight = 18.sp,
            )
        }
    }
}

@Composable
private fun PatientMriThumbnail(
    bitmap: Bitmap?,
    onImageClick: (() -> Unit)?,
) {
    val imageModifier = Modifier
        .size(132.dp)
        .clip(RoundedCornerShape(12.dp))
        .background(PatientResultColors.mriPanelBg)
        .then(
            if (onImageClick != null) {
                Modifier.clickable(onClick = onImageClick)
            } else {
                Modifier
            },
        )

    Box(
        modifier = imageModifier,
        contentAlignment = Alignment.Center,
    ) {
        if (bitmap != null) {
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = stringResource(R.string.home_screen_result_mri_label),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                contentScale = ContentScale.Fit,
            )
        }

        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(6.dp)
                .size(34.dp)
                .clip(CircleShape)
                .background(Color.Black.copy(alpha = 0.55f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Outlined.ZoomIn,
                contentDescription = stringResource(R.string.patient_scan_zoom_image),
                tint = Color.White,
                modifier = Modifier.size(18.dp),
            )
        }
    }
}

@Composable
fun PatientUnchangedResultContent(
    stageIndex: Int,
    confidencePercent: Int,
    scores: List<Float>,
    stageLabel: String,
    onReturnToHub: () -> Unit,
    onTryAnotherScan: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(PatientResultDimens.sectionGap),
    ) {
        PatientUnchangedNoticeCard(
            stageLabel = stageLabel,
            confidencePercent = confidencePercent,
        )

        PatientProbabilitySection(
            scores = scores,
            selectedStageIndex = stageIndex,
        )

        Button(
            onClick = onReturnToHub,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(PatientResultDimens.buttonCorner),
            colors = ButtonDefaults.buttonColors(
                containerColor = PatientResultColors.primary,
                contentColor = Color.White,
            ),
            contentPadding = PaddingValues(vertical = 14.dp),
        ) {
            Text(
                text = stringResource(R.string.patient_hub_scan_return),
                fontWeight = FontWeight.Bold,
            )
        }

        OutlinedButton(
            onClick = onTryAnotherScan,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(PatientResultDimens.buttonCorner),
            border = BorderStroke(1.dp, PatientResultColors.primary.copy(alpha = 0.35f)),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = PatientResultColors.primary,
            ),
            contentPadding = PaddingValues(vertical = 12.dp),
        ) {
            Text(
                text = stringResource(R.string.patient_hub_scan_try_another),
                fontWeight = FontWeight.SemiBold,
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = Icons.Outlined.Shield,
                contentDescription = null,
                tint = PatientResultColors.mutedText,
                modifier = Modifier.size(14.dp),
            )
            Spacer(Modifier.width(6.dp))
            Text(
                text = stringResource(R.string.patient_scan_not_diagnosis_badge),
                style = MaterialTheme.typography.labelSmall,
                color = PatientResultColors.mutedText,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
private fun PatientUnchangedNoticeCard(
    stageLabel: String,
    confidencePercent: Int,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(PatientResultColors.cardBg)
            .border(1.dp, PatientResultColors.primaryLight, RoundedCornerShape(16.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = stringResource(R.string.patient_hub_scan_unchanged_title),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = PatientResultColors.primary,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )

        Text(
            text = stringResource(R.string.patient_hub_scan_unchanged_body),
            style = MaterialTheme.typography.bodyMedium,
            color = PatientResultColors.textSecondary,
            lineHeight = 22.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(PatientResultColors.primaryLight)
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Text(
                text = stringResource(R.string.patient_scan_detected_class, stageLabel),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = PatientResultColors.textPrimary,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
            Text(
                text = stringResource(R.string.patient_scan_confidence_line, confidencePercent),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Medium,
                color = PatientResultColors.primary,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
fun PatientProbabilitySection(
    scores: List<Float>,
    selectedStageIndex: Int,
) {
    val shortLabels = stringArrayResource(R.array.patient_stage_short_labels)
    val entries = listOf(
        Triple(0, shortLabels[0], PatientResultColors.primary),
        Triple(1, shortLabels[1], PatientResultColors.primary.copy(alpha = 0.55f)),
        Triple(2, shortLabels[2], PatientResultColors.mutedBar),
        Triple(3, shortLabels[3], PatientResultColors.primary.copy(alpha = 0.7f)),
    ).sortedByDescending { scores.getOrElse(it.first) { 0f } }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(PatientResultColors.cardBg)
            .border(1.dp, PatientResultColors.primaryLight, RoundedCornerShape(16.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Text(
            text = stringResource(R.string.patient_scan_probability_title),
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            color = PatientResultColors.textPrimary,
        )

        PatientScoreLineChart(
            scores = scores,
            selectedStageIndex = selectedStageIndex,
        )

        entries.forEach { (idx, label, baseColor) ->
            val scorePct = (scores.getOrElse(idx) { 0f } * 100).toInt()
            val isMuted = scorePct <= 2
            val barAnim by animateFloatAsState(
                targetValue = scorePct / 100f,
                animationSpec = tween(700, easing = FastOutSlowInEasing),
                label = "patientProb$idx",
            )
            val dotColor = if (isMuted) PatientResultColors.mutedBar else baseColor
            val barColor = if (isMuted) PatientResultColors.mutedBar else baseColor
            val labelColor = if (isMuted) PatientResultColors.mutedText else PatientResultColors.textPrimary
            val percentColor = when {
                isMuted -> PatientResultColors.mutedText
                idx == selectedStageIndex -> PatientResultColors.primary
                else -> PatientResultColors.textPrimary
            }

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(dotColor),
                    )
                    Text(
                        text = label,
                        style = MaterialTheme.typography.bodySmall,
                        color = labelColor,
                        fontWeight = if (idx == selectedStageIndex && !isMuted) FontWeight.SemiBold else FontWeight.Normal,
                        modifier = Modifier.weight(1f),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        lineHeight = 18.sp,
                    )
                    Text(
                        text = "$scorePct%",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = if (idx == selectedStageIndex && !isMuted) FontWeight.Bold else FontWeight.Normal,
                        color = percentColor,
                        lineHeight = 18.sp,
                    )
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(PatientResultColors.mutedBar),
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(barAnim.coerceAtLeast(if (scorePct > 0) 0.03f else 0f))
                            .clip(RoundedCornerShape(4.dp))
                            .background(barColor),
                    )
                }
            }
        }
    }
}

@Composable
private fun PatientScoreLineChart(
    scores: List<Float>,
    selectedStageIndex: Int,
) {
    val stageOrder = listOf(0, 1, 2, 3)
    val primary = PatientResultColors.primary
    val mutedBar = PatientResultColors.mutedBar
    val pointColors = listOf(
        primary,
        primary.copy(alpha = 0.55f),
        mutedBar,
        primary.copy(alpha = 0.7f),
    )
    val gridColor = mutedBar.copy(alpha = 0.85f)
    val lineColor = primary.copy(alpha = 0.35f)

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(88.dp),
    ) {
        val padX = 20.dp.toPx()
        val padY = 14.dp.toPx()
        val chartW = size.width - padX * 2
        val chartH = size.height - padY * 2
        val stepX = if (stageOrder.size > 1) chartW / (stageOrder.size - 1) else 0f

        val points = stageOrder.mapIndexed { i, idx ->
            val score = scores.getOrElse(idx) { 0f }.coerceIn(0f, 1f)
            Offset(
                x = padX + i * stepX,
                y = padY + chartH * (1f - score),
            )
        }

        repeat(4) { line ->
            val y = padY + chartH * line / 3f
            drawLine(
                color = gridColor,
                start = Offset(padX, y),
                end = Offset(padX + chartW, y),
                strokeWidth = 1f,
            )
        }

        val path = Path().apply {
            points.forEachIndexed { i, pt ->
                if (i == 0) moveTo(pt.x, pt.y) else lineTo(pt.x, pt.y)
            }
        }
        drawPath(
            path = path,
            color = lineColor,
            style = Stroke(width = 2.5f, cap = StrokeCap.Round),
        )

        points.forEachIndexed { i, pt ->
            val idx = stageOrder[i]
            val isSelected = idx == selectedStageIndex
            val radius = if (isSelected) 6.dp.toPx() else 4.5.dp.toPx()
            val color = if (isSelected) primary else pointColors.getOrElse(i) { mutedBar }
            drawCircle(color = Color.White, radius = radius + 2f, center = pt)
            drawCircle(color = color, radius = radius, center = pt)
        }
    }
}

@Composable
private fun PatientStageEducationCard(
    text: String,
    title: String? = null,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(PatientResultColors.cardBg)
            .border(1.dp, PatientResultColors.primaryLight, RoundedCornerShape(16.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = title ?: stringResource(R.string.patient_scan_stage_info_title),
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            color = PatientResultColors.textPrimary,
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = PatientResultColors.textSecondary,
            lineHeight = 22.sp,
        )
    }
}

@Composable
private fun PatientExerciseLink(
    label: String,
    onClick: () -> Unit,
) {
    Spacer(Modifier.height(10.dp))
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, PatientResultColors.primary.copy(alpha = 0.14f), RoundedCornerShape(12.dp))
            .background(PatientResultColors.cardBg)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = PatientResultColors.primary.copy(alpha = 0.85f),
        )
        Icon(
            imageVector = Icons.AutoMirrored.Outlined.ArrowForward,
            contentDescription = null,
            tint = PatientResultColors.primary.copy(alpha = 0.5f),
            modifier = Modifier.size(18.dp),
        )
    }
}

@Composable
fun PatientResultPageBackground(): Color = PatientResultColors.pageBg
