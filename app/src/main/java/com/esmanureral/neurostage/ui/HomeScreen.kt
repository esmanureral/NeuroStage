package com.esmanureral.neurostage.ui

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
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
import com.esmanureral.neurostage.ui.patient.PatientScanGuidanceCard
import com.esmanureral.neurostage.ui.theme.*
import com.esmanureral.neurostage.xai.XaiUiState
import com.esmanureral.neurostage.xai.parseAiReportBlocks
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
private fun stageTone(i: Int): StageTone {
    val labels = stringArrayResource(R.array.home_screen_stage_tones)
    return when (i) {
        2 -> StageTone(labels[0], StageColors.Healthy, StageColors.HealthyBg, 0)
        3 -> StageTone(labels[1], StageColors.VeryMild, StageColors.VeryMildBg, 1)
        0 -> StageTone(labels[2], StageColors.Mild, StageColors.MildBg, 2)
        1 -> StageTone(labels[3], StageColors.Moderate, StageColors.ModerateBg, 3)
        else -> StageTone(labels[4], StageColors.Severe, StageColors.SevereBg, 4)
    }
}

@Composable
fun MainScreen(
    viewModel: AnalysisViewModel = hiltViewModel(),
    patientId: String? = null,
    isPatient: Boolean = false,
    onBack: (() -> Unit)? = null,
    onOpenGames: (() -> Unit)? = null,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val analysisSteps by viewModel.analysisProgressSteps.collectAsStateWithLifecycle()
    val validationError by viewModel.validationError.collectAsStateWithLifecycle()
    val isMriValidated by viewModel.isMriValidated.collectAsStateWithLifecycle()
    val saveError by viewModel.saveError.collectAsStateWithLifecycle()
    val xaiState by viewModel.xaiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val bitmapState = remember { mutableStateOf<Bitmap?>(null) }

    LaunchedEffect(patientId) {
        viewModel.setActivePatient(patientId)
    }

    val step = when {
        state is AnalysisState.Success -> Step.RESULT
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

    val camera =
        rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { bmp ->
            if (bmp != null) {
                bitmapState.value = bmp
                viewModel.validateAndSetBitmap(bmp)
            }
        }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = NsDoctorScaffoldBg,
    ) {
        when (step) {
            Step.UPLOAD -> UploadStep(
                error = validationError,
                onBack = onBack,
                onPickGallery = { gallery.launch("image/*") },
                onPickCamera = { camera.launch(null) },
            )

            Step.PREVIEW -> PreviewStep(
                bitmap = bitmapState.value,
                validated = isMriValidated,
                error = validationError,
                onRepick = { gallery.launch("image/*") },
                onAnalyze = { bitmapState.value?.let(viewModel::analyze) },
                onReset = { bitmapState.value = null; viewModel.reset() }
            )

            Step.ANALYZE -> AnalyzeStep(completedSteps = analysisSteps.coerceIn(0, 4))
            Step.RESULT -> ResultStep(
                result = state as AnalysisState.Success,
                xaiState = xaiState,
                saveError = saveError,
                isPatient = isPatient,
                onBack = onBack,
                onNewScan = { bitmapState.value = null; viewModel.reset() },
                onOpenGames = onOpenGames,
            )
        }
    }
}

