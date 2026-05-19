package com.esmanureral.neurostage.ui

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.esmanureral.neurostage.AnalysisState
import com.esmanureral.neurostage.AnalysisViewModel
import com.esmanureral.neurostage.domain.patient.PatientStage
import com.esmanureral.neurostage.ui.patient.PatientMriResultContent
import com.esmanureral.neurostage.ui.patient.PatientResultPageBackground
import com.esmanureral.neurostage.ui.theme.*
import com.esmanureral.neurostage.ui.doctor.DoctorXaiResultsSection
import com.esmanureral.neurostage.xai.XaiUiState
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.stringArrayResource
import com.esmanureral.neurostage.R
import androidx.compose.ui.geometry.Size as GeometrySize
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val neurostageBrandBlue = NeurostageBrandBlue

private fun waveBottomShape(waveDepth: Dp): Shape = object : Shape {
    override fun createOutline(
        size: GeometrySize,
        layoutDirection: LayoutDirection,
        density: Density,
    ): Outline {
        val waveDepthPx = with(density) { waveDepth.toPx() }
        val path = Path().apply {
            moveTo(0f, 0f)
            lineTo(size.width, 0f)
            lineTo(size.width, size.height - waveDepthPx)
            quadraticTo(size.width / 2f, size.height + waveDepthPx, 0f, size.height - waveDepthPx)
            close()
        }
        return Outline.Generic(path)
    }
}

private enum class Step { UPLOAD, PREVIEW, ANALYZE, RESULT }

private data class StageTone(val label: String, val dot: Color, val bg: Color, val scale: Int)

@Composable
private fun stageTone(i: Int, forPatient: Boolean = false): StageTone {
    val labels = stringArrayResource(R.array.dementia_stage_labels)
    if (forPatient) {
        return when (i) {
            PatientStage.MILD_DEMENTIA -> StageTone(
                labels[PatientStage.MILD_DEMENTIA],
                PatientResultStageColors.Mild,
                PatientResultStageColors.MildBg,
                2,
            )
            PatientStage.MODERATE_DEMENTIA -> StageTone(
                labels[PatientStage.MODERATE_DEMENTIA],
                PatientResultStageColors.Moderate,
                PatientResultStageColors.ModerateBg,
                3,
            )
            PatientStage.HEALTHY -> StageTone(
                labels[PatientStage.HEALTHY],
                PatientResultStageColors.Healthy,
                PatientResultStageColors.HealthyBg,
                0,
            )
            PatientStage.VERY_MILD_DEMENTIA -> StageTone(
                labels[PatientStage.VERY_MILD_DEMENTIA],
                PatientResultStageColors.VeryMild,
                PatientResultStageColors.VeryMildBg,
                1,
            )
            else -> StageTone(
                labels.getOrElse(0) { "" },
                PatientResultStageColors.Moderate,
                PatientResultStageColors.ModerateBg,
                4,
            )
        }
    }
    return when (i) {
        PatientStage.MILD_DEMENTIA -> StageTone(
            labels[PatientStage.MILD_DEMENTIA],
            StageColors.Mild,
            StageColors.MildBg,
            2,
        )
        PatientStage.MODERATE_DEMENTIA -> StageTone(
            labels[PatientStage.MODERATE_DEMENTIA],
            StageColors.Moderate,
            StageColors.ModerateBg,
            3,
        )
        PatientStage.HEALTHY -> StageTone(
            labels[PatientStage.HEALTHY],
            StageColors.Healthy,
            StageColors.HealthyBg,
            0,
        )
        PatientStage.VERY_MILD_DEMENTIA -> StageTone(
            labels[PatientStage.VERY_MILD_DEMENTIA],
            StageColors.VeryMild,
            StageColors.VeryMildBg,
            1,
        )
        else -> StageTone(
            labels.getOrElse(0) { "" },
            StageColors.Severe,
            StageColors.SevereBg,
            4,
        )
    }
}

