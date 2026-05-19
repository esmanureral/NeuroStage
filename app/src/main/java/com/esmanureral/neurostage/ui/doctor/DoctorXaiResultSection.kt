package com.esmanureral.neurostage.ui.doctor

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.ZoomIn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.esmanureral.neurostage.R
import com.esmanureral.neurostage.ui.patient.PatientProbabilitySection
import com.esmanureral.neurostage.ui.theme.NeurostageBrandBlue
import com.esmanureral.neurostage.ui.theme.NsDoctorAvatarSoftBg
import com.esmanureral.neurostage.ui.theme.NsDoctorScaffoldBg
import com.esmanureral.neurostage.ui.theme.NsChipGreenBg
import com.esmanureral.neurostage.ui.theme.NsChipGreenFg
import com.esmanureral.neurostage.ui.theme.NsChipIndigoBg
import com.esmanureral.neurostage.ui.theme.NsChipIndigoFg
import com.esmanureral.neurostage.ui.theme.NsGray400
import com.esmanureral.neurostage.ui.theme.NsGray600
import com.esmanureral.neurostage.ui.theme.NsGray700
import com.esmanureral.neurostage.ui.theme.NsGray800
import com.esmanureral.neurostage.ui.theme.NsGray900
import com.esmanureral.neurostage.ui.theme.NsIndigo500
import com.esmanureral.neurostage.ui.theme.NsRose50
import com.esmanureral.neurostage.ui.theme.NsSlate50
import com.esmanureral.neurostage.ui.theme.NsSlate100
import com.esmanureral.neurostage.ui.theme.NsStatusError
import com.esmanureral.neurostage.ui.theme.NsViolet500
import com.esmanureral.neurostage.ui.theme.NsWhite
import com.esmanureral.neurostage.ui.theme.ScanDimens
import com.esmanureral.neurostage.xai.GeminiReport
import com.esmanureral.neurostage.xai.GradCamResult
import com.esmanureral.neurostage.xai.XaiUiState
import com.esmanureral.neurostage.xai.parseAiReportBlocks

