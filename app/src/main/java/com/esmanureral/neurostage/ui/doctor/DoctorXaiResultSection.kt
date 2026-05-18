package com.esmanureral.neurostage.ui.doctor

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Psychology
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.esmanureral.neurostage.R
import com.esmanureral.neurostage.ui.theme.NsGray400
import com.esmanureral.neurostage.ui.theme.NsRose50
import com.esmanureral.neurostage.ui.theme.NsStatusError
import com.esmanureral.neurostage.ui.theme.NsGray800
import com.esmanureral.neurostage.ui.theme.NsIndigo500
import com.esmanureral.neurostage.ui.theme.NsNavy
import com.esmanureral.neurostage.ui.theme.NsWhite
import com.esmanureral.neurostage.ui.theme.ScanColors
import com.esmanureral.neurostage.ui.theme.ScanDimens
import com.esmanureral.neurostage.xai.GeminiReport
import com.esmanureral.neurostage.xai.GradCamResult
import com.esmanureral.neurostage.xai.McDropoutResult
import com.esmanureral.neurostage.xai.XaiUiState
import com.esmanureral.neurostage.xai.parseAiReportBlocks

@Composable
fun DoctorXaiResultsSection(
    xaiState: XaiUiState,
    stageLabel: String,
    confidencePct: Int,
    onRequestAiReport: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var showAiReport by remember { mutableStateOf(false) }

    LaunchedEffect(xaiState.geminiReport) {
        if (xaiState.geminiReport != null) {
            showAiReport = true
        }
    }

    val isModelExplainLoading = xaiState.isMcLoading || xaiState.isGradCamLoading
    val modelExplainReady = xaiState.mcResult != null && xaiState.gradCamResult != null

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(ScanDimens.resultBlockGap),
    ) {
        if (isModelExplainLoading) {
            DoctorModelExplainLoadingBanner()
        }

        xaiState.gradCamResult?.let { gradCam ->
            DoctorGradCamCard(gradCam = gradCam)
        }

        xaiState.gradCamError?.let { err ->
            ErrorBanner(
                String.format(
                    stringResource(R.string.home_screen_gradcam_error),
                    err,
                ),
            )
        }

        xaiState.mcError?.let { err ->
            ErrorBanner(
                String.format(
                    stringResource(R.string.home_screen_mc_error),
                    err,
                ),
            )
        }

        if (modelExplainReady) {
            DoctorModelDecisionCard(
                stageLabel = stageLabel,
                confidencePct = confidencePct,
                mcResult = xaiState.mcResult,
                gradCam = xaiState.gradCamResult,
            )
        }

        AnimatedVisibility(
            visible = showAiReport,
            enter = fadeIn() + slideInVertically { it / 3 },
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(ScanDimens.resultBlockGap),
            ) {
                if (xaiState.isGeminiLoading) {
                    DoctorAiReportLoadingBanner()
                }
                xaiState.geminiReport?.let { report ->
                    DoctorAiReportCards(report = report)
                }
                xaiState.geminiError?.let { err ->
                    ErrorBanner(
                        String.format(
                            stringResource(R.string.home_screen_gemini_error),
                            err,
                        ),
                    )
                }
            }
        }

        if (!showAiReport || xaiState.geminiReport == null) {
            DoctorAskAiFab(
                isLoading = xaiState.isGeminiLoading,
                enabled = modelExplainReady && !xaiState.isGeminiLoading,
                onClick = {
                    showAiReport = true
                    onRequestAiReport()
                },
            )
        }
    }
}

@Composable
private fun DoctorModelExplainLoadingBanner() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(ScanDimens.xaiLoadingCorner))
            .background(ScanColors.xaiLoadingBackground)
            .border(
                ScanDimens.xaiLoadingBorder,
                ScanColors.xaiLoadingBorder,
                RoundedCornerShape(ScanDimens.xaiLoadingCorner),
            )
            .padding(ScanDimens.xaiLoadingPadding),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(ScanDimens.xaiLoadingGap),
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(ScanDimens.xaiSpinnerSize),
            strokeWidth = ScanDimens.xaiSpinnerStroke,
            color = ScanColors.xaiLoadingAccent,
        )
        Column(verticalArrangement = Arrangement.spacedBy(ScanDimens.xaiTextGap)) {
            Text(
                text = stringResource(R.string.home_screen_xai_model_loading_title),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = ScanColors.xaiLoadingTitle,
            )
            Text(
                text = stringResource(R.string.home_screen_xai_model_loading_subtitle),
                style = MaterialTheme.typography.bodySmall,
                color = ScanColors.xaiLoadingSubtitle,
            )
        }
    }
}

