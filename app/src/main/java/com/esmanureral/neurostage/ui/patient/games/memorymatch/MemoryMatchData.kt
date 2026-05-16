package com.esmanureral.neurostage.ui.patient.games.memorymatch

import androidx.annotation.StringRes
import com.esmanureral.neurostage.R

enum class MemoryMatchPairMode {
    Identical,
    ImageAndWord,
}

data class MemoryMatchPair(
    val pairId: Int,
    @get:StringRes val emojiRes: Int,
    @get:StringRes val labelRes: Int,
)

data class MemoryMatchLevel(
    val levelNumber: Int,
    @get:StringRes val titleRes: Int,
    val gridColumns: Int,
    val pairs: List<MemoryMatchPair>,
    val pairMode: MemoryMatchPairMode = MemoryMatchPairMode.Identical,
)

sealed interface MemoryMatchCardFace {
    data class Emoji(@get:StringRes val res: Int) : MemoryMatchCardFace
    data class Word(@get:StringRes val res: Int) : MemoryMatchCardFace
}

data class MemoryMatchCard(
    val instanceId: Int,
    val pairId: Int,
    val face: MemoryMatchCardFace,
    @get:StringRes val labelRes: Int,
)

fun buildShuffledDeck(level: MemoryMatchLevel): List<MemoryMatchCard> {
    return level.pairs
        .flatMapIndexed { index, pair ->
            val baseId = index * 2
            when (level.pairMode) {
                MemoryMatchPairMode.Identical -> listOf(
                    card(baseId, pair, MemoryMatchCardFace.Emoji(pair.emojiRes)),
                    card(baseId + 1, pair, MemoryMatchCardFace.Emoji(pair.emojiRes)),
                )

                MemoryMatchPairMode.ImageAndWord -> listOf(
                    card(baseId, pair, MemoryMatchCardFace.Emoji(pair.emojiRes)),
                    card(baseId + 1, pair, MemoryMatchCardFace.Word(pair.labelRes)),
                )
            }
        }
        .shuffled()
}

private fun card(
    instanceId: Int,
    pair: MemoryMatchPair,
    face: MemoryMatchCardFace,
): MemoryMatchCard = MemoryMatchCard(
    instanceId = instanceId,
    pairId = pair.pairId,
    face = face,
    labelRes = pair.labelRes,
)

val memoryMatchLevel1 = MemoryMatchLevel(
    levelNumber = 1,
    titleRes = R.string.memory_match_level_1_title,
    gridColumns = 3,
    pairs = listOf(
        MemoryMatchPair(1, R.string.memory_emoji_apple, R.string.memory_item_apple),
        MemoryMatchPair(2, R.string.memory_emoji_car, R.string.memory_item_car),
        MemoryMatchPair(3, R.string.memory_emoji_house, R.string.memory_item_house),
    ),
)

val memoryMatchLevel2 = MemoryMatchLevel(
    levelNumber = 2,
    titleRes = R.string.memory_match_level_2_title,
    gridColumns = 3,
    pairs = listOf(
        MemoryMatchPair(1, R.string.memory_emoji_apple, R.string.memory_item_apple),
        MemoryMatchPair(2, R.string.memory_emoji_tomato, R.string.memory_item_tomato),
        MemoryMatchPair(3, R.string.memory_emoji_strawberry, R.string.memory_item_strawberry),
        MemoryMatchPair(4, R.string.memory_emoji_cherry, R.string.memory_item_cherry),
        MemoryMatchPair(5, R.string.memory_emoji_peach, R.string.memory_item_peach),
        MemoryMatchPair(6, R.string.memory_emoji_watermelon, R.string.memory_item_watermelon),
    ),
)

val memoryMatchLevel3 = MemoryMatchLevel(
    levelNumber = 3,
    titleRes = R.string.memory_match_level_3_title,
    gridColumns = 4,
    pairs = listOf(
        MemoryMatchPair(1, R.string.memory_emoji_moon_waxing, R.string.memory_item_moon_waxing),
        MemoryMatchPair(2, R.string.memory_emoji_moon_waning, R.string.memory_item_moon_waning),
        MemoryMatchPair(3, R.string.memory_emoji_arrow_ne, R.string.memory_item_arrow_ne),
        MemoryMatchPair(4, R.string.memory_emoji_arrow_nw, R.string.memory_item_arrow_nw),
        MemoryMatchPair(5, R.string.memory_emoji_clock_300, R.string.memory_item_clock_300),
        MemoryMatchPair(6, R.string.memory_emoji_clock_330, R.string.memory_item_clock_330),
        MemoryMatchPair(7, R.string.memory_emoji_clover_4, R.string.memory_item_clover_4),
        MemoryMatchPair(8, R.string.memory_emoji_clover_3, R.string.memory_item_clover_3),
    ),
)

val memoryMatchLevel4 = MemoryMatchLevel(
    levelNumber = 4,
    titleRes = R.string.memory_match_level_4_title,
    gridColumns = 3,
    pairMode = MemoryMatchPairMode.ImageAndWord,
    pairs = listOf(
        MemoryMatchPair(1, R.string.memory_emoji_cat, R.string.memory_item_cat),
        MemoryMatchPair(2, R.string.memory_emoji_dog, R.string.memory_item_dog),
        MemoryMatchPair(3, R.string.memory_emoji_house, R.string.memory_item_house),
        MemoryMatchPair(4, R.string.memory_emoji_apple, R.string.memory_item_apple),
        MemoryMatchPair(5, R.string.memory_emoji_car, R.string.memory_item_car),
        MemoryMatchPair(6, R.string.memory_emoji_book, R.string.memory_item_book),
    ),
)

val memoryMatchAllLevels: List<MemoryMatchLevel> = listOf(
    memoryMatchLevel1,
    memoryMatchLevel2,
    memoryMatchLevel3,
    memoryMatchLevel4,
)