@Composable
private fun UploadStep(
    error: String?,
    onBack: (() -> Unit)?,
    onPickGallery: () -> Unit,
    onPickCamera: () -> Unit,
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
                    color = NsWhite.copy(alpha = 0.8f),
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

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = ScanDimens.contentHorizontalPadding),
            horizontalArrangement = Arrangement.spacedBy(ScanDimens.pickerRowGap),
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(ScanDimens.pickerCorner))
                    .background(NsWhite)
                    .clickable(onClick = onPickCamera)
                    .padding(vertical = ScanDimens.pickerButtonVerticalPadding),
                contentAlignment = Alignment.Center,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(ScanDimens.pickerIconGap),
                ) {
                    Icon(
                        Icons.Outlined.PhotoCamera,
                        null,
                        tint = neurostageBrandBlue,
                        modifier = Modifier.size(ScanDimens.pickerIconSize),
                    )
                    Text(
                        stringResource(R.string.home_screen_pick_camera),
                        style = MaterialTheme.typography.titleMedium,
                        color = NsGray900,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }
            Box(
                modifier = Modifier
                    .weight(1f)
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
                        null,
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
        }

        Text(
            stringResource(R.string.home_screen_disclaimer),
            style = MaterialTheme.typography.bodySmall,
            color = NsGray600,
            modifier = Modifier.padding(
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
private fun AnalyzeStep(completedSteps: Int) {
    val progress = completedSteps / 4f
    val animProg by animateFloatAsState(
        targetValue = progress.coerceIn(0.05f, 1f),
        animationSpec = tween(500, easing = FastOutSlowInEasing),
        label = "p",
    )
    val inf = rememberInfiniteTransition(label = "a")
    val rot by inf.animateFloat(
        0f, 360f,
        infiniteRepeatable(tween(3000, easing = LinearEasing)),
        label = "r",
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Box(
            modifier = Modifier.size(ScanDimens.progressOuter),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                progress = { animProg },
                modifier = Modifier.size(ScanDimens.progressOuter),
                strokeWidth = ScanDimens.progressStroke,
                color = neurostageBrandBlue,
                trackColor = NsDoctorLoginFieldBorderIdle,
                strokeCap = StrokeCap.Round,
            )
            Box(
                modifier = Modifier
                    .size(ScanDimens.progressInner)
                    .rotate(rot)
                    .border(
                        ScanDimens.progressRingBorder,
                        neurostageBrandBlue.copy(0.2f),
                        CircleShape
                    )
            )
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    "${(animProg * 100).toInt()}%",
                    style = MaterialTheme.typography.headlineLarge,
                    color = NsGray900,
                    fontWeight = FontWeight.ExtraBold,
                )
                Text(
                    stringResource(R.string.home_screen_analyze_calculating),
                    style = MaterialTheme.typography.bodySmall,
                    color = NsGray600
                )
            }
        }

        Spacer(Modifier.height(ScanDimens.analyzeTitleGap))

        Text(
            if (completedSteps < 4) stringResource(R.string.home_screen_analyze_brain_img) else stringResource(
                R.string.home_screen_analyze_calculating
            ),
            style = MaterialTheme.typography.titleMedium,
            color = NsGray900,
            fontWeight = FontWeight.SemiBold,
        )

        Spacer(Modifier.height(ScanDimens.analyzeSubtitleGap))

        val labels = stringArrayResource(R.array.home_screen_analyze_labels).toList()
        Column(
            modifier = Modifier.padding(horizontal = ScanDimens.analyzeLabelsHorizontalPadding),
            verticalArrangement = Arrangement.spacedBy(ScanDimens.analyzeLabelGap),
        ) {
            labels.forEachIndexed { i, label ->
                val done = i < completedSteps
                val active = i == completedSteps && completedSteps < 4
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(ScanDimens.analyzeLabelGap),
                ) {
                    Box(
                        modifier = Modifier
                            .size(ScanDimens.analyzeDotSize)
                            .clip(CircleShape)
                            .background(
                                when {
                                    done -> neurostageBrandBlue; active -> neurostageBrandBlue.copy(
                                    0.5f
                                ); else -> NsGray300
                                }
                            )
                    )
                    Text(
                        label,
                        style = MaterialTheme.typography.bodyMedium,
                        color = when {
                            done || active -> NsGray900; else -> NsGray400
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun ResultStep(
    result: AnalysisState.Success,
    xaiState: XaiUiState,
    saveError: String?,
    isPatient: Boolean = false,
    onBack: (() -> Unit)?,
    onNewScan: () -> Unit,
    onOpenGames: (() -> Unit)? = null,
) {
    val t = stageTone(result.stageIndex)

    val confidencePct = (result.confidence * 100).toInt()

    val waveDepth = ScanDimens.waveDepth

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ScanColors.resultBackground)
            .systemBarsPadding()
            .verticalScroll(rememberScrollState()),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(waveBottomShape(waveDepth))
                .background(neurostageBrandBlue)
                .padding(
                    horizontal = ScanDimens.resultHeaderHorizontalPadding,
                    vertical = ScanDimens.resultHeaderVerticalPadding,
                ),
        ) {
            if (onBack != null) {
                IconButton(onClick = onBack, modifier = Modifier.align(Alignment.CenterStart)) {
                    Icon(Icons.AutoMirrored.Outlined.ArrowBack, null, tint = NsWhite)
                }
            }
            Text(
                stringResource(R.string.home_screen_result_title),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = NsWhite,
                modifier = Modifier.align(Alignment.Center),
            )
        }

        Column(
            modifier = Modifier.padding(
                horizontal = ScanDimens.resultContentHorizontalPadding,
                vertical = ScanDimens.resultContentVerticalPadding,
            ),
            verticalArrangement = Arrangement.spacedBy(ScanDimens.resultBlockGap),
        ) {
            var showFullscreenImage by remember { mutableStateOf(false) }

            result.bitmap?.let { bmp ->
                MriImageBox(
                    bitmap = bmp,
                    label = stringResource(R.string.home_screen_result_mri_label),
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                        .clickable { showFullscreenImage = true }
                )

                if (showFullscreenImage) {
                    Dialog(
                        onDismissRequest = { showFullscreenImage = false },
                        properties = DialogProperties(usePlatformDefaultWidth = false)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black)
                                .clickable { showFullscreenImage = false },
                            contentAlignment = Alignment.Center
                        ) {
                            androidx.compose.foundation.Image(
                                bitmap = bmp.asImageBitmap(),
                                contentDescription = "Fullscreen MRI",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Fit
                            )
                        }
                    }
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(ScanDimens.resultCardCorner))
                    .background(NsWhite)
                    .padding(
                        horizontal = ScanDimens.resultCardPaddingHorizontal,
                        vertical = ScanDimens.resultCardPaddingVertical,
                    ),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        modifier = Modifier
                            .size(ScanDimens.resultStageDot)
                            .clip(CircleShape)
                            .background(t.dot),
                    )
                    Spacer(Modifier.width(ScanDimens.resultStageDotGap))
                    Text(
                        t.label,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = NsNavy,
                        modifier = Modifier.weight(1f),
                    )
                    Text(
                        String.format(
                            stringResource(R.string.home_screen_result_confidence),
                            confidencePct
                        ),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = t.dot,
                    )
                }
            }
            val scores = result.allScores
            val classLabels = stringArrayResource(R.array.home_screen_class_labels)
            val allClasses = listOf(
                Triple(0, classLabels[0], StageColors.Mild),
                Triple(1, classLabels[1], StageColors.Moderate),
                Triple(2, classLabels[2], StageColors.Healthy),
                Triple(3, classLabels[3], StageColors.VeryMild),
            ).sortedByDescending { scores.getOrElse(it.first) { 0f } }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(ScanDimens.resultCardCorner))
                    .background(NsWhite)
                    .padding(ScanDimens.resultScoresPadding),
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(ScanDimens.resultScoresGap)) {
                    allClasses.forEach { (idx, label, color) ->
                        val scorePct = (scores.getOrElse(idx) { 0f } * 100).toInt()
                        val barAnim by animateFloatAsState(
                            targetValue = scorePct / 100f,
                            animationSpec = tween(700, easing = FastOutSlowInEasing),
                            label = "bar$idx",
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(ScanDimens.resultScoreRowGap),
                        ) {
                            Text(
                                label,
                                style = MaterialTheme.typography.bodySmall,
                                color = if (idx == result.stageIndex) NsNavy else NsSlate,
                                fontWeight = if (idx == result.stageIndex) FontWeight.Bold else FontWeight.Normal,
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
                                "$scorePct%",
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

            if (!isPatient) {
                val isXaiLoading =
                    xaiState.isMcLoading || xaiState.isGradCamLoading || xaiState.isGeminiLoading
                if (isXaiLoading) {
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
                                stringResource(R.string.home_screen_xai_loading_title),
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = ScanColors.xaiLoadingTitle,
                            )
                            Text(
                                stringResource(R.string.home_screen_xai_loading_subtitle),
                                style = MaterialTheme.typography.bodySmall,
                                color = ScanColors.xaiLoadingSubtitle,
                            )
                        }
                    }
                }
                xaiState.geminiReport?.let { report ->
                    val summaryKeyword = stringResource(R.string.home_screen_xai_summary_keyword)
                    val knownHeadings = stringArrayResource(R.array.xai_report_headings).toList()
                    val aiBlocks = parseAiReportBlocks(report.text, knownHeadings)
                    val summaryBlock = aiBlocks.firstOrNull {
                        it.first?.contains(
                            summaryKeyword,
                            ignoreCase = true
                        ) == true
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
                                    null,
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

                    if (otherBlocks.isNotEmpty()) {
                        otherBlocks.forEach { (title, content) ->
                            val cleanContent =
                                content.replace("**", "").replace(Regex("(?m)^- "), "• ")
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
                    }

                    Text(
                        stringResource(R.string.home_screen_result_disclaimer),
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

                xaiState.geminiError?.let { err ->
                    ErrorBanner(
                        String.format(
                            stringResource(R.string.home_screen_gemini_error),
                            err
                        )
                    )
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
            }

            if (isPatient) {
                PatientScanGuidanceCard(
                    stageIndex = result.stageIndex,
                    onOpenGames = onOpenGames,
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