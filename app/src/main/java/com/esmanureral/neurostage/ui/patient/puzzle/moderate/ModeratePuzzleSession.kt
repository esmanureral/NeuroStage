package com.esmanureral.neurostage.ui.patient.puzzle.moderate

import com.esmanureral.neurostage.domain.patient.puzzle.moderate.ModeratePuzzleCatalog
import com.esmanureral.neurostage.ui.patient.puzzle.core.PuzzleCatalogKind
import com.esmanureral.neurostage.ui.patient.puzzle.core.PuzzleSequentialRevealMode
import com.esmanureral.neurostage.ui.patient.puzzle.core.PuzzleSessionConfig

internal fun buildModerateDementiaPuzzleSession(progress: Int): PuzzleSessionConfig {
    val step = ModeratePuzzleCatalog.stepForProgress(progress)
    val sequentialMode = when (step.gameLevel) {
        1 -> PuzzleSequentialRevealMode.HorizontalLeftThenRight
        2 -> PuzzleSequentialRevealMode.Grid2x2_TopRowThenBottom
        3 -> PuzzleSequentialRevealMode.Grid2x3_RowTriple
        else -> PuzzleSequentialRevealMode.None
    }
    return PuzzleSessionConfig(
        drawableRes = step.drawableRes,
        nameRes = step.nameRes,
        rows = step.rows,
        cols = step.cols,
        straightPieceEdges = false,
        viewModelKey = "moderate_${step.name}",
        catalogKind = PuzzleCatalogKind.ModerateProgression,
        hasNextStep = ModeratePuzzleCatalog.hasNextStep(step),
        sequentialRevealMode = sequentialMode,
    )
}
