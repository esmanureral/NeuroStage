package com.esmanureral.neurostage.ui.patient.games

import androidx.annotation.StringRes
import com.esmanureral.neurostage.R
import com.esmanureral.neurostage.domain.patient.PatientStage

data class RoutineStep(
    val id: Int,
    @get:StringRes val emojiRes: Int,
    @get:StringRes val labelRes: Int,
    val correctPosition: Int,
)

data class RoutineGame(
    @get:StringRes val titleRes: Int,
    @get:StringRes val emojiRes: Int,
    val steps: List<RoutineStep>,
)

data class MemoryItem(
    val id: Int,
    @get:StringRes val emojiRes: Int,
    @get:StringRes val labelRes: Int,
)

fun itemCountForStage(stageIndex: Int?): Int =
    PatientStage.memoryItemCount(stageIndex)

fun puzzleGridForStage(stageIndex: Int?): Int =
    PatientStage.fallbackPuzzleGridSize(stageIndex)

val routineGames: List<RoutineGame> = listOf(
    RoutineGame(
        titleRes = R.string.routine_game_tea,
        emojiRes = R.string.routine_emoji_tea_game,
        steps = listOf(
            RoutineStep(1, R.string.routine_emoji_tea_pot, R.string.routine_step_tea_pot, 1),
            RoutineStep(2, R.string.routine_emoji_tea_water, R.string.routine_step_tea_water, 2),
            RoutineStep(3, R.string.routine_emoji_tea_stove, R.string.routine_step_tea_stove, 3),
            RoutineStep(4, R.string.routine_emoji_tea_brew, R.string.routine_step_tea_brew, 4),
        ),
    ),
    RoutineGame(
        titleRes = R.string.routine_game_plant,
        emojiRes = R.string.routine_emoji_plant_game,
        steps = listOf(
            RoutineStep(5, R.string.routine_emoji_plant_tap, R.string.routine_step_plant_tap, 1),
            RoutineStep(
                6,
                R.string.routine_emoji_plant_bucket,
                R.string.routine_step_plant_bucket,
                2
            ),
            RoutineStep(7, R.string.routine_emoji_plant_go, R.string.routine_step_plant_go, 3),
            RoutineStep(
                8,
                R.string.routine_emoji_plant_water,
                R.string.routine_step_plant_water,
                4
            ),
        ),
    ),
    RoutineGame(
        titleRes = R.string.routine_game_wash,
        emojiRes = R.string.routine_emoji_wash_game,
        steps = listOf(
            RoutineStep(9, R.string.routine_emoji_wash_tap, R.string.routine_step_wash_tap, 1),
            RoutineStep(10, R.string.routine_emoji_wash_soap, R.string.routine_step_wash_soap, 2),
            RoutineStep(11, R.string.routine_emoji_wash_rinse, R.string.routine_step_wash_rinse, 3),
            RoutineStep(12, R.string.routine_emoji_wash_dry, R.string.routine_step_wash_dry, 4),
        ),
    ),
)

val memoryItemPool: List<MemoryItem> = listOf(
    MemoryItem(101, R.string.memory_emoji_apple, R.string.memory_item_apple),
    MemoryItem(102, R.string.memory_emoji_dog, R.string.memory_item_dog),
    MemoryItem(103, R.string.memory_emoji_pencil, R.string.memory_item_pencil),
    MemoryItem(104, R.string.memory_emoji_book, R.string.memory_item_book),
    MemoryItem(105, R.string.memory_emoji_rose, R.string.memory_item_rose),
    MemoryItem(106, R.string.memory_emoji_guitar, R.string.memory_item_guitar),
    MemoryItem(107, R.string.memory_emoji_star, R.string.memory_item_star),
    MemoryItem(108, R.string.memory_emoji_car, R.string.memory_item_car),
    MemoryItem(109, R.string.memory_emoji_rainbow, R.string.memory_item_rainbow),
    MemoryItem(110, R.string.memory_emoji_paint, R.string.memory_item_paint),
    MemoryItem(111, R.string.memory_emoji_house, R.string.memory_item_house),
    MemoryItem(112, R.string.memory_emoji_moon, R.string.memory_item_moon),
)
