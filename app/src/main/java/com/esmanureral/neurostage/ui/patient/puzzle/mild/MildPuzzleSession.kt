package com.esmanureral.neurostage.ui.patient.puzzle.mild

import com.esmanureral.neurostage.domain.patient.PatientStage
import com.esmanureral.neurostage.domain.patient.puzzle.mild.MildPuzzleCatalog
import com.esmanureral.neurostage.domain.patient.puzzle.mild.MildPuzzleStep
import com.esmanureral.neurostage.ui.patient.puzzle.core.PuzzleCatalogKind
import com.esmanureral.neurostage.ui.patient.puzzle.core.PuzzleSequentialRevealMode
import com.esmanureral.neurostage.ui.patient.puzzle.core.PuzzleSessionConfig

internal fun buildMildBrainExercisePuzzleSession(progress: Int): PuzzleSessionConfig {
    val step = MildPuzzleCatalog.stepForProgress(progress)
    return PuzzleSessionConfig(
        drawableRes = step.drawableRes,
        nameRes = step.nameRes,
        rows = step.rows,
        cols = step.cols,
        straightPieceEdges = false,
        viewModelKey = "mild_${step.name}",
        catalogKind = PuzzleCatalogKind.MildProgression,
        hasNextStep = MildPuzzleCatalog.hasNextStep(step),
        sequentialRevealMode = PuzzleSequentialRevealMode.None,
    )
}

internal fun buildFallbackGridPuzzleSession(stageIndex: Int?): PuzzleSessionConfig {
    val grid = PatientStage.fallbackPuzzleGridSize(stageIndex)
    val tea = MildPuzzleStep.TEA
    return PuzzleSessionConfig(
        drawableRes = tea.drawableRes,
        nameRes = tea.nameRes,
        rows = grid,
        cols = grid,
        straightPieceEdges = false,
        viewModelKey = "fallback_${grid}x${grid}",
        catalogKind = PuzzleCatalogKind.FallbackGrid,
        hasNextStep = false,
        sequentialRevealMode = PuzzleSequentialRevealMode.None,
    )
}