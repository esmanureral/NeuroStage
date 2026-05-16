package com.esmanureral.neurostage.ui.patient.games.puzzle

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.integerArrayResource
import androidx.compose.ui.res.integerResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import com.esmanureral.neurostage.R

/** Puzzle layout sabitleri — kaynak: `puzzle_config.xml`, `puzzle_arrays.xml`, `dimens.xml`. */
object PuzzleLayoutMetrics {

    const val NO_DRAG_PIECE_ID = -1

    @Composable
    @ReadOnlyComposable
    fun snapRadiusFraction(): Float = stringResource(R.string.puzzle_snap_radius_fraction).toFloat()

    @Composable
    @ReadOnlyComposable
    fun ghostImageAlpha(): Float = stringResource(R.string.puzzle_ghost_image_alpha).toFloat()

    @Composable
    @ReadOnlyComposable
    fun jigsawKnobFraction(): Float = stringResource(R.string.puzzle_jigsaw_knob_fraction).toFloat()

    @Composable
    @ReadOnlyComposable
    fun trayScaleOfSlot(): Float = stringResource(R.string.puzzle_tray_scale_of_slot).toFloat()

    @Composable
    @ReadOnlyComposable
    fun trayHitScale(): Float = stringResource(R.string.puzzle_tray_hit_scale).toFloat()

    @Composable
    @ReadOnlyComposable
    fun trayBackgroundAlpha(): Float =
        stringResource(R.string.puzzle_tray_background_alpha).toFloat()

    @Composable
    @ReadOnlyComposable
    fun borderAnimDurationMs(): Int = integerResource(R.integer.puzzle_border_anim_duration_ms)

    @Composable
    @ReadOnlyComposable
    fun trayColumnsFor(gridCols: Int): Int =
        if (gridCols >= integerResource(R.integer.puzzle_tray_columns_threshold)) {
            integerResource(R.integer.puzzle_tray_columns_wide)
        } else {
            integerResource(R.integer.puzzle_tray_columns_default)
        }

    @Composable
    @ReadOnlyComposable
    fun successContentWidthFraction(): Float =
        integerResource(R.integer.puzzle_success_content_width_percent) / 100f

    @Composable
    @ReadOnlyComposable
    fun boardPieceZIndex(): Float = integerResource(R.integer.puzzle_board_piece_z_index).toFloat()

    @Composable
    @ReadOnlyComposable
    fun dragOverlayZIndex(): Float =
        integerResource(R.integer.puzzle_drag_overlay_z_index).toFloat()

    @Composable
    @ReadOnlyComposable
    fun trayTiltDegrees(): List<Float> =
        integerArrayResource(R.array.puzzle_tray_tilt_degrees).map(Int::toFloat)

    @Composable
    fun slotStrokeWidthPx(normal: Dp, magnet: Dp): Pair<Float, Float> {
        val density = LocalDensity.current
        return remember(normal, magnet, density) {
            with(density) { normal.toPx() to magnet.toPx() }
        }
    }
}