@Composable
fun MainScreen(
    viewModel: AnalysisViewModel = hiltViewModel(),
    patientId: String? = null,
    isPatient: Boolean = false,
    returnToHub: Boolean = false,
    stageBeforeScan: Int? = null,
    onHubUnchangedResult: ((stageIndex: Int, confidence: Float, scores: List<Float>) -> Unit)? = null,
    onBack: (() -> Unit)? = null,
    onOpenGames: (() -> Unit)? = null,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val validationError by viewModel.validationError.collectAsStateWithLifecycle()
    val isMriValidated by viewModel.isMriValidated.collectAsStateWithLifecycle()
    val saveError by viewModel.saveError.collectAsStateWithLifecycle()
    val xaiState by viewModel.xaiState.collectAsStateWithLifecycle()
    val activePatient by viewModel.activePatient.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val bitmapState = remember { mutableStateOf<Bitmap?>(null) }

    LaunchedEffect(patientId) {
        viewModel.setActivePatient(patientId)
    }

    val successState = state as? AnalysisState.Success
    var exitingForHubUnchanged by remember { mutableStateOf(false) }
    val onHubUnchangedResultState = rememberUpdatedState(onHubUnchangedResult)

    DisposableEffect(returnToHub, stageBeforeScan) {
        viewModel.setHubUnchangedContext(
            stageBeforeScan = if (returnToHub) stageBeforeScan else null,
            handler = if (returnToHub) {
                { result ->
                    exitingForHubUnchanged = true
                    bitmapState.value = null
                    onHubUnchangedResultState.value?.invoke(
                        result.stageIndex,
                        result.confidence,
                        result.scores,
                    )
                }
            } else {
                null
            },
        )
        onDispose {
            viewModel.setHubUnchangedContext(stageBeforeScan = null, handler = null)
        }
    }

    val step = when {
        successState != null -> Step.RESULT
        state is AnalysisState.Loading -> Step.ANALYZE
        bitmapState.value != null -> Step.PREVIEW
        else -> Step.UPLOAD
    }

    val gallery = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            val bmp = context.contentResolver.openInputStream(it)
                ?.use { s -> BitmapFactory.decodeStream(s) }
            if (bmp != null) {
                bitmapState.value = bmp; viewModel.validateAndSetBitmap(bmp)
            }
        }
    }

    BackHandler {
        when (step) {
            Step.ANALYZE -> {
                viewModel.reset()
                bitmapState.value = null
            }
            Step.PREVIEW -> {
                bitmapState.value = null
                viewModel.reset()
            }
            Step.RESULT, Step.UPLOAD -> onBack?.invoke()
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = NsDoctorScaffoldBg,
    ) {
        if (exitingForHubUnchanged) {
            Box(Modifier.fillMaxSize())
            return@Surface
        }

        when (step) {
            Step.UPLOAD -> UploadStep(
                error = validationError,
                onBack = onBack,
                onPickGallery = { gallery.launch("image/*") },
            )

            Step.PREVIEW -> PreviewStep(
                bitmap = bitmapState.value,
                validated = isMriValidated,
                error = validationError,
                onRepick = { gallery.launch("image/*") },
                onAnalyze = { bitmapState.value?.let(viewModel::analyze) },
                onReset = { bitmapState.value = null; viewModel.reset() }
            )

            Step.ANALYZE -> AnalyzeStep(bitmap = bitmapState.value)
            Step.RESULT -> ResultStep(
                result = state as AnalysisState.Success,
                xaiState = xaiState,
                saveError = saveError,
                isPatient = isPatient,
                patientAge = activePatient?.age,
                patientGender = activePatient?.gender,
                onBack = onBack,
                onNewScan = { bitmapState.value = null; viewModel.reset() },
                onOpenGames = onOpenGames,
                onRequestAiReport = viewModel::requestGeminiReport,
            )

        }
    }
}