@Composable
private fun DoctorAiReportLoadingBanner() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(ScanDimens.xaiLoadingCorner))
            .background(ScanColors.xaiLoadingBackground)
            .border(
                ScanDimens.xaiLoadingBorder,
                ScanColors.xaiLoadingBorder,
                RoundedCornerShape(ScanDimens.xaiLoadingCorner),
            )
            .padding(ScanDimens.xaiLoadingPadding),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(ScanDimens.xaiLoadingGap),
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(ScanDimens.xaiSpinnerSize),
            strokeWidth = ScanDimens.xaiSpinnerStroke,
            color = ScanColors.xaiLoadingAccent,
        )
        Column(verticalArrangement = Arrangement.spacedBy(ScanDimens.xaiTextGap)) {
            Text(
                text = stringResource(R.string.home_screen_xai_loading_title),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = ScanColors.xaiLoadingTitle,
            )
            Text(
                text = stringResource(R.string.home_screen_xai_loading_subtitle),
                style = MaterialTheme.typography.bodySmall,
                color = ScanColors.xaiLoadingSubtitle,
            )
        }
    }
}

@Composable
private fun DoctorGradCamCard(gradCam: GradCamResult) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(ScanDimens.resultCardCorner))
            .background(NsWhite)
            .padding(ScanDimens.resultScoresPadding),
        verticalArrangement = Arrangement.spacedBy(ScanDimens.reportCardGap),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(ScanDimens.reportIconGap),
        ) {
            Icon(
                imageVector = Icons.Outlined.Visibility,
                contentDescription = null,
                tint = ScanColors.xaiSummaryAccent,
                modifier = Modifier.size(ScanDimens.reportIconSize),
            )
            Column {
                Text(
                    text = stringResource(R.string.home_screen_gradcam_title),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = ScanColors.xaiSummaryAccent,
                )
                Text(
                    text = stringResource(R.string.home_screen_gradcam_subtitle),
                    style = MaterialTheme.typography.bodySmall,
                    color = NsGray800,
                )
            }
        }
        Image(
            bitmap = gradCam.heatmapBitmap.asImageBitmap(),
            contentDescription = stringResource(R.string.home_screen_gradcam_title),
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(RoundedCornerShape(ScanDimens.resultCardCorner)),
            contentScale = ContentScale.Fit,
        )
    }
}

@Composable
private fun DoctorModelDecisionCard(
    stageLabel: String,
    confidencePct: Int,
    mcResult: McDropoutResult?,
    gradCam: GradCamResult?,
) {
    val uncertaintyPct = ((mcResult?.topStd ?: 0f) * 100f)
    val peakScore = gradCam?.peakActivation?.takeIf { it > 0f }
        ?: gradCam?.rawCam?.maxOrNull()
        ?: 0f
    val peakActivation = (peakScore * 100f).toInt()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(ScanDimens.resultCardCorner))
            .background(NsWhite)
            .padding(ScanDimens.resultScoresPadding),
        verticalArrangement = Arrangement.spacedBy(ScanDimens.reportCardGap),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(ScanDimens.reportIconGap),
        ) {
            Icon(
                imageVector = Icons.Outlined.Psychology,
                contentDescription = null,
                tint = ScanColors.xaiSummaryAccent,
                modifier = Modifier.size(ScanDimens.reportIconSize),
            )
            Text(
                text = stringResource(R.string.home_screen_model_decision_title),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = ScanColors.xaiSummaryAccent,
            )
        }
        Text(
            text = stringResource(
                R.string.home_screen_model_decision_prediction,
                stageLabel,
                confidencePct,
            ),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = NsNavy,
            lineHeight = ScanDimens.reportBodyLineHeight,
        )
        gradCam?.activeRegion?.takeIf { it.isNotBlank() }?.let { region ->
            Text(
                text = stringResource(R.string.home_screen_model_decision_region, region),
                style = MaterialTheme.typography.bodyMedium,
                color = NsGray800,
                lineHeight = ScanDimens.reportSectionLineHeight,
            )
        }
        if (uncertaintyPct > 0f) {
            Text(
                text = stringResource(
                    R.string.home_screen_model_decision_uncertainty,
                    uncertaintyPct,
                ),
                style = MaterialTheme.typography.bodyMedium,
                color = NsGray800,
                lineHeight = ScanDimens.reportSectionLineHeight,
            )
        }
        if (peakActivation > 0) {
            Text(
                text = stringResource(
                    R.string.home_screen_model_decision_peak,
                    peakActivation,
                ),
                style = MaterialTheme.typography.bodyMedium,
                color = NsGray800,
                lineHeight = ScanDimens.reportSectionLineHeight,
            )
        }
        Text(
            text = stringResource(R.string.home_screen_model_decision_footer),
            style = MaterialTheme.typography.bodySmall,
            color = NsGray400,
            lineHeight = ScanDimens.reportSectionLineHeight,
        )
    }
}

