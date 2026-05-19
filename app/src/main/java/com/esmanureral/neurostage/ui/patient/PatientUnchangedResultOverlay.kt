package com.esmanureral.neurostage.ui.patient

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import com.esmanureral.neurostage.R
import com.esmanureral.neurostage.ui.theme.PatientResultColors
import com.esmanureral.neurostage.ui.theme.PatientResultDimens
import com.esmanureral.neurostage.ui.theme.ScanDimens
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

private const val OverlayDismissMs = 260L

@Composable
fun PatientUnchangedResultOverlay(
    stageIndex: Int,
    confidencePercent: Int,
    scores: List<Float>,
    stageLabel: String,
    onDismiss: () -> Unit,
    onTryAnotherScan: () -> Unit,
) {
    var visible by remember { mutableStateOf(true) }
    var pendingAction by remember { mutableStateOf<(() -> Unit)?>(null) }
    val sheetCorner = ScanDimens.sheetTopCorner
    val sheetPaddingH = PatientResultDimens.unchangedSheetHorizontalPadding
    val sheetPaddingBottom = PatientResultDimens.unchangedSheetBottomPadding
    val scope = rememberCoroutineScope()
    val sheetOffset = remember { Animatable(0f) }
    val scrollState = rememberScrollState()
    val density = LocalDensity.current
    val dismissThresholdPx = with(density) { 120.dp.toPx() }

    fun requestClose(afterClose: () -> Unit) {
        pendingAction = afterClose
        visible = false
    }

    suspend fun settleSheetDrag() {
        if (sheetOffset.value > dismissThresholdPx) {
            sheetOffset.animateTo(with(density) { 480.dp.toPx() }, tween(OverlayDismissMs.toInt()))
            requestClose(onDismiss)
        } else {
            sheetOffset.animateTo(0f, tween(OverlayDismissMs.toInt()))
        }
    }

    val sheetDragConnection = remember(scrollState, dismissThresholdPx) {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                val delta = available.y
                if (delta > 0f && scrollState.value == 0) {
                    scope.launch {
                        sheetOffset.snapTo((sheetOffset.value + delta).coerceAtLeast(0f))
                    }
                    return Offset(0f, delta)
                }
                if (delta < 0f && sheetOffset.value > 0f) {
                    val consumed = delta.coerceAtLeast(-sheetOffset.value)
                    scope.launch {
                        sheetOffset.snapTo((sheetOffset.value + consumed).coerceAtLeast(0f))
                    }
                    return Offset(0f, consumed)
                }
                return Offset.Zero
            }

            override suspend fun onPreFling(available: Velocity): Velocity {
                if (sheetOffset.value > 0f) {
                    settleSheetDrag()
                }
                return available
            }
        }
    }

    val dragHandleModifier = Modifier
        .fillMaxWidth()
        .pointerInput(dismissThresholdPx) {
            detectVerticalDragGestures(
                onVerticalDrag = { change, dragAmount ->
                    change.consume()
                    scope.launch {
                        sheetOffset.snapTo((sheetOffset.value + dragAmount).coerceAtLeast(0f))
                    }
                },
                onDragEnd = { scope.launch { settleSheetDrag() } },
                onDragCancel = { scope.launch { sheetOffset.animateTo(0f) } },
            )
        }

    BackHandler(enabled = visible) {
        requestClose(onDismiss)
    }

    LaunchedEffect(visible, pendingAction) {
        if (!visible && pendingAction != null) {
            delay(OverlayDismissMs)
            val action = pendingAction
            pendingAction = null
            action?.invoke()
        }
    }

    AnimatedVisibility(
        visible = visible,
        modifier = Modifier.fillMaxSize(),
        enter = EnterTransition.None,
        exit = slideOutVertically(
            targetOffsetY = { fullHeight -> fullHeight },
            animationSpec = tween(OverlayDismissMs.toInt()),
        ),
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.38f))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = { requestClose(onDismiss) },
                    ),
            )
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .offset { IntOffset(0, sheetOffset.value.roundToInt()) }
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(topStart = sheetCorner, topEnd = sheetCorner))
                    .background(PatientResultColors.cardBg)
                    .navigationBarsPadding()
                    .padding(bottom = sheetPaddingBottom),
            ) {
                Box(
                    modifier = dragHandleModifier
                        .fillMaxWidth()
                        .heightIn(min = 36.dp)
                        .padding(top = 6.dp, bottom = 4.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(99.dp))
                            .background(PatientResultColors.mutedBar)
                            .padding(horizontal = 28.dp, vertical = 4.dp),
                    )
                }
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .nestedScroll(sheetDragConnection)
                        .verticalScroll(scrollState)
                        .padding(horizontal = sheetPaddingH),
                    verticalArrangement = Arrangement.spacedBy(PatientResultDimens.sectionGap),
                ) {
                    Text(
                        text = stringResource(R.string.patient_scan_result_screen_title),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = PatientResultColors.textPrimary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    PatientUnchangedResultContent(
                        stageIndex = stageIndex,
                        confidencePercent = confidencePercent,
                        scores = scores,
                        stageLabel = stageLabel,
                        onReturnToHub = { requestClose(onDismiss) },
                        onTryAnotherScan = { requestClose(onTryAnotherScan) },
                    )
                }
            }
        }
    }
}
