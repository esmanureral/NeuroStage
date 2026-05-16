package com.esmanureral.neurostage.ui.patient.games.puzzle

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.esmanureral.neurostage.ui.patient.puzzle.core.PuzzleSequentialRevealMode


class PuzzleGameViewModelFactory(
    private val rows: Int,
    private val cols: Int,
    private val sequentialRevealMode: PuzzleSequentialRevealMode = PuzzleSequentialRevealMode.None,
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        require(modelClass.isAssignableFrom(PuzzleGameViewModel::class.java)) {
            "Unknown ViewModel: ${modelClass.name}"
        }
        return PuzzleGameViewModel(rows, cols, sequentialRevealMode) as T
    }
}