@Composable
private fun StageScoreBreakdown(
    scores: List<Float>,
    selectedStageIndex: Int,
) {
    val classLabels = stringArrayResource(R.array.dementia_stage_labels)
    val allClasses = listOf(
        Triple(0, classLabels[0], StageColors.Mild),
        Triple(1, classLabels[1], StageColors.Moderate),
        Triple(2, classLabels[2], StageColors.Healthy),
        Triple(3, classLabels[3], StageColors.VeryMild),
    ).sortedByDescending { scores.getOrElse(it.first) { 0f } }

    Column(verticalArrangement = Arrangement.spacedBy(ScanDimens.resultScoresGap)) {
        allClasses.forEach { (idx, label, color) ->
            val scorePct = (scores.getOrElse(idx) { 0f } * 100).toInt()
            val barAnim by animateFloatAsState(
                targetValue = scorePct / 100f,
                animationSpec = tween(700, easing = FastOutSlowInEasing),
                label = "unchangedBar$idx",
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(ScanDimens.resultScoreRowGap),
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (idx == selectedStageIndex) NsNavy else NsSlate,
                    fontWeight = if (idx == selectedStageIndex) FontWeight.Bold else FontWeight.Normal,
                    modifier = Modifier.width(ScanDimens.resultScoreLabelWidth),
                )
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(ScanDimens.resultScoreBarHeight)
                        .clip(RoundedCornerShape(ScanDimens.resultScoreBarCorner))
                        .background(ScanColors.scoreBarTrack),
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(barAnim)
                            .clip(RoundedCornerShape(ScanDimens.resultScoreBarCorner))
                            .background(color),
                    )
                }
                Text(
                    text = "$scorePct%",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = NsNavy,
                    modifier = Modifier.width(ScanDimens.resultScorePercentWidth),
                    textAlign = TextAlign.End,
                )
            }
        }
    }
}

@Composable
private fun UploadStep(
    error: String?,
    onBack: (() -> Unit)?,
    onPickGallery: () -> Unit,
) {
    val inf = rememberInfiniteTransition(label = "pulse")
    val pulse by inf.animateFloat(
        initialValue = 0.85f, targetValue = 1f,
        animationSpec = infiniteRepeatable(
            tween(1800, easing = FastOutSlowInEasing),
            RepeatMode.Reverse
        ),
        label = "pf"
    )
    val waveDepth = ScanDimens.waveDepth
    val uploadCorner = ScanDimens.uploadCardCorner

    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(waveBottomShape(waveDepth))
                .background(neurostageBrandBlue)
                .padding(
                    horizontal = ScanDimens.headerHorizontalPadding,
                    vertical = ScanDimens.headerVerticalPadding,
                ),
        ) {
            Column {
                if (onBack != null) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.offset(x = -ScanDimens.backButtonOffset),
                    ) {
                        Icon(Icons.AutoMirrored.Outlined.ArrowBack, null, tint = NsWhite)
                    }
                }
                Text(
                    stringResource(R.string.home_screen_upload_title),
                    style = MaterialTheme.typography.headlineSmall,
                    color = NsWhite,
                    fontWeight = FontWeight.ExtraBold,
                )
                Spacer(Modifier.height(ScanDimens.headerTitleGap))
                Text(
                    stringResource(R.string.home_screen_upload_subtitle),
                    style = MaterialTheme.typography.bodyMedium,
                    color = NsWhite.copy(alpha = 0.85f),
                    lineHeight = 22.sp,
                )
                Spacer(Modifier.height(ScanDimens.headerContentGap))
            }
        }

        Spacer(Modifier.height(ScanDimens.sectionGapL))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = ScanDimens.contentHorizontalPadding)
                .clip(RoundedCornerShape(uploadCorner))
                .background(NsWhite)
                .border(
                    width = ScanDimens.uploadBorderWidth * pulse,
                    color = neurostageBrandBlue.copy(alpha = 0.3f * pulse),
                    shape = RoundedCornerShape(uploadCorner),
                )
                .clickable(onClick = onPickGallery),
            contentAlignment = Alignment.Center,
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(ScanDimens.uploadZoneGap),
            ) {
                Box(
                    modifier = Modifier
                        .size(ScanDimens.uploadIconCircle)
                        .clip(CircleShape)
                        .background(NsChipIndigoBg),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Outlined.FileUpload, null,
                        tint = neurostageBrandBlue,
                        modifier = Modifier.size(ScanDimens.uploadIconSize),
                    )
                }
                Text(
                    stringResource(R.string.home_screen_pick_image),
                    style = MaterialTheme.typography.titleMedium,
                    color = NsGray900,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    "JPG · PNG",
                    style = MaterialTheme.typography.bodySmall,
                    color = NsGray600,
                )
            }
        }

        AnimatedVisibility(
            visible = error != null,
            enter = fadeIn() + slideInVertically(),
        ) {
            error?.let {
                Text(
                    it,
                    color = NsStatusError,
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(
                        horizontal = ScanDimens.errorHorizontalPadding,
                        vertical = ScanDimens.errorVerticalPadding,
                    ),
                )
            }
        }

        Spacer(Modifier.height(ScanDimens.pickerSectionGap))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = ScanDimens.contentHorizontalPadding)
                .clip(RoundedCornerShape(ScanDimens.pickerCorner))
                .background(neurostageBrandBlue)
                .clickable(onClick = onPickGallery)
                .padding(vertical = ScanDimens.pickerButtonVerticalPadding),
            contentAlignment = Alignment.Center,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(ScanDimens.pickerIconGap),
            ) {
                Icon(
                    Icons.Outlined.PhotoLibrary,
                    contentDescription = null,
                    tint = NsWhite,
                    modifier = Modifier.size(ScanDimens.pickerIconSize),
                )
                Text(
                    stringResource(R.string.home_screen_pick_gallery),
                    style = MaterialTheme.typography.titleMedium,
                    color = NsWhite,
                    fontWeight = FontWeight.Bold,
                )
            }
        }

        Text(
            stringResource(R.string.home_screen_disclaimer),
            style = MaterialTheme.typography.bodySmall,
            color = NsGray600,
            textAlign = TextAlign.Center,
            lineHeight = 20.sp,
            modifier = Modifier.padding(
                start = ScanDimens.disclaimerHorizontal,
                end = ScanDimens.disclaimerHorizontal,
                top = ScanDimens.disclaimerTop,
                bottom = ScanDimens.disclaimerBottom,
            ),
        )
    }
}

