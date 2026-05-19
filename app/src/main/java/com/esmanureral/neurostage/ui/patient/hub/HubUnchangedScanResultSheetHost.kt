package com.esmanureral.neurostage.ui.patient.hub

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringArrayResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavBackStackEntry
import com.esmanureral.neurostage.R
import com.esmanureral.neurostage.navigation.HubScanNav
import com.esmanureral.neurostage.navigation.clearUnchangedScanResult
import com.esmanureral.neurostage.ui.patient.PatientUnchangedResultOverlay

@Composable
fun HubUnchangedScanResultSheetHost(
    hubBackStackEntry: NavBackStackEntry,
    onStartNewScan: () -> Unit,
) {
    val handle = hubBackStackEntry.savedStateHandle
    val showSheet by handle
        .getStateFlow(HubScanNav.SHOW_UNCHANGED_SHEET, false)
        .collectAsStateWithLifecycle()

    if (!showSheet) return

    val stageIndex = handle.get<Int>(HubScanNav.UNCHANGED_STAGE_INDEX) ?: return
    val confidence = handle.get<Float>(HubScanNav.UNCHANGED_CONFIDENCE) ?: return
    val scores = handle.get<List<Float>>(HubScanNav.UNCHANGED_SCORES) ?: return
    val stageLabels = stringArrayResource(R.array.dementia_stage_labels)
    val stageLabel = stageLabels.getOrNull(stageIndex).orEmpty()

    PatientUnchangedResultOverlay(
        stageIndex = stageIndex,
        confidencePercent = (confidence * 100).toInt(),
        scores = scores,
        stageLabel = stageLabel,
        onDismiss = { handle.clearUnchangedScanResult() },
        onTryAnotherScan = {
            handle.clearUnchangedScanResult()
            onStartNewScan()
        },
    )
}
