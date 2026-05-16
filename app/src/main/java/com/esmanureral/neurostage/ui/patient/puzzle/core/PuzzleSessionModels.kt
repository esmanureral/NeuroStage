package com.esmanureral.neurostage.ui.patient.puzzle.core


enum class PuzzleCatalogKind {
    MildProgression,
    ModerateProgression,
    FallbackGrid,
}

enum class PuzzleProgressTrack {
    MildHomeCatalog,
    MriModerateCatalog,
}

enum class PuzzleSequentialRevealMode {
    None,
    HorizontalLeftThenRight,
    VerticalBottomThenTop,
    Grid2x2TopRowThenBottom,
    Grid2x3RowTriple,
    Grid3x2RowPairs,
}

data class PuzzleSessionConfig(
    val drawableRes: Int,
    val nameRes: Int,
    val rows: Int,
    val cols: Int,
    val straightPieceEdges: Boolean,
    val viewModelKey: String,
    val catalogKind: PuzzleCatalogKind,
    val hasNextStep: Boolean,
    val sequentialRevealMode: PuzzleSequentialRevealMode = PuzzleSequentialRevealMode.None,
    val stepIndexOneBased: Int = 1,
    val stepCount: Int = 1,
)