@Composable
private fun PreviewStep(
    bitmap: Bitmap?,
    validated: Boolean,
    error: String?,
    onRepick: () -> Unit,
    onAnalyze: () -> Unit,
    onReset: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = ScanDimens.toolbarHorizontalPadding,
                    vertical = ScanDimens.toolbarVerticalPadding,
                ),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onReset) {
                Icon(Icons.AutoMirrored.Outlined.ArrowBack, null, tint = neurostageBrandBlue)
            }
            Text(
                stringResource(R.string.home_screen_preview_title),
                style = MaterialTheme.typography.titleMedium,
                color = NsGray900,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier
                    .weight(1f)
                    .padding(start = ScanDimens.toolbarTitleStart),
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(
                    horizontal = ScanDimens.previewImageHorizontalPadding,
                    vertical = ScanDimens.previewImageVerticalPadding,
                )
                .clip(RoundedCornerShape(ScanDimens.previewImageCorner)),
            contentAlignment = Alignment.Center,
        ) {
            bitmap?.let {
                androidx.compose.foundation.Image(
                    bitmap = it.asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(ScanDimens.previewImageInnerCorner)),
                    contentScale = ContentScale.Fit,
                )
            }
        }

        error?.let {
            Text(
                it,
                color = NsStatusError,
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = ScanDimens.previewErrorHorizontalPadding,
                        vertical = ScanDimens.previewErrorVerticalPadding,
                    ),
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(
                    RoundedCornerShape(
                        topStart = ScanDimens.sheetTopCorner,
                        topEnd = ScanDimens.sheetTopCorner,
                    ),
                )
                .background(NsWhite)
                .padding(ScanDimens.sheetPadding),
            verticalArrangement = Arrangement.spacedBy(ScanDimens.sheetGap),
        ) {
            Text(
                stringResource(R.string.home_screen_preview_instruction),
                style = MaterialTheme.typography.bodyMedium,
                color = NsGray600,
            )
            Row(horizontalArrangement = Arrangement.spacedBy(ScanDimens.sheetRowGap)) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(ScanDimens.sheetButtonCorner))
                        .background(NsDoctorLoginTrackBg)
                        .clickable(onClick = onRepick)
                        .padding(vertical = ScanDimens.sheetButtonVerticalPadding),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        stringResource(R.string.home_screen_preview_change),
                        color = NsGray900,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(ScanDimens.sheetButtonCorner))
                        .background(if (validated) neurostageBrandBlue else NsGray300)
                        .clickable(enabled = validated, onClick = onAnalyze)
                        .padding(vertical = ScanDimens.sheetButtonVerticalPadding),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        stringResource(R.string.home_screen_preview_analyze),
                        color = NsWhite,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Text(
                stringResource(R.string.home_screen_preview_disclaimer),
                style = MaterialTheme.typography.bodySmall,
                color = NsGray600,
            )
        }
    }
}

