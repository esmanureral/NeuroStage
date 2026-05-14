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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.esmanureral.neurostage.AnalysisState
import com.esmanureral.neurostage.AnalysisViewModel
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

private val waveBottomShape: Shape = object : Shape {
    override fun createOutline(
        size: GeometrySize,
        layoutDirection: LayoutDirection,
        density: Density,
    ): Outline {
        val waveDepth = with(density) { 20.dp.toPx() }
        val path = Path().apply {
            moveTo(0f, 0f)
            lineTo(size.width, 0f)
            lineTo(size.width, size.height - waveDepth)
            quadraticTo(size.width / 2f, size.height + waveDepth, 0f, size.height - waveDepth)
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
                onNewScan = { bitmapState.value = null; viewModel.reset() }
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(waveBottomShape)
                .background(neurostageBrandBlue)
                .padding(horizontal = 20.dp, vertical = 24.dp),
        ) {
            Column {
                if (onBack != null) {
                    IconButton(onClick = onBack, modifier = Modifier.offset(x = (-12).dp)) {
                        Icon(Icons.AutoMirrored.Outlined.ArrowBack, null, tint = NsWhite)
                    }
                }
                Text(
                    stringResource(R.string.home_screen_upload_title),
                    style = MaterialTheme.typography.headlineSmall,
                    color = NsWhite,
                    fontWeight = FontWeight.ExtraBold,
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    stringResource(R.string.home_screen_upload_subtitle),
                    style = MaterialTheme.typography.bodyMedium,
                    color = NsWhite.copy(alpha = 0.8f),
                )
                Spacer(Modifier.height(16.dp))
            }
        }

        Spacer(Modifier.height(24.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 28.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(NsWhite)
                .border(
                    width = (1.5f * pulse).dp,
                    color = neurostageBrandBlue.copy(alpha = 0.3f * pulse),
                    shape = RoundedCornerShape(24.dp)
                )
                .clickable(onClick = onPickGallery),
            contentAlignment = Alignment.Center,
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(NsChipIndigoBg),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Outlined.FileUpload, null,
                        tint = neurostageBrandBlue,
                        modifier = Modifier.size(36.dp),
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
                    modifier = Modifier.padding(horizontal = 28.dp, vertical = 8.dp),
                )
            }
        }

        Spacer(Modifier.height(20.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 28.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(16.dp))
                    .background(NsWhite)
                    .clickable(onClick = onPickCamera)
                    .padding(vertical = 18.dp),
                contentAlignment = Alignment.Center,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Icon(
                        Icons.Outlined.PhotoCamera,
                        null,
                        tint = neurostageBrandBlue,
                        modifier = Modifier.size(20.dp)
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
                    .clip(RoundedCornerShape(16.dp))
                    .background(neurostageBrandBlue)
                    .clickable(onClick = onPickGallery)
                    .padding(vertical = 18.dp),
                contentAlignment = Alignment.Center,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Icon(
                        Icons.Outlined.PhotoLibrary,
                        null,
                        tint = NsWhite,
                        modifier = Modifier.size(20.dp)
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
            modifier = Modifier.padding(top = 12.dp, bottom = 20.dp),
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
                .padding(horizontal = 8.dp, vertical = 4.dp),
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
                    .padding(start = 4.dp),
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 24.dp, vertical = 8.dp)
                .clip(RoundedCornerShape(20.dp)),
            contentAlignment = Alignment.Center,
        ) {
            bitmap?.let {
                androidx.compose.foundation.Image(
                    bitmap = it.asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(12.dp)),
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
                    .padding(horizontal = 22.dp, vertical = 4.dp),
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                .background(NsWhite)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Text(
                stringResource(R.string.home_screen_preview_instruction),
                style = MaterialTheme.typography.bodyMedium,
                color = NsGray600,
            )
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(16.dp))
                        .background(NsDoctorLoginTrackBg)
                        .clickable(onClick = onRepick)
                        .padding(vertical = 14.dp),
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
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (validated) neurostageBrandBlue else NsGray300)
                        .clickable(enabled = validated, onClick = onAnalyze)
                        .padding(vertical = 14.dp),
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
        Box(modifier = Modifier.size(200.dp), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(
                progress = { animProg },
                modifier = Modifier.size(200.dp),
                strokeWidth = 8.dp,
                color = neurostageBrandBlue,
                trackColor = NsDoctorLoginFieldBorderIdle,
                strokeCap = StrokeCap.Round,
            )
            Box(
                modifier = Modifier
                    .size(180.dp)
                    .rotate(rot)
                    .border(2.dp, neurostageBrandBlue.copy(0.2f), CircleShape)
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

        Spacer(Modifier.height(32.dp))

        Text(
            if (completedSteps < 4) stringResource(R.string.home_screen_analyze_brain_img) else stringResource(
                R.string.home_screen_analyze_calculating
            ),
            style = MaterialTheme.typography.titleMedium,
            color = NsGray900,
            fontWeight = FontWeight.SemiBold,
        )

        Spacer(Modifier.height(16.dp))

        val labels = stringArrayResource(R.array.home_screen_analyze_labels).toList()
        Column(
            modifier = Modifier.padding(horizontal = 40.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            labels.forEachIndexed { i, label ->
                val done = i < completedSteps
                val active = i == completedSteps && completedSteps < 4
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
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
) {
    val t = stageTone(result.stageIndex)

    val confidencePct = (result.confidence * 100).toInt()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F7FF))
            .systemBarsPadding()
            .verticalScroll(rememberScrollState()),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(waveBottomShape)
                .background(neurostageBrandBlue)
                .padding(horizontal = 8.dp, vertical = 20.dp),
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
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
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
                    .clip(RoundedCornerShape(14.dp))
                    .background(NsWhite)
                    .padding(horizontal = 16.dp, vertical = 14.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(t.dot),
                    )
                    Spacer(Modifier.width(8.dp))
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
                    .clip(RoundedCornerShape(14.dp))
                    .background(NsWhite)
                    .padding(16.dp),
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    allClasses.forEach { (idx, label, color) ->
                        val scorePct = (scores.getOrElse(idx) { 0f } * 100).toInt()
                        val barAnim by animateFloatAsState(
                            targetValue = scorePct / 100f,
                            animationSpec = tween(700, easing = FastOutSlowInEasing),
                            label = "bar$idx",
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            Text(
                                label,
                                style = MaterialTheme.typography.bodySmall,
                                color = if (idx == result.stageIndex) NsNavy else NsSlate,
                                fontWeight = if (idx == result.stageIndex) FontWeight.Bold else FontWeight.Normal,
                                modifier = Modifier.width(108.dp),
                            )
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(99.dp))
                                    .background(Color(0xFFEDE9FE)),
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxHeight()
                                        .fillMaxWidth(barAnim)
                                        .clip(RoundedCornerShape(99.dp))
                                        .background(color),
                                )
                            }
                            Text(
                                "$scorePct%",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = NsNavy,
                                modifier = Modifier.width(32.dp),
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
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFF3E8FF))
                            .border(1.dp, Color(0xFFD8B4FE), RoundedCornerShape(12.dp))
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp,
                            color = Color(0xFF9333EA)
                        )
                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Text(
                                stringResource(R.string.home_screen_xai_loading_title),
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF6B21A8)
                            )
                            Text(
                                stringResource(R.string.home_screen_xai_loading_subtitle),
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF7E22CE)
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
                                .clip(RoundedCornerShape(14.dp))
                                .background(NsWhite)
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                            ) {
                                Icon(
                                    Icons.Outlined.AutoAwesome,
                                    null,
                                    tint = Color(0xFF7C3AED),
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    summaryBlock.first ?: summaryFallback,
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF7C3AED)
                                )
                            }
                            Text(
                                summaryBlock.second.replace("**", ""),
                                style = MaterialTheme.typography.bodyMedium,
                                color = NsGray800,
                                lineHeight = 22.sp,
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
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(NsWhite)
                                    .padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                            ) {
                                Text(
                                    title?.uppercase() ?: clinicFallback,
                                    style = MaterialTheme.typography.labelMedium,
                                    color = NsIndigo500,
                                    fontWeight = FontWeight.ExtraBold,
                                    letterSpacing = 0.5.sp
                                )
                                if (cleanContent.isNotBlank()) {
                                    Text(
                                        cleanContent,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = NsGray800,
                                        lineHeight = 20.sp
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
                            .padding(top = 4.dp, bottom = 12.dp)
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
                            .clip(RoundedCornerShape(10.dp))
                            .background(NsRose50)
                            .padding(12.dp),
                    )
                }
            }

            Button(
                onClick = onNewScan,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = neurostageBrandBlue,
                ),
                contentPadding = PaddingValues(vertical = 16.dp),
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
                    .padding(bottom = 8.dp),
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
            .clip(RoundedCornerShape(10.dp))
            .background(NsRose50)
            .padding(12.dp),
    )
}

@Composable
private fun MriImageBox(bitmap: Bitmap?, label: String, modifier: Modifier) {
    Box(
        modifier = modifier
            .fillMaxHeight()
            .clip(RoundedCornerShape(14.dp))
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
                .background(Color(0x99000000))
                .padding(horizontal = 8.dp, vertical = 4.dp),
        )
    }
}