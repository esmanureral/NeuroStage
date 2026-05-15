package com.esmanureral.neurostage.domain.patient

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.esmanureral.neurostage.R

enum class MildPuzzleStep(
    @param:DrawableRes val drawableRes: Int,
    @param:StringRes val nameRes: Int,
    val rows: Int,
    val cols: Int,
    val gameLevel: Int,
) {
    TEA(R.drawable.tea_puzzle, R.string.puzzle_mild_tea, 2, 2, 1),
    DAISY(R.drawable.daisy_puzzle, R.string.puzzle_mild_daisy, 2, 2, 1),
    RADIO(R.drawable.radio_puzzle, R.string.puzzle_mild_radio, 2, 3, 2),
    CAR(R.drawable.car_puzzle, R.string.puzzle_mild_car, 2, 3, 2),
    CAT(R.drawable.cat_puzzle, R.string.puzzle_mild_cat, 2, 3, 2),
    TOWER(R.drawable.maidens_ower_puzzle, R.string.puzzle_mild_tower, 3, 3, 3),
    COFFEE(R.drawable.coffee_puzzle, R.string.puzzle_mild_coffee, 3, 3, 3),
    CLOCK(R.drawable.clock_puzzle, R.string.puzzle_mild_clock, 3, 3, 3),
    ;

}

object MildPuzzleCatalog {
    private val orderedSteps: List<MildPuzzleStep> = MildPuzzleStep.entries

    fun maxProgressIndex(): Int = orderedSteps.lastIndex

    fun stepForProgress(progress: Int): MildPuzzleStep {
        val index = progress.coerceIn(0, maxProgressIndex())
        return orderedSteps[index]
    }

    fun hasNextStep(step: MildPuzzleStep): Boolean = step != orderedSteps.last()

    fun stepIndexInLevel(step: MildPuzzleStep): Int = when (step) {
        MildPuzzleStep.TEA -> 1
        MildPuzzleStep.DAISY -> 2
        MildPuzzleStep.RADIO -> 1
        MildPuzzleStep.CAR -> 2
        MildPuzzleStep.CAT -> 3
        MildPuzzleStep.TOWER -> 1
        MildPuzzleStep.COFFEE -> 2
        MildPuzzleStep.CLOCK -> 3
    }

    fun homeSubtitleFormatRes(gameLevel: Int): Int = when (gameLevel) {
        1 -> R.string.patient_mild_puzzle_subtitle_l1
        2 -> R.string.patient_mild_puzzle_subtitle_l2
        3 -> R.string.patient_mild_puzzle_subtitle_l3
        else -> R.string.patient_mild_puzzle_subtitle_l1
    }
}