@Composable
fun DoctorXaiResultsSection(
    xaiState: XaiUiState,
    bitmap: Bitmap?,
    stageLabel: String,
    confidencePct: Int,
    selectedStageIndex: Int,
    classScores: List<Float>,
    modifier: Modifier = Modifier,
    patientAge: Int? = null,
    patientGender: String? = null,
    onRequestAiReport: () -> Unit,
    onImageClick: (() -> Unit)? = null,
) {
    var showAiSheet by remember { mutableStateOf(false) }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        DoctorResultHeroCard(
            bitmap = bitmap,
            stageLabel = stageLabel,
            confidencePct = confidencePct,
            patientAge = patientAge,
            patientGender = patientGender,
            isAiLoading = xaiState.isGeminiLoading,
            hasAiReport = xaiState.geminiReport != null,
            onAskAi = {
                showAiSheet = true
                if (xaiState.geminiReport == null && !xaiState.isGeminiLoading) {
                    onRequestAiReport()
                }
            },
            onImageClick = onImageClick,
        )

        PatientProbabilitySection(
            scores = classScores,
            selectedStageIndex = selectedStageIndex,
            useDoctorTheme = true,
        )

        if (xaiState.isGradCamLoading) {
            GradCamLoadingCard()
        }

        xaiState.gradCamError?.let {
            ErrorBanner(stringResource(R.string.home_screen_gradcam_error, it))
        }

        xaiState.gradCamResult?.let { gradCam ->
            DoctorGradCamCard(gradCam = gradCam)
        }
    }

    if (showAiSheet) {
        DoctorGroqAnalysisBottomSheet(
            xaiState = xaiState,
            stageLabel = stageLabel,
            patientAge = patientAge,
            patientGender = patientGender,
            onDismiss = { showAiSheet = false },
            onRequestReport = onRequestAiReport,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DoctorGroqAnalysisBottomSheet(
    xaiState: XaiUiState,
    stageLabel: String,
    patientAge: Int?,
    patientGender: String?,
    onDismiss: () -> Unit,
    onRequestReport: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val knownHeadings = stringArrayResource(R.array.xai_report_headings).toList()
    val summaryKeyword = stringResource(R.string.home_screen_xai_summary_keyword)
    val markdownBold = stringResource(R.string.patient_history_markdown_bold)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = NsSlate50,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 10.dp)
                    .width(40.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(99.dp))
                    .background(NsGray400.copy(alpha = 0.35f)),
            )
        },
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight(0.92f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .navigationBarsPadding()
                .padding(bottom = 28.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.doctor_ai_sheet_title),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = NsGray900,
                    )
                    Text(
                        text = stageLabel,
                        style = MaterialTheme.typography.bodyMedium,
                        color = NeurostageBrandBlue,
                        fontWeight = FontWeight.SemiBold,
                    )
                    PatientMetaChips(
                        age = patientAge,
                        gender = patientGender,
                        modifier = Modifier.padding(top = 6.dp),
                    )
                }
                IconButton(onClick = onDismiss) {
                    Icon(
                        Icons.Outlined.Close,
                        contentDescription = stringResource(R.string.doctor_shell_cd_close),
                        tint = NsGray600,
                    )
                }
            }

            if (xaiState.isGeminiLoading) {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = NsWhite),
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(32.dp),
                            strokeWidth = 2.5.dp,
                            color = NeurostageBrandBlue,
                        )
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = stringResource(R.string.doctor_ai_generating),
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = NsGray900,
                            )
                            Text(
                                text = stringResource(R.string.home_screen_xai_loading_subtitle),
                                style = MaterialTheme.typography.bodySmall,
                                color = NsGray600,
                                lineHeight = 18.sp,
                            )
                        }
                    }
                }
            }

            xaiState.geminiReport?.let { report ->
                DoctorGroqReportContent(
                    report = report,
                    knownHeadings = knownHeadings,
                    summaryKeyword = summaryKeyword,
                    markdownBold = markdownBold,
                    patientAge = patientAge,
                    patientGender = patientGender,
                )
            }

            xaiState.geminiError?.let {
                ErrorBanner(stringResource(R.string.home_screen_gemini_error, it))
            }

            if (!xaiState.isGeminiLoading) {
                OutlinedButton(
                    onClick = onRequestReport,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                ) {
                    Icon(
                        Icons.Outlined.Refresh,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.doctor_ai_refresh),
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }

            Text(
                text = stringResource(R.string.home_screen_result_disclaimer),
                style = MaterialTheme.typography.bodySmall,
                color = NsGray400,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun PatientMetaChips(
    age: Int?,
    gender: String?,
    modifier: Modifier = Modifier,
) {
    if (age == null && gender.isNullOrBlank()) return
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        age?.let {
            MetaChip(
                text = stringResource(R.string.doctor_patient_age_chip, it),
                background = NeurostageBrandBlue.copy(alpha = 0.12f),
                foreground = NeurostageBrandBlue,
            )
        }
        gender?.takeIf { it.isNotBlank() }?.let {
            MetaChip(
                text = stringResource(R.string.doctor_patient_gender_chip, it),
                background = NsViolet500.copy(alpha = 0.12f),
                foreground = NsViolet500,
            )
        }
    }
}

@Composable
private fun MetaChip(
    text: String,
    background: Color,
    foreground: Color,
) {
    Text(
        text = text,
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(background)
            .padding(horizontal = 10.dp, vertical = 5.dp),
        style = MaterialTheme.typography.labelMedium,
        fontWeight = FontWeight.Bold,
        color = foreground,
    )
}

@Composable
private fun ColoredGradCamExplanation(modifier: Modifier = Modifier) {
    val text = stringResource(R.string.gradcam_doctor_explanation)
    val redWord = "Kırmızı"
    val yellowWord = "sarı"
    Text(
        text = buildAnnotatedString {
            var index = 0
            while (index < text.length) {
                when {
                    text.startsWith(redWord, index, ignoreCase = true) -> {
                        withStyle(SpanStyle(color = Color(0xFFD32F2F), fontWeight = FontWeight.Bold)) {
                            append(redWord)
                        }
                        index += redWord.length
                    }
                    text.startsWith(yellowWord, index, ignoreCase = true) -> {
                        withStyle(SpanStyle(color = Color(0xFFF9A825), fontWeight = FontWeight.Bold)) {
                            append(yellowWord)
                        }
                        index += yellowWord.length
                    }
                    else -> {
                        append(text[index])
                        index++
                    }
                }
            }
        },
        modifier = modifier,
        style = MaterialTheme.typography.bodySmall,
        color = NsGray700,
        lineHeight = 21.sp,
    )
}

@Composable
private fun DoctorGroqReportContent(
    report: GeminiReport,
    knownHeadings: List<String>,
    summaryKeyword: String,
    markdownBold: String,
    patientAge: Int?,
    patientGender: String?,
) {
    val aiBlocks = parseAiReportBlocks(report.text, knownHeadings)
    val blocksToShow = when {
        aiBlocks.size >= 2 -> aiBlocks
        aiBlocks.size == 1 -> aiBlocks
        else -> listOf(null to report.text)
    }

    for ((index, block) in blocksToShow.withIndex()) {
        val (title, content) = block
        val cleanContent = content
            .replace(markdownBold, "")
            .replace("**", "")
            .replace(Regex("(?m)^- "), "• ")
            .replace(Regex("(?m)^\\* "), "• ")
            .trim()
        if (cleanContent.isBlank()) continue

        val isSummary = title?.contains(summaryKeyword, ignoreCase = true) == true ||
            title.equals("Klinik Özet", ignoreCase = true) ||
            (title == null && index == 0 && blocksToShow.size == 1)

        key(title ?: index) {
            DoctorAiReportSectionCard(
                title = title,
                cleanContent = cleanContent,
                isSummary = isSummary,
                patientAge = if (isSummary) patientAge else null,
                patientGender = if (isSummary) patientGender else null,
            )
        }
    }
}

@Composable
private fun DoctorAiReportSectionCard(
    title: String?,
    cleanContent: String,
    isSummary: Boolean,
    patientAge: Int? = null,
    patientGender: String? = null,
) {
    if (isSummary) {
        DoctorClinicalSummaryCard(
            title = title ?: stringResource(R.string.home_screen_xai_summary_fallback),
            content = cleanContent,
            patientAge = patientAge,
            patientGender = patientGender,
        )
    } else {
        DoctorReportSectionCard(
            title = title ?: stringResource(R.string.home_screen_xai_summary_fallback),
            content = cleanContent,
        )
    }
}

@Composable
private fun DoctorClinicalSummaryCard(
    title: String,
    content: String,
    patientAge: Int?,
    patientGender: String?,
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = NsWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(4.dp)
                    .background(NeurostageBrandBlue),
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = NsGray900,
                )
                if (patientAge != null || !patientGender.isNullOrBlank()) {
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        patientAge?.let { age ->
                            SummaryMetaChip(
                                text = stringResource(R.string.doctor_patient_age_chip, age),
                                background = NsChipIndigoBg,
                                foreground = NsChipIndigoFg,
                            )
                        }
                        patientGender?.takeIf { it.isNotBlank() }?.let { gender ->
                            SummaryMetaChip(
                                text = stringResource(R.string.doctor_patient_gender_chip, gender),
                                background = NsChipGreenBg,
                                foreground = NsChipGreenFg,
                            )
                        }
                    }
                }
                Text(
                    text = content,
                    style = MaterialTheme.typography.bodyMedium,
                    color = NsGray700,
                    lineHeight = 24.sp,
                )
            }
        }
    }
}