@Composable
private fun AnalyzeStep(bitmap: Bitmap?) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .background(NsDoctorScaffoldBg),
        contentAlignment = Alignment.Center,
    ) {
        bitmap?.let {
            MriScanningImage(
                bitmap = it,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = ScanDimens.previewImageHorizontalPadding)
                    .aspectRatio(1f),
            )
        }
    }
}

@Composable
private fun ResultStep(
    result: AnalysisState.Success,
    xaiState: XaiUiState,
    saveError: String?,
    isPatient: Boolean = false,
    patientAge: Int? = null,
    patientGender: String? = null,
    onBack: (() -> Unit)?,
    onNewScan: () -> Unit,
    onOpenGames: (() -> Unit)? = null,
    onRequestAiReport: () -> Unit = {},
) {
    val t = stageTone(result.stageIndex)

    val confidencePct = (result.confidence * 100).toInt()

    if (isPatient) {
        PatientResultStep(
            result = result,
            confidencePct = confidencePct,
            onBack = onBack,
            onNewScan = onNewScan,
            onOpenGames = onOpenGames,
        )
        return
    }

    var showFullscreenImage by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NsDoctorScaffoldBg)
            .systemBarsPadding()
            .verticalScroll(rememberScrollState()),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (onBack != null) {
                IconButton(onClick = onBack) {
                    Icon(
                        Icons.AutoMirrored.Outlined.ArrowBack,
                        contentDescription = null,
                        tint = NeurostageBrandBlue,
                    )
                }
            } else {
                Spacer(Modifier.width(48.dp))
            }
            Text(
                text = stringResource(R.string.home_screen_result_title),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = NsGray900,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.width(48.dp))
        }

        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            DoctorXaiResultsSection(
                xaiState = xaiState,
                bitmap = result.bitmap,
                stageLabel = t.label,
                confidencePct = confidencePct,
                selectedStageIndex = result.stageIndex,
                classScores = result.allScores,
                patientAge = patientAge,
                patientGender = patientGender,
                onRequestAiReport = onRequestAiReport,
                onImageClick = result.bitmap?.let { { showFullscreenImage = true } },
            )

            result.bitmap?.let { bmp ->
                if (showFullscreenImage) {
                    FullscreenMriDialog(
                        bitmap = bmp,
                        onDismiss = { showFullscreenImage = false },
                    )
                }
            }

            saveError?.let { err ->
                Text(
                    err,
                    style = MaterialTheme.typography.bodySmall,
                    color = NsStatusError,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(ScanDimens.errorBannerCorner))
                        .background(NsRose50)
                        .padding(ScanDimens.errorBannerPadding),
                )
            }

            Button(
                onClick = onNewScan,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(ScanDimens.primaryButtonCorner),
                colors = ButtonDefaults.buttonColors(
                    containerColor = neurostageBrandBlue,
                ),
                contentPadding = PaddingValues(vertical = ScanDimens.primaryButtonVerticalPadding),
            ) {
                Text(
                    stringResource(R.string.home_screen_new_scan),
                    color = NsWhite,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium
                )
            }

            Text(
                stringResource(R.string.home_screen_app_disclaimer),
                style = MaterialTheme.typography.bodySmall,
                color = NsGray400,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = ScanDimens.footerBottom),
            )
        }
    }
}


