package com.esmanureral.neurostage.xai

import android.content.Context
import com.esmanureral.neurostage.R

object GradCamRegionMapper {
    private const val GRID = 16

    fun detectActiveRegionFromGrid(context: Context, row: Int, col: Int): String {
        val cam = FloatArray(GRID * GRID)
        val idx = row.coerceIn(0, GRID - 1) * GRID + col.coerceIn(0, GRID - 1)
        cam[idx] = 1f
        return detectActiveRegion(context, cam)
    }

    fun detectActiveRegion(context: Context, cam: FloatArray): String {
        val peakIdx = cam.indices.maxByOrNull { cam[it] }
            ?: return context.getString(R.string.brain_region_unknown)
        val row = peakIdx / GRID
        val col = peakIdx % GRID
        val t = GRID / 3
        return when {
            row < t -> when {
                col < t -> context.getString(R.string.brain_region_left_frontal)
                col > GRID - t -> context.getString(R.string.brain_region_right_frontal)
                else -> context.getString(R.string.brain_region_prefrontal)
            }

            row <= GRID - t -> when {
                col < t -> context.getString(R.string.brain_region_left_temporal)
                col > GRID - t -> context.getString(R.string.brain_region_right_temporal)
                else -> context.getString(R.string.brain_region_hippocampal)
            }

            else -> when {
                col < t -> context.getString(R.string.brain_region_left_parietal)
                col > GRID - t -> context.getString(R.string.brain_region_right_parietal)
                else -> context.getString(R.string.brain_region_parietal)
            }
        }
    }
}
