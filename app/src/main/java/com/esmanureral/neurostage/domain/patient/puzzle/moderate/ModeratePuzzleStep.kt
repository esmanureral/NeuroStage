package com.esmanureral.neurostage.domain.patient.puzzle.moderate

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.esmanureral.neurostage.R

enum class ModeratePuzzleStep(
    @param:DrawableRes val drawableRes: Int,
    @param:StringRes val nameRes: Int,
    val rows: Int,
    val cols: Int,
    val gameLevel: Int,
) {
    COFFEE_L1(
        drawableRes = R.drawable.trcoffee_puzzle,
        nameRes = R.string.puzzle_moderate_coffee,
        rows = 1,
        cols = 2,
        gameLevel = 1,
    ),
    BUTTERFLY_L1(
        drawableRes = R.drawable.butterfly_puzzle,
        nameRes = R.string.puzzle_moderate_butterfly,
        rows = 1,
        cols = 2,
        gameLevel = 1,
    ),
    WATERMELON_L2(
        drawableRes = R.drawable.watermelon_puzzle,
        nameRes = R.string.puzzle_moderate_watermelon,
        rows = 2,
        cols = 2,
        gameLevel = 2,
    ),
    SEA_L2(
        drawableRes = R.drawable.sea_puzzle,
        nameRes = R.string.puzzle_moderate_sea,
        rows = 2,
        cols = 2,
        gameLevel = 2,
    ),
    CAT_L2(
        drawableRes = R.drawable.cat_puzzle,
        nameRes = R.string.puzzle_mild_cat,
        rows = 2,
        cols = 2,
        gameLevel = 2,
    ),
    CAR_L3(
        drawableRes = R.drawable.car_puzzle,
        nameRes = R.string.puzzle_mild_car,
        rows = 2,
        cols = 3,
        gameLevel = 3,
    ),
    CLOCK_L3(
        drawableRes = R.drawable.clock_puzzle,
        nameRes = R.string.puzzle_mild_clock,
        rows = 2,
        cols = 3,
        gameLevel = 3,
    ),
    MAIDENS_L3(
        drawableRes = R.drawable.maidens_ower_puzzle,
        nameRes = R.string.puzzle_mild_tower,
        rows = 2,
        cols = 3,
        gameLevel = 3,
    ),
}

object ModeratePuzzleCatalog {
    private val orderedSteps: List<ModeratePuzzleStep> = listOf(
        ModeratePuzzleStep.COFFEE_L1,
        ModeratePuzzleStep.BUTTERFLY_L1,
        ModeratePuzzleStep.WATERMELON_L2,
        ModeratePuzzleStep.SEA_L2,
        ModeratePuzzleStep.CAT_L2,
        ModeratePuzzleStep.CAR_L3,
        ModeratePuzzleStep.CLOCK_L3,
        ModeratePuzzleStep.MAIDENS_L3,
    )

    fun maxProgressIndex(): Int = orderedSteps.lastIndex

    fun stepForProgress(progress: Int): ModeratePuzzleStep {
        val index = progress.coerceIn(0, maxProgressIndex())
        return orderedSteps[index]
    }

    fun hasNextStep(step: ModeratePuzzleStep): Boolean = step != orderedSteps.last()
}
