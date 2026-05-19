package com.esmanureral.neurostage.domain.patient.memorymatch.moderate

import com.esmanureral.neurostage.R
import com.esmanureral.neurostage.ui.patient.games.memorymatch.MemoryMatchLayoutMode
import com.esmanureral.neurostage.ui.patient.games.memorymatch.MemoryMatchLevel
import com.esmanureral.neurostage.ui.patient.games.memorymatch.MemoryMatchLevelProfile
import com.esmanureral.neurostage.ui.patient.games.memorymatch.MemoryMatchPair
import com.esmanureral.neurostage.ui.patient.games.memorymatch.MemoryMatchUiStyle

object ModerateMemoryMatchCatalog {
    const val MAX_LEVEL_INDEX = 3

    val levels: List<MemoryMatchLevel> = listOf(
        MemoryMatchLevel(
            levelNumber = 1,
            titleRes = R.string.memory_match_moderate_level_1_title,
            gridColumns = 2,
            profile = MemoryMatchLevelProfile.HighContrastBasic,
            uiStyle = MemoryMatchUiStyle.Minimal,
            pairs = listOf(
                MemoryMatchPair(1, R.string.memory_emoji_sun, R.string.memory_item_sun),
                MemoryMatchPair(2, R.string.memory_emoji_umbrella, R.string.memory_item_umbrella),
            ),
        ),
        MemoryMatchLevel(
            levelNumber = 2,
            titleRes = R.string.memory_match_moderate_level_2_title,
            gridColumns = 2,
            profile = MemoryMatchLevelProfile.PrimaryColorsSpacious,
            uiStyle = MemoryMatchUiStyle.Minimal,
            pairs = listOf(
                MemoryMatchPair(1, R.string.memory_emoji_apple, R.string.memory_item_apple),
                MemoryMatchPair(2, R.string.memory_emoji_umbrella, R.string.memory_item_umbrella),
                MemoryMatchPair(3, R.string.memory_emoji_star, R.string.memory_item_star),
            ),
        ),
        MemoryMatchLevel(
            levelNumber = 3,
            titleRes = R.string.memory_match_moderate_level_3_title,
            gridColumns = 3,
            profile = MemoryMatchLevelProfile.ModerateGridCompact,
            uiStyle = MemoryMatchUiStyle.Minimal,
            pairs = listOf(
                MemoryMatchPair(1, R.string.memory_emoji_house, R.string.memory_item_house),
                MemoryMatchPair(2, R.string.memory_emoji_car, R.string.memory_item_car),
                MemoryMatchPair(3, R.string.memory_emoji_dog, R.string.memory_item_dog),
                MemoryMatchPair(4, R.string.memory_emoji_cat, R.string.memory_item_cat),
                MemoryMatchPair(5, R.string.memory_emoji_apple, R.string.memory_item_apple),
                MemoryMatchPair(6, R.string.memory_emoji_star, R.string.memory_item_star),
            ),
        ),
        MemoryMatchLevel(
            levelNumber = 4,
            titleRes = R.string.memory_match_moderate_level_4_title,
            gridColumns = 3,
            layoutMode = MemoryMatchLayoutMode.OpenClosed,
            profile = MemoryMatchLevelProfile.OpenClosedAnchors,
            uiStyle = MemoryMatchUiStyle.Minimal,
            pairs = listOf(
                MemoryMatchPair(1, R.string.memory_emoji_sun, R.string.memory_item_sun),
                MemoryMatchPair(2, R.string.memory_emoji_apple, R.string.memory_item_apple),
                MemoryMatchPair(3, R.string.memory_emoji_car, R.string.memory_item_car),
            ),
        ),
    )
}