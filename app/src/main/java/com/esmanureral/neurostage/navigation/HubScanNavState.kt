package com.esmanureral.neurostage.navigation

import androidx.lifecycle.SavedStateHandle

fun SavedStateHandle.publishUnchangedScanResult(
    stageIndex: Int,
    confidence: Float,
    scores: List<Float>,
) {
    set(HubScanNav.SHOW_UNCHANGED_SHEET, true)
    set(HubScanNav.UNCHANGED_STAGE_INDEX, stageIndex)
    set(HubScanNav.UNCHANGED_CONFIDENCE, confidence)
    set(HubScanNav.UNCHANGED_SCORES, scores)
}

fun SavedStateHandle.clearUnchangedScanResult() {
    remove<Boolean>(HubScanNav.SHOW_UNCHANGED_SHEET)
    remove<Int>(HubScanNav.UNCHANGED_STAGE_INDEX)
    remove<Float>(HubScanNav.UNCHANGED_CONFIDENCE)
    remove<List<Float>>(HubScanNav.UNCHANGED_SCORES)
}
