package com.esmanureral.neurostage.ui.patient.games.puzzle

import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.ViewModel
import com.esmanureral.neurostage.ui.patient.puzzle.core.PuzzleSequentialRevealMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlin.math.min
import kotlin.math.sqrt


data class PuzzlePiece(
    val id: Int,
    val correctSlot: Int,
    val currentSlot: Int,
) {
    val isPlaced: Boolean get() = currentSlot == correctSlot
}

class PuzzleGameViewModel(
    rows: Int,
    cols: Int,
    private val sequentialRevealMode: PuzzleSequentialRevealMode = PuzzleSequentialRevealMode.None,
) : ViewModel() {

    private val total = rows * cols

    private val _pieces = MutableStateFlow(buildInitial())
    val pieces: StateFlow<List<PuzzlePiece>> = _pieces.asStateFlow()

    private val _isCompleted = MutableStateFlow(false)
    val isCompleted: StateFlow<Boolean> = _isCompleted.asStateFlow()

    private val _trayOrder = MutableStateFlow(buildTrayOrder())
    val trayOrder: StateFlow<List<Int>> = _trayOrder.asStateFlow()

    fun placePiece(pieceId: Int, slotIndex: Int) {
        _pieces.update { list ->
            val dragged = list.first { it.id == pieceId }
            val occupant = list.firstOrNull { it.currentSlot == slotIndex && it.id != pieceId }
            val oldSlot = dragged.currentSlot
            list.map { p ->
                when (p.id) {
                    dragged.id -> p.copy(currentSlot = slotIndex)
                    occupant?.id -> p.copy(currentSlot = oldSlot)
                    else -> p
                }
            }
        }
        _isCompleted.value = _pieces.value.all { it.isPlaced }
    }

    fun tryMagneticSnap(
        pieceId: Int,
        fingerRoot: Offset,
        pieceCenterRoot: Offset,
        slotCenters: Map<Int, Offset>,
        snapRadiusPx: Float,
    ): Boolean {
        val piece = _pieces.value.firstOrNull { it.id == pieceId } ?: return false
        val target = slotCenters[piece.correctSlot] ?: return false
        val fingerDist = distanceToTarget(fingerRoot.x - target.x, fingerRoot.y - target.y)
        val centerDist =
            distanceToTarget(pieceCenterRoot.x - target.x, pieceCenterRoot.y - target.y)
        val dist = min(fingerDist, centerDist)
        return if (dist <= snapRadiusPx) {
            placePiece(pieceId, piece.correctSlot)
            true
        } else {
            false
        }
    }

    fun reset() {
        _pieces.value = buildInitial()
        _trayOrder.value = buildTrayOrder()
        _isCompleted.value = false
    }

    fun piecesInTray(trayOrder: List<Int>, pieces: List<PuzzlePiece>): List<PuzzlePiece> =
        trayOrder.mapNotNull { pieceId ->
            pieces.find { it.id == pieceId && it.currentSlot < 0 }
        }

    private fun buildTrayOrder(): List<Int> = when (sequentialRevealMode) {
        PuzzleSequentialRevealMode.None ->
            (0 until total).toList().shuffled()

        PuzzleSequentialRevealMode.HorizontalLeftThenRight,
        PuzzleSequentialRevealMode.VerticalBottomThenTop,
        PuzzleSequentialRevealMode.Grid2x2TopRowThenBottom,
        PuzzleSequentialRevealMode.Grid2x3RowTriple,
        PuzzleSequentialRevealMode.Grid3x2RowPairs,
            -> (0 until total).toList()
    }

    private fun buildInitial(): List<PuzzlePiece> =
        (0 until total).map { id ->
            PuzzlePiece(
                id = id,
                correctSlot = id,
                currentSlot = -1,
            )
        }

    private fun distanceToTarget(dx: Float, dy: Float): Float = sqrt(dx * dx + dy * dy)
}
