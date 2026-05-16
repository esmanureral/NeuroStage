package com.esmanureral.neurostage.ui.patient.games.puzzle

import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.ImageBitmap

import com.esmanureral.neurostage.ui.patient.puzzle.core.PuzzleSessionConfig

@Stable
data class PuzzleScreenSession(
    val config: PuzzleSessionConfig,
    val fallbackGrid: Int,
    val slotCount: Int,
    val boardAspectRatio: Float,
)

@Stable
data class PuzzleGameUiState(
    val pieces: List<PuzzlePiece>,
    val trayOrder: List<Int>,
    val isCompleted: Boolean,
    val showSuccess: Boolean,
    val stepLabel: String,
    val puzzleBitmap: ImageBitmap,
    val clickSound: PuzzleClickSound,
    val dragState: PuzzleDragStateHolder,
    val ghostAlpha: Float,
    val knobFraction: Float,
    val snapRadiusFraction: Float,
    val trayScaleOfSlot: Float,
    val trayHitScale: Float,
    val trayBackgroundAlpha: Float,
    val borderAnimDurationMs: Int,
    val trayColumns: Int,
    val successContentWidthFraction: Float,
    val boardPieceZIndex: Float,
    val dragOverlayZIndex: Float,
    val slotStrokeNormalPx: Float,
    val slotStrokeMagnetPx: Float,
)
