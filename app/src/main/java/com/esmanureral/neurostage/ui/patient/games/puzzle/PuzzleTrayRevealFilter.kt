package com.esmanureral.neurostage.ui.patient.games.puzzle

import com.esmanureral.neurostage.ui.patient.puzzle.core.PuzzleSequentialRevealMode

/** Sıralı tepsi görünürlüğü — ViewModel/UI ortak kullanım. */
object PuzzleTrayRevealFilter {

  fun visibleTrayPieces(
      mode: PuzzleSequentialRevealMode,
      gridRows: Int,
      gridCols: Int,
      trayPieces: List<PuzzlePiece>,
      allPieces: List<PuzzlePiece>,
  ): List<PuzzlePiece> = when (mode) {
    PuzzleSequentialRevealMode.HorizontalLeftThenRight ->
        filterHorizontalLeftThenRight(gridRows, gridCols, trayPieces, allPieces)

    PuzzleSequentialRevealMode.Grid2x2TopRowThenBottom ->
        filterGrid2x2TopThenBottom(gridRows, gridCols, trayPieces, allPieces)

    PuzzleSequentialRevealMode.Grid2x3RowTriple ->
        filterGrid2x3RowTriple(gridRows, gridCols, trayPieces, allPieces)

    else -> trayPieces
  }

  private fun filterHorizontalLeftThenRight(
      gridRows: Int,
      gridCols: Int,
      trayPieces: List<PuzzlePiece>,
      allPieces: List<PuzzlePiece>,
  ): List<PuzzlePiece> {
    if (gridRows != 1 || gridCols != 2) return trayPieces
    val first = allPieces.find { it.id == 0 }
    val second = allPieces.find { it.id == 1 }
    return trayPieces.filter { piece ->
      when {
        first != null && !first.isPlaced -> piece.id == 0
        first != null && first.isPlaced && second != null && !second.isPlaced -> piece.id == 1
        else -> false
      }
    }
  }

  private fun filterGrid2x2TopThenBottom(
      gridRows: Int,
      gridCols: Int,
      trayPieces: List<PuzzlePiece>,
      allPieces: List<PuzzlePiece>,
  ): List<PuzzlePiece> {
    if (gridRows != 2 || gridCols != 2) return trayPieces
    val topRowDone = allPieces.find { it.id == 0 }?.isPlaced == true &&
        allPieces.find { it.id == 1 }?.isPlaced == true
    return trayPieces.filter { piece ->
      if (!topRowDone) piece.id == 0 || piece.id == 1
      else piece.id == 2 || piece.id == 3
    }
  }

  private fun filterGrid2x3RowTriple(
      gridRows: Int,
      gridCols: Int,
      trayPieces: List<PuzzlePiece>,
      allPieces: List<PuzzlePiece>,
  ): List<PuzzlePiece> {
    if (gridRows != 2 || gridCols != 3) return trayPieces
    val topRowDone = (0..2).all { id -> allPieces.find { it.id == id }?.isPlaced == true }
    return trayPieces.filter { piece ->
      if (!topRowDone) piece.id in 0..2 else piece.id in 3..5
    }
  }
}
