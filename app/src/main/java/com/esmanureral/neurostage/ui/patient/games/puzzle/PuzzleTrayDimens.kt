package com.esmanureral.neurostage.ui.patient.games.puzzle

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.res.integerResource
import androidx.compose.ui.unit.Dp
import com.esmanureral.neurostage.R
import com.esmanureral.neurostage.ui.theme.PatientDimens

@Composable
@ReadOnlyComposable
fun puzzleTrayMinHeight(slotCount: Int): Dp {
    val fourPieceThreshold = integerResource(R.integer.puzzle_tray_piece_count_four)
    val sixPieceThreshold = integerResource(R.integer.puzzle_tray_piece_count_six)
    return when {
        slotCount <= fourPieceThreshold -> PatientDimens.puzzleTrayMinHeightFourPieces
        slotCount <= sixPieceThreshold -> PatientDimens.puzzleTrayMinHeightSixPieces
        else -> PatientDimens.puzzleTrayMinHeightNinePieces
    }
}