@Composable
private fun DoctorAiReportCards(report: GeminiReport) {
    val summaryKeyword = stringResource(R.string.home_screen_xai_summary_keyword)
    val knownHeadings = stringArrayResource(R.array.xai_report_headings).toList()
    val aiBlocks = parseAiReportBlocks(report.text, knownHeadings)
    val summaryBlock = aiBlocks.firstOrNull {
        it.first?.contains(summaryKeyword, ignoreCase = true) == true
    } ?: aiBlocks.firstOrNull()
    val otherBlocks = aiBlocks.filter { it != summaryBlock }

    val summaryFallback = stringResource(R.string.home_screen_xai_summary_fallback)
    val clinicFallback = stringResource(R.string.home_screen_xai_clinic_fallback)

    if (summaryBlock != null) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(ScanDimens.resultCardCorner))
                .background(NsWhite)
                .padding(ScanDimens.resultScoresPadding),
            verticalArrangement = Arrangement.spacedBy(ScanDimens.reportCardGap),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(ScanDimens.reportIconGap),
            ) {
                Icon(
                    Icons.Outlined.AutoAwesome,
                    contentDescription = null,
                    tint = ScanColors.xaiSummaryAccent,
                    modifier = Modifier.size(ScanDimens.reportIconSize),
                )
                Text(
                    summaryBlock.first ?: summaryFallback,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = ScanColors.xaiSummaryAccent,
                )
            }
            Text(
                summaryBlock.second.replace("**", ""),
                style = MaterialTheme.typography.bodyMedium,
                color = NsGray800,
                lineHeight = ScanDimens.reportBodyLineHeight,
            )
        }
    }

    otherBlocks.forEach { (title, content) ->
        val cleanContent = content
            .replace("**", "")
            .replace(Regex("(?m)^- "), "• ")
            .replace(Regex("(?m)^\\* "), "• ")
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(ScanDimens.resultCardCorner))
                .background(NsWhite)
                .padding(ScanDimens.resultScoresPadding),
            verticalArrangement = Arrangement.spacedBy(ScanDimens.reportCardGap),
        ) {
            Text(
                title?.uppercase() ?: clinicFallback,
                style = MaterialTheme.typography.labelMedium,
                color = NsIndigo500,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = ScanDimens.reportHeadingLetterSpacing,
            )
            if (cleanContent.isNotBlank()) {
                Text(
                    cleanContent,
                    style = MaterialTheme.typography.bodyMedium,
                    color = NsGray800,
                    lineHeight = ScanDimens.reportSectionLineHeight,
                )
            }
        }
    }

    Text(
        text = stringResource(R.string.home_screen_result_disclaimer),
        style = MaterialTheme.typography.bodySmall,
        color = NsGray400,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                top = ScanDimens.resultDisclaimerTop,
                bottom = ScanDimens.resultDisclaimerBottom,
            ),
    )
}

@Composable
private fun DoctorAskAiFab(
    isLoading: Boolean,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        contentAlignment = Alignment.Center,
    ) {
        FloatingActionButton(
            onClick = { if (enabled) onClick() },
            modifier = Modifier
                .size(72.dp)
                .alpha(if (enabled) 1f else 0.45f),
            shape = CircleShape,
            containerColor = ScanColors.xaiSummaryAccent,
            contentColor = NsWhite,
            elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 8.dp),
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(28.dp),
                    strokeWidth = 2.5.dp,
                    color = NsWhite,
                )
            } else {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    Icon(
                        imageVector = Icons.Outlined.AutoAwesome,
                        contentDescription = stringResource(R.string.home_screen_ask_ai),
                        modifier = Modifier.size(26.dp),
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = stringResource(R.string.home_screen_ask_ai),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        maxLines = 2,
                    )
                }
            }
        }
    }
}

@Composable
private fun ErrorBanner(message: String) {
    Text(
        message,
        style = MaterialTheme.typography.bodySmall,
        color = NsStatusError,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(ScanDimens.errorBannerCorner))
            .background(NsRose50)
            .padding(ScanDimens.errorBannerPadding),
    )
}
