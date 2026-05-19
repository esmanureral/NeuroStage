package com.esmanureral.neurostage.domain.patient.colormatch

import com.esmanureral.neurostage.R
import kotlin.random.Random

val ColorMatchStandardPalette: List<ColorMatchColor> = ColorMatchColor.entries

data class ColorMatchLevelDef(
    val id: String,
    val displayLevel: Int,
    val instructionRes: Int,
    val gridSize: Int,
    val palette: List<ColorMatchColor>,
    val uniqueColorsOnly: Boolean,
    val compact: Boolean,
    val discScale: Float,
    val poolDiscScale: Float,
    val showSlotHint: Boolean = false,
)

const val ColorMatchPoolTouchPaddingDp = 14

data class ColorMatchRound(
    val template: List<ColorMatchColor>,
    val poolTokens: List<ColorMatchPoolToken>,
) {
    val slotCount: Int get() = template.size
}

data class ColorMatchPoolToken(
    val id: Int,
    val color: ColorMatchColor,
)

object ColorMatchCatalog {
    val levels: List<ColorMatchLevelDef> = listOf(
        ColorMatchLevelDef(
            id = "1",
            displayLevel = 1,
            instructionRes = R.string.color_match_instruction_l1,
            gridSize = 2,
            palette = ColorMatchStandardPalette,
            uniqueColorsOnly = true,
            compact = false,
            discScale = 0.76f,
            poolDiscScale = 0.94f,
        ),
        ColorMatchLevelDef(
            id = "2",
            displayLevel = 2,
            instructionRes = R.string.color_match_instruction_l1_3,
            gridSize = 2,
            palette = ColorMatchStandardPalette,
            uniqueColorsOnly = true,
            compact = true,
            discScale = 0.68f,
            poolDiscScale = 0.88f,
            showSlotHint = true,
        ),
        ColorMatchLevelDef(
            id = "3",
            displayLevel = 3,
            instructionRes = R.string.color_match_instruction_l2,
            gridSize = 3,
            palette = ColorMatchStandardPalette,
            uniqueColorsOnly = false,
            compact = true,
            discScale = 0.58f,
            poolDiscScale = 0.78f,
        ),
        ColorMatchLevelDef(
            id = "4",
            displayLevel = 4,
            instructionRes = R.string.color_match_instruction_l2,
            gridSize = 3,
            palette = ColorMatchStandardPalette,
            uniqueColorsOnly = false,
            compact = true,
            discScale = 0.52f,
            poolDiscScale = 0.72f,
        ),
    )

    fun buildRound(level: ColorMatchLevelDef, seed: Int = Random.nextInt()): ColorMatchRound {
        val rng = Random(seed)
        val n = level.gridSize * level.gridSize
        val template = if (level.uniqueColorsOnly && n == level.palette.size) {
            level.palette.shuffled(rng)
        } else {
            List(n) { level.palette[rng.nextInt(level.palette.size)] }
        }
        val tokens = template.mapIndexed { index, color ->
            ColorMatchPoolToken(id = index, color = color)
        }.shuffled(rng)
        return ColorMatchRound(template = template, poolTokens = tokens)
    }
}