@Composable
private fun SummaryMetaChip(
    text: String,
    background: Color,
    foreground: Color,
) {
    Text(
        text = text,
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(background)
            .padding(horizontal = 8.dp, vertical = 3.dp),
        style = MaterialTheme.typography.labelSmall,
        fontWeight = FontWeight.SemiBold,
        color = foreground,
    )
}

@Composable
private fun DoctorReportSectionCard(
    title: String,
    content: String,
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = NsWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = title.uppercase(),
                style = MaterialTheme.typography.labelLarge,
                color = NsIndigo500,
                fontWeight = FontWeight.Bold,
                letterSpacing = ScanDimens.reportHeadingLetterSpacing,
            )
            Text(
                text = content,
                style = MaterialTheme.typography.bodyMedium,
                color = NsGray800,
                lineHeight = 24.sp,
            )
        }
    }
}

@Composable
private fun DoctorResultHeroCard(
    bitmap: Bitmap?,
    stageLabel: String,
    confidencePct: Int,
    patientAge: Int?,
    patientGender: String?,
    isAiLoading: Boolean,
    hasAiReport: Boolean,
    onAskAi: () -> Unit,
    onImageClick: (() -> Unit)?,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = NsWhite,
        shadowElevation = 2.dp,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, NsDoctorAvatarSoftBg, RoundedCornerShape(20.dp))
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalAlignment = Alignment.Top,
            ) {
                DoctorMriThumbnail(bitmap = bitmap, onClick = onImageClick)

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Text(
                        text = stringResource(R.string.doctor_scan_result_badge),
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(NeurostageBrandBlue)
                            .padding(horizontal = 10.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = NsWhite,
                    )
                    Text(
                        text = stageLabel,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = NsGray900,
                        lineHeight = 26.sp,
                    )
                    PatientMetaChips(age = patientAge, gender = patientGender)
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = stringResource(R.string.home_screen_result_confidence, confidencePct),
                                style = MaterialTheme.typography.bodySmall,
                                color = NsGray600,
                            )
                            Text(
                                text = "$confidencePct%",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = NeurostageBrandBlue,
                            )
                        }
                        LinearProgressIndicator(
                            progress = { confidencePct / 100f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(7.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = NeurostageBrandBlue,
                            trackColor = NsDoctorScaffoldBg,
                        )
                    }
                }
            }

            Button(
                onClick = onAskAi,
                modifier = Modifier.fillMaxWidth(),
                enabled = !isAiLoading,
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = NeurostageBrandBlue,
                    disabledContainerColor = NeurostageBrandBlue.copy(alpha = 0.5f),
                ),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 13.dp),
            ) {
                if (isAiLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = NsWhite,
                    )
                    Spacer(Modifier.width(10.dp))
                    Text(
                        text = stringResource(R.string.doctor_ai_generating),
                        fontWeight = FontWeight.SemiBold,
                        color = NsWhite,
                    )
                } else {
                    Icon(
                        Icons.Outlined.AutoAwesome,
                        contentDescription = null,
                        tint = NsWhite,
                        modifier = Modifier.size(20.dp),
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = if (hasAiReport) {
                            stringResource(R.string.doctor_ai_view_report)
                        } else {
                            stringResource(R.string.home_screen_ask_ai)
                        },
                        fontWeight = FontWeight.Bold,
                        color = NsWhite,
                    )
                }
            }
        }
    }
}

