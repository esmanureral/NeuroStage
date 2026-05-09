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
import androidx.compose.ui.graphics.Brush
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
import java.text.SimpleDateFormat
import java.util.*

private val dateFmt = SimpleDateFormat("d MMM yyyy", Locale("tr"))
private enum class Step { UPLOAD, PREVIEW, ANALYZE, RESULT }

// ── Evre renk/etiket yardımcısı ──────────────────────────────────────────────
private data class StageTone(val label: String, val dot: Color, val bg: Color, val scale: Int)
private fun stageTone(i: Int) = when (i) {
    2    -> StageTone("Sağlıklı",       StageColors.Healthy,  StageColors.HealthyBg,  0)
    3    -> StageTone("Çok Hafif Evre", StageColors.VeryMild, StageColors.VeryMildBg, 1)
    0    -> StageTone("Hafif Evre",     StageColors.Mild,     StageColors.MildBg,     2)
    1    -> StageTone("Orta Evre",      StageColors.Moderate, StageColors.ModerateBg, 3)
    else -> StageTone("İleri Evre",     StageColors.Severe,   StageColors.SevereBg,   4)
}

@Composable
fun MainScreen(
    viewModel: AnalysisViewModel = hiltViewModel(),
) {
    val state           by viewModel.state.collectAsStateWithLifecycle()
    val analysisSteps   by viewModel.analysisProgressSteps.collectAsStateWithLifecycle()
    val validationError by viewModel.validationError.collectAsStateWithLifecycle()
    val isMriValidated  by viewModel.isMriValidated.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }

    val step = when {
        state is AnalysisState.Success -> Step.RESULT
        state is AnalysisState.Loading -> Step.ANALYZE
        bitmap != null                 -> Step.PREVIEW
        else                           -> Step.UPLOAD
    }

    val gallery = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            val bmp = context.contentResolver.openInputStream(it)?.use { s -> BitmapFactory.decodeStream(s) }
            if (bmp != null) { bitmap = bmp; viewModel.validateAndSetBitmap(bmp) }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(NsBgTop)
    ) {
        when (step) {
            Step.UPLOAD  -> UploadStep(
                error  = validationError,
                onPick = { gallery.launch("image/*") }
            )
            Step.PREVIEW -> PreviewStep(
                bitmap    = bitmap,
                validated = isMriValidated,
                error     = validationError,
                onRepick  = { gallery.launch("image/*") },
                onAnalyze = { bitmap?.let(viewModel::analyze) },
                onReset   = { bitmap = null; viewModel.reset() }
            )
            Step.ANALYZE -> AnalyzeStep(completedSteps = analysisSteps.coerceIn(0, 4))
            Step.RESULT  -> ResultStep(
                result    = state as AnalysisState.Success,
                onNewScan = { bitmap = null; viewModel.reset() }
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// UPLOAD
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun UploadStep(error: String?, onPick: () -> Unit) {
    val inf = rememberInfiniteTransition(label = "pulse")
    val pulse by inf.animateFloat(
        initialValue = 0.85f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1800, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "pf"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.height(40.dp))

        Text(
            "NeuroStage",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.ExtraBold,
            color = NsWhite,
        )
        Text(
            "Alzheimer MR Analizi",
            style = MaterialTheme.typography.bodySmall,
            color = NsWhite.copy(0.5f),
        )

        Spacer(Modifier.height(32.dp))

        // Tarama alanı
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 28.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(Brush.verticalGradient(listOf(NsPanel, NsBgBottom)))
                .border(
                    width = (1.5f * pulse).dp,
                    color = NsWhite.copy(alpha = 0.18f * pulse),
                    shape = RoundedCornerShape(24.dp)
                )
                .clickable(onClick = onPick),
            contentAlignment = Alignment.Center,
        ) {
            ScanCorners()
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                Icon(
                    Icons.Outlined.FileUpload, null,
                    tint = NsWhite.copy(0.7f),
                    modifier = Modifier.size(52.dp),
                )
                Text(
                    "MR Görüntüsü Seçin",
                    style = MaterialTheme.typography.titleMedium,
                    color = NsWhite,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    "JPG · PNG",
                    style = MaterialTheme.typography.bodySmall,
                    color = NsWhite.copy(0.4f),
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
                    color = NsCoral,
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 28.dp, vertical = 8.dp),
                )
            }
        }

        Spacer(Modifier.height(20.dp))

        // Seç butonu
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 28.dp)
                .clip(RoundedCornerShape(99.dp))
                .background(Brush.horizontalGradient(listOf(NsRose, NsLavender)))
                .clickable(onClick = onPick)
                .padding(vertical = 18.dp),
            contentAlignment = Alignment.Center,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Icon(Icons.Outlined.PhotoLibrary, null, tint = NsWhite, modifier = Modifier.size(20.dp))
                Text(
                    "Galeriden Seç",
                    style = MaterialTheme.typography.titleMedium,
                    color = NsWhite,
                    fontWeight = FontWeight.Bold,
                )
            }
        }

        Text(
            "Sonuçlar tıbbi tanı yerine geçmez.",
            style = MaterialTheme.typography.bodySmall,
            color = NsWhite.copy(0.3f),
            modifier = Modifier.padding(top = 12.dp, bottom = 20.dp),
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// PREVIEW
// ─────────────────────────────────────────────────────────────────────────────
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
                Icon(Icons.Outlined.ArrowBack, null, tint = NsWhite)
            }
            Text(
                "Görüntü Hazır",
                style = MaterialTheme.typography.titleMedium,
                color = NsWhite,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.weight(1f).padding(start = 4.dp),
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
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit,
                )
                ScanCorners()
            }
        }

        error?.let {
            Text(
                it,
                color = NsCoral,
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
                .background(NsPanel)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Text(
                "Analizi başlatmak için onaylayın",
                style = MaterialTheme.typography.bodyMedium,
                color = NsWhite.copy(0.6f),
            )
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(99.dp))
                        .background(NsWhite.copy(0.1f))
                        .border(1.dp, NsWhite.copy(0.15f), RoundedCornerShape(99.dp))
                        .clickable(onClick = onRepick)
                        .padding(vertical = 14.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text("Değiştir", color = NsWhite.copy(0.8f),
                        style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold)
                }
                Box(
                    modifier = Modifier
                        .weight(2f)
                        .clip(RoundedCornerShape(99.dp))
                        .background(
                            if (validated)
                                Brush.horizontalGradient(listOf(NsRose, NsLavender))
                            else
                                Brush.horizontalGradient(listOf(NsRose.copy(0.3f), NsLavender.copy(0.3f)))
                        )
                        .clickable(enabled = validated, onClick = onAnalyze)
                        .padding(vertical = 14.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text("Analizi Başlat", color = NsWhite,
                        style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                }
            }
            Text(
                "~15 saniye · Sonuçlar tıbbi tanı yerine geçmez.",
                style = MaterialTheme.typography.bodySmall,
                color = NsWhite.copy(0.35f),
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// ANALYZE
// ─────────────────────────────────────────────────────────────────────────────
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
                color = NsRose,
                trackColor = NsWhite.copy(0.1f),
                strokeCap = StrokeCap.Round,
            )
            Box(
                modifier = Modifier
                    .size(180.dp)
                    .rotate(rot)
                    .border(2.dp, NsLavender.copy(0.25f), CircleShape)
            )
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    "${(animProg * 100).toInt()}%",
                    style = MaterialTheme.typography.headlineLarge,
                    color = NsWhite,
                    fontWeight = FontWeight.ExtraBold,
                )
                Text("Taranıyor…", style = MaterialTheme.typography.bodySmall, color = NsWhite.copy(0.55f))
            }
        }

        Spacer(Modifier.height(32.dp))

        Text(
            if (completedSteps < 4) "Beyin görüntüsü inceleniyor…" else "Sonuç hesaplanıyor…",
            style = MaterialTheme.typography.titleMedium,
            color = NsWhite,
            fontWeight = FontWeight.SemiBold,
        )

        Spacer(Modifier.height(16.dp))

        val labels = listOf("Görüntü hazırlanıyor", "Beyin segmentasyonu", "Hipokampus analizi", "Evre tespiti")
        Column(
            modifier = Modifier.padding(horizontal = 40.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            labels.forEachIndexed { i, label ->
                val done   = i < completedSteps
                val active = i == completedSteps && completedSteps < 4
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(when { done -> NsRose; active -> NsLavender; else -> NsWhite.copy(0.2f) })
                    )
                    Text(
                        label,
                        style = MaterialTheme.typography.bodyMedium,
                        color = when { done || active -> NsWhite; else -> NsWhite.copy(0.35f) },
                    )
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// RESULT
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun ResultStep(result: AnalysisState.Success, onNewScan: () -> Unit) {
    val t     = stageTone(result.stageIndex)
    val pct   = (result.confidence * 100).toInt()
    val today = remember { dateFmt.format(Date()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(t.bg)
            .systemBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(22.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            "Analiz Sonucu",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.ExtraBold,
            color = NsNavy,
        )

        // Sonuç kartı
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(NsWhite)
                .padding(20.dp),
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Box(Modifier.size(10.dp).clip(CircleShape).background(t.dot))
                    Text(
                        "ANALİZ TAMAMLANDI",
                        style = MaterialTheme.typography.labelSmall,
                        color = t.dot,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                    )
                }
                Text(t.label, style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.ExtraBold, color = NsNavy)
                Text(result.description, style = MaterialTheme.typography.bodyMedium, color = NsSlate)

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    ResultChip("GÜVEN",  "%$pct",  Modifier.weight(1f))
                    ResultChip("TARİH",  today,     Modifier.weight(1f))
                }
            }
        }

        // Evre skalası
        Text("EVRE SKALASI", style = MaterialTheme.typography.labelSmall,
            color = NsSlate.copy(0.5f), letterSpacing = 1.sp)
        val scaleColors = listOf(
            StageColors.Healthy, StageColors.VeryMild,
            StageColors.Mild, StageColors.Moderate, StageColors.Severe,
        )
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxWidth()) {
            repeat(5) { idx ->
                Box(
                    Modifier.weight(1f).height(8.dp).clip(RoundedCornerShape(99.dp))
                        .background(if (idx == t.scale) scaleColors[idx] else Color(0xFFE0DDED))
                )
            }
        }

        // Yeni tarama butonu
        Button(
            onClick = onNewScan,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = NsRose),
            elevation = ButtonDefaults.buttonElevation(0.dp),
        ) {
            Text("Yeni Tarama", color = NsWhite, fontWeight = FontWeight.Bold)
        }

        Text(
            "Bu uygulama tıbbi tanı aracı değildir.",
            style = MaterialTheme.typography.bodySmall,
            color = NsSlate.copy(0.5f),
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun ResultChip(label: String, value: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFF5F3FF))
            .padding(12.dp),
    ) {
        Text(label, style = MaterialTheme.typography.labelSmall, color = NsSlate)
        Text(value, style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold, color = NsNavy)
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Ortak tarayıcı köşe çerçevesi
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun ScanCorners() {
    val len = 28.dp
    val w   = 3.dp
    Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Box(Modifier.size(len, w).align(Alignment.TopStart).background(NsWhite))
        Box(Modifier.size(w, len).align(Alignment.TopStart).background(NsWhite))
        Box(Modifier.size(len, w).align(Alignment.TopEnd).background(NsWhite))
        Box(Modifier.size(w, len).align(Alignment.TopEnd).background(NsWhite))
        Box(Modifier.size(len, w).align(Alignment.BottomStart).background(NsWhite))
        Box(Modifier.size(w, len).align(Alignment.BottomStart).background(NsWhite))
        Box(Modifier.size(len, w).align(Alignment.BottomEnd).background(NsWhite))
        Box(Modifier.size(w, len).align(Alignment.BottomEnd).background(NsWhite))
    }
}