@Composable
private fun PatientResultStep(
    result: AnalysisState.Success,
    confidencePct: Int,
    onBack: (() -> Unit)?,
    onNewScan: () -> Unit,
    onOpenGames: (() -> Unit)?,
) {
    var showFullscreenImage by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PatientResultPageBackground())
            .systemBarsPadding()
            .verticalScroll(rememberScrollState()),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (onBack != null) {
                IconButton(onClick = onBack) {
                    Icon(
                        Icons.AutoMirrored.Outlined.ArrowBack,
                        contentDescription = null,
                        tint = PatientResultColors.primary,
                    )
                }
            } else {
                Spacer(Modifier.width(48.dp))
            }
            Text(
                text = stringResource(R.string.patient_scan_result_screen_title),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = PatientColors.textPrimary,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.width(48.dp))
        }

        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .padding(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            PatientMriResultContent(
                bitmap = result.bitmap,
                stageIndex = result.stageIndex,
                confidencePercent = confidencePct,
                scores = result.allScores,
                onOpenGames = onOpenGames,
                onImageClick = result.bitmap?.let { { showFullscreenImage = true } },
            )

            OutlinedButton(
                onClick = onNewScan,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(PatientResultDimens.buttonCorner),
                border = BorderStroke(1.dp, PatientResultColors.primary.copy(alpha = 0.35f)),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = PatientResultColors.primary,
                ),
                contentPadding = PaddingValues(vertical = 12.dp),
            ) {
                Text(
                    stringResource(R.string.home_screen_new_scan),
                    fontWeight = FontWeight.SemiBold,
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.Top,
            ) {
                Icon(
                    imageVector = Icons.Outlined.Shield,
                    contentDescription = null,
                    tint = PatientResultColors.disclaimerMuted,
                    modifier = Modifier.size(14.dp),
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    text = stringResource(R.string.home_screen_app_disclaimer),
                    style = MaterialTheme.typography.labelSmall,
                    color = PatientResultColors.disclaimerMuted,
                    textAlign = TextAlign.Center,
                    lineHeight = 16.sp,
                )
            }
        }
    }

    result.bitmap?.let { bmp ->
        if (showFullscreenImage) {
            FullscreenMriDialog(
                bitmap = bmp,
                onDismiss = { showFullscreenImage = false },
            )
        }
    }
}

@Composable
private fun FullscreenMriDialog(
    bitmap: Bitmap,
    onDismiss: () -> Unit,
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .clickable(onClick = onDismiss),
            contentAlignment = Alignment.Center,
        ) {
            androidx.compose.foundation.Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = stringResource(R.string.home_screen_result_mri_label),
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit,
            )
        }
    }
}

@Composable
private fun MriImageBox(bitmap: Bitmap?, label: String, modifier: Modifier) {
    Box(
        modifier = modifier
            .fillMaxHeight()
            .clip(RoundedCornerShape(ScanDimens.resultCardCorner))
            .background(NsChipIndigoBg),
        contentAlignment = Alignment.BottomStart,
    ) {
        bitmap?.let {
            androidx.compose.foundation.Image(
                bitmap = it.asImageBitmap(),
                contentDescription = label,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit,
            )
        }
        Text(
            label,
            style = MaterialTheme.typography.labelSmall,
            color = NsWhite,
            modifier = Modifier
                .background(ScanColors.mriLabelScrim)
                .padding(
                    horizontal = ScanDimens.mriLabelHorizontalPadding,
                    vertical = ScanDimens.mriLabelVerticalPadding,
                ),
        )
    }
}