@Composable
private fun DoctorMriThumbnail(bitmap: Bitmap?, onClick: (() -> Unit)?) {
    val mod = Modifier
        .size(108.dp)
        .clip(RoundedCornerShape(14.dp))
        .background(NsDoctorScaffoldBg)
        .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)

    Box(modifier = mod, contentAlignment = Alignment.Center) {
        if (bitmap != null) {
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = stringResource(R.string.home_screen_result_mri_label),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(6.dp),
                contentScale = ContentScale.Fit,
            )
        }
        if (onClick != null) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(6.dp)
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.55f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    Icons.Outlined.ZoomIn,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(16.dp),
                )
            }
        }
    }
}

@Composable
private fun GradCamLoadingCard() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(NsDoctorAvatarSoftBg)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(26.dp),
            strokeWidth = 2.5.dp,
            color = NeurostageBrandBlue,
        )
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = stringResource(R.string.home_screen_gradcam_title),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = NeurostageBrandBlue,
            )
            Text(
                text = stringResource(R.string.doctor_gradcam_loading_hint),
                style = MaterialTheme.typography.bodySmall,
                color = NsGray600,
            )
        }
    }
}

@Composable
private fun DoctorGradCamCard(gradCam: GradCamResult) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = NsWhite,
        shadowElevation = 1.dp,
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(NsDoctorAvatarSoftBg),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        Icons.Outlined.Visibility,
                        contentDescription = null,
                        tint = NeurostageBrandBlue,
                        modifier = Modifier.size(20.dp),
                    )
                }
                Text(
                    text = stringResource(R.string.home_screen_gradcam_title),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = NsGray900,
                )
            }

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = NsDoctorAvatarSoftBg,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    gradCam.hfPredictedStageLabel?.takeIf { it.isNotBlank() }?.let { region ->
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = stringResource(R.string.doctor_gradcam_focus_label),
                                style = MaterialTheme.typography.labelSmall,
                                color = NsGray600,
                                fontWeight = FontWeight.SemiBold,
                            )
                            Text(
                                text = region,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFE65100),
                            )
                        }
                    }
                    ColoredGradCamExplanation()
                }
            }

            Image(
                bitmap = gradCam.heatmapBitmap.asImageBitmap(),
                contentDescription = stringResource(R.string.home_screen_gradcam_title),
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(14.dp))
                    .border(1.dp, NsDoctorAvatarSoftBg, RoundedCornerShape(14.dp)),
                contentScale = ContentScale.Fit,
            )
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
            .clip(RoundedCornerShape(12.dp))
            .background(NsRose50)
            .padding(12.dp),
    )
}
