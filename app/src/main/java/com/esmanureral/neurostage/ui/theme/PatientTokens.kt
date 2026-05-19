package com.esmanureral.neurostage.ui.theme

import androidx.annotation.DimenRes
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.esmanureral.neurostage.R


object PatientColors {
    val background: Color
        @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.background
    val surface: Color
        @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.surface
    val textPrimary: Color
        @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.onSurface
    val textSecondary: Color
        @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.onSurfaceVariant
    val divider: Color
        @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.outline
    val primary: Color
        @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.primary
    val primaryLight: Color
        @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.primaryContainer

    val mildAccent: Color
        @Composable @ReadOnlyComposable get() = colorResource(R.color.patient_mild_accent)
    val mildBackground: Color
        @Composable @ReadOnlyComposable get() = colorResource(R.color.patient_mild_background)
    val mildBodyText: Color
        @Composable @ReadOnlyComposable get() = colorResource(R.color.patient_mild_body_text)
    val puzzleCardBackground: Color
        @Composable @ReadOnlyComposable get() = colorResource(R.color.patient_puzzle_card_background)
    val puzzleCardText: Color
        @Composable @ReadOnlyComposable get() = colorResource(R.color.patient_puzzle_card_text)
    val routineCardBackground: Color
        @Composable @ReadOnlyComposable get() = colorResource(R.color.patient_routine_card_background)
    val routineCardText: Color
        @Composable @ReadOnlyComposable get() = colorResource(R.color.patient_routine_card_text)
    val memoryCardBackground: Color
        @Composable @ReadOnlyComposable get() = colorResource(R.color.patient_memory_card_background)
    val memoryCardText: Color
        @Composable @ReadOnlyComposable get() = colorResource(R.color.patient_memory_card_text)
    val matchCardBackground: Color
        @Composable @ReadOnlyComposable get() = colorResource(R.color.patient_match_card_background)
    val matchCardText: Color
        @Composable @ReadOnlyComposable get() = colorResource(R.color.patient_match_card_text)
    val hubRoutineCardBg: Color
        @Composable @ReadOnlyComposable get() = colorResource(R.color.patient_hub_routine_card_bg)
    val hubRoutineTitle: Color
        @Composable @ReadOnlyComposable get() = colorResource(R.color.patient_hub_routine_title)
    val hubRoutineButton: Color
        @Composable @ReadOnlyComposable get() = colorResource(R.color.patient_hub_routine_button)
    val hubRoutineButtonText: Color
        @Composable @ReadOnlyComposable get() = colorResource(R.color.patient_hub_routine_button_text)
    val hubMemoryCardBg: Color
        @Composable @ReadOnlyComposable get() = colorResource(R.color.patient_hub_memory_card_bg)
    val hubMemoryTitle: Color
        @Composable @ReadOnlyComposable get() = colorResource(R.color.patient_hub_memory_title)
    val hubMemoryButton: Color
        @Composable @ReadOnlyComposable get() = colorResource(R.color.patient_hub_memory_button)
    val hubMemoryButtonText: Color
        @Composable @ReadOnlyComposable get() = colorResource(R.color.patient_hub_memory_button_text)
    val hubMatchCardBg: Color
        @Composable @ReadOnlyComposable get() = colorResource(R.color.patient_hub_match_card_bg)
    val hubMatchTitle: Color
        @Composable @ReadOnlyComposable get() = colorResource(R.color.patient_hub_match_title)
    val hubMatchButton: Color
        @Composable @ReadOnlyComposable get() = colorResource(R.color.patient_hub_match_button)
    val hubMatchButtonText: Color
        @Composable @ReadOnlyComposable get() = colorResource(R.color.patient_hub_match_button_text)
    val hubColorMatchCardBg: Color
        @Composable @ReadOnlyComposable get() = colorResource(R.color.patient_hub_color_match_card_bg)
    val hubColorMatchTitle: Color
        @Composable @ReadOnlyComposable get() = colorResource(R.color.patient_hub_color_match_title)
    val hubColorMatchButton: Color
        @Composable @ReadOnlyComposable get() = colorResource(R.color.patient_hub_color_match_button)
    val hubColorMatchButtonText: Color
        @Composable @ReadOnlyComposable get() = colorResource(R.color.patient_hub_color_match_button_text)
    val hubPuzzleCardBg: Color
        @Composable @ReadOnlyComposable get() = colorResource(R.color.patient_hub_puzzle_card_bg)
    val hubPuzzleTitle: Color
        @Composable @ReadOnlyComposable get() = colorResource(R.color.patient_hub_puzzle_title)
    val hubPuzzleButton: Color
        @Composable @ReadOnlyComposable get() = colorResource(R.color.patient_hub_puzzle_button)
    val hubPuzzleButtonText: Color
        @Composable @ReadOnlyComposable get() = colorResource(R.color.patient_hub_puzzle_button_text)
    val matchAccent: Color
        @Composable @ReadOnlyComposable get() = colorResource(R.color.patient_match_accent)
    val matchAccentLight: Color
        @Composable @ReadOnlyComposable get() = colorResource(R.color.patient_match_accent_light)
    val matchBoardBackground: Color
        @Composable @ReadOnlyComposable get() = colorResource(R.color.patient_match_board_background)
    val matchCardFace: Color
        @Composable @ReadOnlyComposable get() = colorResource(R.color.patient_match_card_face)
    val matchCardBack: Color
        @Composable @ReadOnlyComposable get() = colorResource(R.color.patient_match_card_back)
    val matchCardBackBorder: Color
        @Composable @ReadOnlyComposable get() = colorResource(R.color.patient_match_card_back_border)
    val matchMatchedBackground: Color
        @Composable @ReadOnlyComposable get() = colorResource(R.color.patient_match_matched_background)
    val matchMatchedBorder: Color
        @Composable @ReadOnlyComposable get() = colorResource(R.color.patient_match_matched_border)
    val matchMismatchBackground: Color
        @Composable @ReadOnlyComposable get() = colorResource(R.color.patient_match_mismatch_background)
    val matchMismatchBorder: Color
        @Composable @ReadOnlyComposable get() = colorResource(R.color.patient_match_mismatch_border)
    val moderateNoticeBackground: Color
        @Composable @ReadOnlyComposable get() = colorResource(R.color.patient_moderate_notice_background)
    val moderateNoticeTitle: Color
        @Composable @ReadOnlyComposable get() = colorResource(R.color.patient_moderate_notice_title_color)
    val moderateNoticeBody: Color
        @Composable @ReadOnlyComposable get() = colorResource(R.color.patient_moderate_notice_body_color)
    val guidanceTextTitle: Color
        @Composable @ReadOnlyComposable get() = colorResource(R.color.patient_guidance_text_title)
    val guidanceTextBody: Color
        @Composable @ReadOnlyComposable get() = colorResource(R.color.patient_guidance_text_body)
    val guidanceOnButton: Color
        @Composable @ReadOnlyComposable get() = colorResource(R.color.patient_on_surface)
    val gameBackgroundCream: Color
        @Composable @ReadOnlyComposable get() = colorResource(R.color.patient_game_background_cream)
    val gameTextPrimary: Color
        @Composable @ReadOnlyComposable get() = colorResource(R.color.patient_game_text_primary)
    val gameTextMuted: Color
        @Composable @ReadOnlyComposable get() = colorResource(R.color.patient_game_text_muted)
    val gameSuccess: Color
        @Composable @ReadOnlyComposable get() = colorResource(R.color.patient_game_success)
    val gameSuccessLight: Color
        @Composable @ReadOnlyComposable get() = colorResource(R.color.patient_game_success_light)
    val gameWarningBackground: Color
        @Composable @ReadOnlyComposable get() = colorResource(R.color.patient_game_warning_background)
    val gameWarningText: Color
        @Composable @ReadOnlyComposable get() = colorResource(R.color.patient_game_warning_text)
    val memoryAccent: Color
        @Composable @ReadOnlyComposable get() = colorResource(R.color.patient_memory_accent)
    val memoryAccentLight: Color
        @Composable @ReadOnlyComposable get() = colorResource(R.color.patient_memory_accent_light)
    val routineAccent: Color
        @Composable @ReadOnlyComposable get() = colorResource(R.color.patient_routine_accent)
    val routineAccentLight: Color
        @Composable @ReadOnlyComposable get() = colorResource(R.color.patient_routine_accent_light)
    val puzzleBackground: Color
        @Composable @ReadOnlyComposable get() = colorResource(R.color.patient_puzzle_background)
    val puzzleBoardBackground: Color
        @Composable @ReadOnlyComposable get() = colorResource(R.color.patient_puzzle_board_background)
    val puzzleSlot: Color
        @Composable @ReadOnlyComposable get() = colorResource(R.color.patient_puzzle_slot)
    val puzzleAccent: Color
        @Composable @ReadOnlyComposable get() = colorResource(R.color.patient_puzzle_accent)

    val puzzleSuccess: Color
        @Composable @ReadOnlyComposable get() = colorResource(R.color.patient_puzzle_success)
    val puzzleTextPrimary: Color
        @Composable @ReadOnlyComposable get() = colorResource(R.color.patient_puzzle_text_primary)
    val puzzleTextSecondary: Color
        @Composable @ReadOnlyComposable get() = colorResource(R.color.patient_puzzle_text_secondary)
    val puzzleSlotStroke: Color
        @Composable @ReadOnlyComposable get() = colorResource(R.color.patient_puzzle_slot_stroke)
    val puzzleMagnetStroke: Color
        @Composable @ReadOnlyComposable get() = colorResource(R.color.patient_puzzle_magnet_stroke)

}

object PatientDimens {
    val homeScreenHorizontalPadding: Dp
        @Composable @ReadOnlyComposable get() = dimensionResource(R.dimen.patient_home_screen_h_padding)
    val homeScreenVerticalPadding: Dp
        @Composable @ReadOnlyComposable get() = dimensionResource(R.dimen.patient_home_screen_v_padding)
    val homeSectionSpacing: Dp
        @Composable @ReadOnlyComposable get() = dimensionResource(R.dimen.patient_home_section_spacing)
    val homeDisclaimerSpacing: Dp
        @Composable @ReadOnlyComposable get() = dimensionResource(R.dimen.patient_home_disclaimer_spacing)
    val homeBottomSpacing: Dp
        @Composable @ReadOnlyComposable get() = dimensionResource(R.dimen.patient_home_bottom_spacing)
    val homeHeaderCorner: Dp
        @Composable @ReadOnlyComposable get() = dimensionResource(R.dimen.patient_home_header_corner)
    val homeNoticeCorner: Dp
        @Composable @ReadOnlyComposable get() = dimensionResource(R.dimen.patient_home_notice_corner)
    val homeCardCorner: Dp
        @Composable @ReadOnlyComposable get() = dimensionResource(R.dimen.patient_home_card_corner)
    val homeActionCardCorner: Dp
        @Composable @ReadOnlyComposable get() = dimensionResource(R.dimen.patient_home_action_card_corner)
    val homeExerciseRowGap: Dp
        @Composable @ReadOnlyComposable get() = dimensionResource(R.dimen.patient_home_exercise_row_gap)
    val homeAppBarTitleSize: TextUnit
        @Composable @ReadOnlyComposable get() = textDimen(R.dimen.patient_home_appbar_title_size)
    val homeChipTextSize: TextUnit
        @Composable @ReadOnlyComposable get() = textDimen(R.dimen.patient_home_chip_text_size)
    val homeSubtitleTextSize: TextUnit
        @Composable @ReadOnlyComposable get() = textDimen(R.dimen.patient_home_subtitle_text_size)
    val homeDisclaimerTextSize: TextUnit
        @Composable @ReadOnlyComposable get() = textDimen(R.dimen.patient_home_disclaimer_text_size)
    val homeMildHeaderPadding: Dp
        @Composable @ReadOnlyComposable get() = dimensionResource(R.dimen.patient_home_mild_header_padding)
    val homeMildEmojiSize: TextUnit
        @Composable @ReadOnlyComposable get() = textDimen(R.dimen.patient_home_mild_emoji_size)
    val homeMildTitleGap: Dp
        @Composable @ReadOnlyComposable get() = dimensionResource(R.dimen.patient_home_mild_title_gap)
    val homeMildBodyGap: Dp
        @Composable @ReadOnlyComposable get() = dimensionResource(R.dimen.patient_home_mild_body_gap)
    val homeNoticePadding: Dp
        @Composable @ReadOnlyComposable get() = dimensionResource(R.dimen.patient_home_notice_padding)
    val homeNoticeTitleGap: Dp
        @Composable @ReadOnlyComposable get() = dimensionResource(R.dimen.patient_home_notice_title_gap)
    val homeExerciseCardPadding: Dp
        @Composable @ReadOnlyComposable get() = dimensionResource(R.dimen.patient_home_exercise_card_padding)
    val homeExerciseEmojiSize: TextUnit
        @Composable @ReadOnlyComposable get() = textDimen(R.dimen.patient_home_exercise_emoji_size)
    val homeExerciseEmojiGap: Dp
        @Composable @ReadOnlyComposable get() = dimensionResource(R.dimen.patient_home_exercise_emoji_gap)
    val homeActionIconBoxSize: Dp
        @Composable @ReadOnlyComposable get() = dimensionResource(R.dimen.patient_home_action_icon_box_size)
    val homeActionIconCorner: Dp
        @Composable @ReadOnlyComposable get() = dimensionResource(R.dimen.patient_home_action_icon_corner)
    val homeActionIconSize: Dp
        @Composable @ReadOnlyComposable get() = dimensionResource(R.dimen.patient_home_action_icon_size)
    val homeActionRowPadding: Dp
        @Composable @ReadOnlyComposable get() = dimensionResource(R.dimen.patient_home_action_row_padding)
    val homeActionTextGap: Dp
        @Composable @ReadOnlyComposable get() = dimensionResource(R.dimen.patient_home_action_text_gap)
    val homeActionTitleSize: TextUnit
        @Composable @ReadOnlyComposable get() = textDimen(R.dimen.patient_home_action_title_size)
    val homeActionSubtitleSize: TextUnit
        @Composable @ReadOnlyComposable get() = textDimen(R.dimen.patient_home_action_subtitle_size)
    val homeActionChevronSize: Dp
        @Composable @ReadOnlyComposable get() = dimensionResource(R.dimen.patient_home_action_chevron_size)
    val homeExerciseSectionGap: Dp
        @Composable @ReadOnlyComposable get() = dimensionResource(R.dimen.patient_home_exercise_section_gap)
    val homeIntroBottomGap: Dp
        @Composable @ReadOnlyComposable get() = dimensionResource(R.dimen.patient_home_intro_bottom_gap)
    val homeMildTitleSize: TextUnit
        @Composable @ReadOnlyComposable get() = textDimen(R.dimen.patient_home_mild_title_size)
    val homeMildBodyLineHeight: TextUnit
        @Composable @ReadOnlyComposable get() = textDimen(R.dimen.patient_home_mild_body_line_height)
    val homeNoticeBodyLineHeight: TextUnit
        @Composable @ReadOnlyComposable get() = textDimen(R.dimen.patient_home_notice_body_line_height)
    val homeExerciseSectionTitleSize: TextUnit
        @Composable @ReadOnlyComposable get() = textDimen(R.dimen.patient_home_exercise_section_title_size)
    val homeExerciseHighlightBorder: Dp
        @Composable @ReadOnlyComposable get() = dimensionResource(R.dimen.patient_home_exercise_highlight_border)
    val homeActionCardElevation: Dp
        @Composable @ReadOnlyComposable get() = dimensionResource(R.dimen.patient_home_action_card_elevation)

    val cornerNone: Dp
        @Composable @ReadOnlyComposable get() = dimensionResource(R.dimen.patient_corner_none)
    val exerciseTopBarTitleSize: TextUnit
        @Composable @ReadOnlyComposable get() = textDimen(R.dimen.patient_exercise_topbar_title_size)
    val exerciseTopBarSubtitleSize: TextUnit
        @Composable @ReadOnlyComposable get() = textDimen(R.dimen.patient_exercise_topbar_subtitle_size)
    val exerciseTopBarTitleSoloSize: TextUnit
        @Composable @ReadOnlyComposable get() = textDimen(R.dimen.patient_exercise_topbar_title_solo_size)
    val gameHubScreenPadding: Dp
        @Composable @ReadOnlyComposable get() = dimensionResource(R.dimen.patient_game_hub_screen_padding)
    val gameHubPickTextSize: TextUnit
        @Composable @ReadOnlyComposable get() = textDimen(R.dimen.patient_game_hub_pick_text_size)
    val gameHubSectionGap: Dp
        @Composable @ReadOnlyComposable get() = dimensionResource(R.dimen.patient_game_hub_section_gap)
    val gameHubCardGap: Dp
        @Composable @ReadOnlyComposable get() = dimensionResource(R.dimen.patient_game_hub_card_gap)
    val gameHubCardCorner: Dp
        @Composable @ReadOnlyComposable get() = dimensionResource(R.dimen.patient_game_hub_card_corner)
    val gameHubCardPadding: Dp
        @Composable @ReadOnlyComposable get() = dimensionResource(R.dimen.patient_game_hub_card_padding)
    val gameHubCardEmojiSize: TextUnit
        @Composable @ReadOnlyComposable get() = textDimen(R.dimen.patient_game_hub_card_emoji_size)
    val gameHubCardEmojiGap: Dp
        @Composable @ReadOnlyComposable get() = dimensionResource(R.dimen.patient_game_hub_card_emoji_gap)
    val gameHubCardSubtitleGap: Dp
        @Composable @ReadOnlyComposable get() = dimensionResource(R.dimen.patient_game_hub_card_subtitle_gap)
    val gameHubHeaderTitleSize: TextUnit
        @Composable @ReadOnlyComposable get() = textDimen(R.dimen.patient_game_hub_header_title_size)
    val gameHubHeaderSubtitleGap: Dp
        @Composable @ReadOnlyComposable get() = dimensionResource(R.dimen.patient_game_hub_header_subtitle_gap)
    val gameHubContentTopPadding: Dp
        @Composable @ReadOnlyComposable get() = dimensionResource(R.dimen.patient_game_hub_content_top_padding)
    val gameHubContentBottomPadding: Dp
        @Composable @ReadOnlyComposable get() = dimensionResource(R.dimen.patient_game_hub_content_bottom_padding)
    val gameHubIconBoxSize: Dp
        @Composable @ReadOnlyComposable get() = dimensionResource(R.dimen.patient_game_hub_icon_box_size)
    val gameHubChevronSize: Dp
        @Composable @ReadOnlyComposable get() = dimensionResource(R.dimen.patient_game_hub_chevron_size)
    val gameHubCardElevation: Dp
        @Composable @ReadOnlyComposable get() = dimensionResource(R.dimen.patient_game_hub_card_elevation)
    val gameHubCardBorder: Dp
        @Composable @ReadOnlyComposable get() = dimensionResource(R.dimen.patient_game_hub_card_border)
    val gameHubGreetingSize: TextUnit
        @Composable @ReadOnlyComposable get() = textDimen(R.dimen.patient_game_hub_greeting_size)
    val gameHubGreetingSubtitleSize: TextUnit
        @Composable @ReadOnlyComposable get() = textDimen(R.dimen.patient_game_hub_greeting_subtitle_size)
    val gameHubStageChipCorner: Dp
        @Composable @ReadOnlyComposable get() = dimensionResource(R.dimen.patient_game_hub_stage_chip_corner)
    val gameHubStageChipPaddingH: Dp
        @Composable @ReadOnlyComposable get() = dimensionResource(R.dimen.patient_game_hub_stage_chip_padding_h)
    val gameHubStageChipPaddingV: Dp
        @Composable @ReadOnlyComposable get() = dimensionResource(R.dimen.patient_game_hub_stage_chip_padding_v)
    val gameHubStageChipTextSize: TextUnit
        @Composable @ReadOnlyComposable get() = textDimen(R.dimen.patient_game_hub_stage_chip_text_size)
    val gameHubPlayfulCardHeight: Dp
        @Composable @ReadOnlyComposable get() = dimensionResource(R.dimen.patient_game_hub_playful_card_height)
    val gameHubGridCardHeight: Dp
        @Composable @ReadOnlyComposable get() = dimensionResource(R.dimen.patient_game_hub_grid_card_height)
    val gameHubGridEmojiSize: TextUnit
        @Composable @ReadOnlyComposable get() = textDimen(R.dimen.patient_game_hub_grid_emoji_size)
    val gameHubGridTitleSize: TextUnit
        @Composable @ReadOnlyComposable get() = textDimen(R.dimen.patient_game_hub_grid_title_size)
    val gameHubPlayfulCardCorner: Dp
        @Composable @ReadOnlyComposable get() = dimensionResource(R.dimen.patient_game_hub_playful_card_corner)
    val gameHubPlayfulCardPadding: Dp
        @Composable @ReadOnlyComposable get() = dimensionResource(R.dimen.patient_game_hub_playful_card_padding)
    val gameHubPlayfulContentGap: Dp
        @Composable @ReadOnlyComposable get() = dimensionResource(R.dimen.patient_game_hub_playful_content_gap)
    val gameHubPlayfulTitleSize: TextUnit
        @Composable @ReadOnlyComposable get() = textDimen(R.dimen.patient_game_hub_playful_title_size)
    val gameHubPlayfulTitleLineHeight: TextUnit
        @Composable @ReadOnlyComposable get() = textDimen(R.dimen.patient_game_hub_playful_title_line_height)
    val gameHubPlayfulEmojiGap: Dp
        @Composable @ReadOnlyComposable get() = dimensionResource(R.dimen.patient_game_hub_playful_emoji_gap)
    val gameHubPlayfulEmojiBox: Dp
        @Composable @ReadOnlyComposable get() = dimensionResource(R.dimen.patient_game_hub_playful_emoji_box)
    val gameHubPlayfulEmojiSize: TextUnit
        @Composable @ReadOnlyComposable get() = textDimen(R.dimen.patient_game_hub_playful_emoji_size)
    val gameHubStartButtonCorner: Dp
        @Composable @ReadOnlyComposable get() = dimensionResource(R.dimen.patient_game_hub_start_button_corner)
    val gameHubStartButtonHeight: Dp
        @Composable @ReadOnlyComposable get() = dimensionResource(R.dimen.patient_game_hub_start_button_height)
    val gameHubStartButtonTextSize: TextUnit
        @Composable @ReadOnlyComposable get() = textDimen(R.dimen.patient_game_hub_start_button_text_size)
    val gameHubBottomBarHeight: Dp
        @Composable @ReadOnlyComposable get() = dimensionResource(R.dimen.patient_game_hub_bottom_bar_height)
    val gameHubStageBannerPadding: Dp
        @Composable @ReadOnlyComposable get() = dimensionResource(R.dimen.patient_game_hub_stage_banner_padding)
    val gameHubBottomNavElevation: Dp
        @Composable @ReadOnlyComposable get() = dimensionResource(R.dimen.patient_game_hub_bottom_nav_elevation)
    val guidanceCardCorner: Dp
        @Composable @ReadOnlyComposable get() = dimensionResource(R.dimen.guidance_card_corner)
    val guidanceCardPadding: Dp
        @Composable @ReadOnlyComposable get() = dimensionResource(R.dimen.guidance_card_padding)
    val guidanceCardBorder: Dp
        @Composable @ReadOnlyComposable get() = dimensionResource(R.dimen.guidance_card_border)
    val guidanceContentGap: Dp
        @Composable @ReadOnlyComposable get() = homeExerciseRowGap
    val guidanceHeaderGap: Dp
        @Composable @ReadOnlyComposable get() = homeExerciseRowGap
    val guidanceIconSize: TextUnit
        @Composable @ReadOnlyComposable get() = textDimen(R.dimen.guidance_icon_size)
    val guidanceBodyLineHeight: TextUnit
        @Composable @ReadOnlyComposable get() = textDimen(R.dimen.guidance_body_line_height)
    val guidanceButtonCorner: Dp
        @Composable @ReadOnlyComposable get() = dimensionResource(R.dimen.guidance_button_corner)
    val guidanceButtonVPadding: Dp
        @Composable @ReadOnlyComposable get() = dimensionResource(R.dimen.guidance_button_v_padding)
    val guidanceButtonTopGap: Dp
        @Composable @ReadOnlyComposable get() = homeExerciseRowGap
    val gameScreenPadding: Dp
        @Composable @ReadOnlyComposable get() = dimensionResource(R.dimen.patient_game_screen_padding)
    val gameBottomBarHeight: Dp
        @Composable @ReadOnlyComposable get() = dimensionResource(R.dimen.patient_game_bottom_bar_height)
    val gamePrimaryButtonHeight: Dp
        @Composable @ReadOnlyComposable get() = dimensionResource(R.dimen.patient_game_primary_button_height)
    val gameSecondaryButtonHeight: Dp
        @Composable @ReadOnlyComposable get() = dimensionResource(R.dimen.patient_game_secondary_button_height)
    val gameButtonCorner: Dp
        @Composable @ReadOnlyComposable get() = dimensionResource(R.dimen.patient_game_button_corner)
    val gameInstructionCorner: Dp
        @Composable @ReadOnlyComposable get() = dimensionResource(R.dimen.patient_game_instruction_corner)
    val gameInstructionPadding: Dp
        @Composable @ReadOnlyComposable get() = dimensionResource(R.dimen.patient_game_instruction_padding)
    val gameTitleSize: TextUnit
        @Composable @ReadOnlyComposable get() = textDimen(R.dimen.patient_game_title_size)
    val gameRecallTitleSize: TextUnit
        @Composable @ReadOnlyComposable get() = textDimen(R.dimen.patient_game_recall_title_size)
    val gameInstructionSize: TextUnit
        @Composable @ReadOnlyComposable get() = textDimen(R.dimen.patient_game_instruction_size)
    val gameInstructionLineHeight: TextUnit
        @Composable @ReadOnlyComposable get() = textDimen(R.dimen.patient_game_instruction_line_height)
    val gameButtonTextSize: TextUnit
        @Composable @ReadOnlyComposable get() = textDimen(R.dimen.patient_game_button_text_size)
    val gameCountTextSize: TextUnit
        @Composable @ReadOnlyComposable get() = textDimen(R.dimen.patient_game_count_text_size)
    val gameSuccessEmojiSize: TextUnit
        @Composable @ReadOnlyComposable get() = textDimen(R.dimen.patient_game_success_emoji_size)
    val gameSuccessTitleSize: TextUnit
        @Composable @ReadOnlyComposable get() = textDimen(R.dimen.patient_game_success_title_size)
    val gameSuccessIconSize: TextUnit
        @Composable @ReadOnlyComposable get() = textDimen(R.dimen.patient_game_success_icon_size)
    val gameStepEmojiSize: TextUnit
        @Composable @ReadOnlyComposable get() = textDimen(R.dimen.patient_game_step_emoji_size)
    val gameStepLabelSize: TextUnit
        @Composable @ReadOnlyComposable get() = textDimen(R.dimen.patient_game_step_label_size)
    val gameTryAgainTextSize: TextUnit
        @Composable @ReadOnlyComposable get() = textDimen(R.dimen.patient_game_try_again_text_size)
    val gameBlockGapS: Dp
        @Composable @ReadOnlyComposable get() = dimensionResource(R.dimen.patient_game_block_gap_s)
    val gameBlockGapM: Dp
        @Composable @ReadOnlyComposable get() = dimensionResource(R.dimen.patient_game_block_gap_m)
    val gameBlockGapL: Dp
        @Composable @ReadOnlyComposable get() = dimensionResource(R.dimen.patient_game_block_gap_l)
    val gameBlockGapXl: Dp
        @Composable @ReadOnlyComposable get() = dimensionResource(R.dimen.patient_game_block_gap_xl)
    val gameGridGap: Dp
        @Composable @ReadOnlyComposable get() = dimensionResource(R.dimen.patient_game_grid_gap)
    val gameGridGapS: Dp
        @Composable @ReadOnlyComposable get() = dimensionResource(R.dimen.patient_game_grid_gap_s)
    val memoryMatchBoardCorner: Dp
        @Composable @ReadOnlyComposable get() = dimensionResource(R.dimen.patient_memory_match_board_corner)
    val memoryMatchBoardPadding: Dp
        @Composable @ReadOnlyComposable get() = dimensionResource(R.dimen.patient_memory_match_board_padding)
    val memoryMatchCardCorner: Dp
        @Composable @ReadOnlyComposable get() = dimensionResource(R.dimen.patient_memory_match_card_corner)
    val memoryMatchCardEmojiSize: TextUnit
        @Composable @ReadOnlyComposable get() = textDimen(R.dimen.patient_memory_match_card_emoji_size)
    val memoryMatchCardEmojiSizeDense: TextUnit
        @Composable @ReadOnlyComposable get() = textDimen(R.dimen.patient_memory_match_card_emoji_size_dense)
    val memoryMatchCardWordSize: TextUnit
        @Composable @ReadOnlyComposable get() = textDimen(R.dimen.patient_memory_match_card_word_size)
    val memoryMatchCardBorder: Dp
        @Composable @ReadOnlyComposable get() = dimensionResource(R.dimen.patient_memory_match_card_border)
    val memoryMatchModerateBoardPadding: Dp
        @Composable @ReadOnlyComposable get() = dimensionResource(R.dimen.patient_memory_match_moderate_board_padding)
    val memoryMatchModerateCardEmojiSize: TextUnit
        @Composable @ReadOnlyComposable get() = textDimen(R.dimen.patient_memory_match_moderate_card_emoji_size)
    val memoryMatchModerateGridGap: Dp
        @Composable @ReadOnlyComposable get() = dimensionResource(R.dimen.patient_memory_match_moderate_grid_gap)
    val gameTryAgainCorner: Dp
        @Composable @ReadOnlyComposable get() = dimensionResource(R.dimen.patient_game_try_again_corner)
    val gameTryAgainPadding: Dp
        @Composable @ReadOnlyComposable get() = dimensionResource(R.dimen.patient_game_try_again_padding)
    val gameResultPadding: Dp
        @Composable @ReadOnlyComposable get() = dimensionResource(R.dimen.patient_game_result_padding)
    val gameItemCardPaddingV: Dp
        @Composable @ReadOnlyComposable get() = dimensionResource(R.dimen.patient_game_item_card_padding_v)
    val gameItemLabelGap: Dp
        @Composable @ReadOnlyComposable get() = dimensionResource(R.dimen.patient_game_item_label_gap)
    val gameItemLabelSize: TextUnit
        @Composable @ReadOnlyComposable get() = textDimen(R.dimen.patient_game_item_label_size)
    val gameStepRowGapV: Dp
        @Composable @ReadOnlyComposable get() = dimensionResource(R.dimen.patient_game_step_row_gap_v)
    val gameStepRowPaddingH: Dp
        @Composable @ReadOnlyComposable get() = dimensionResource(R.dimen.patient_game_step_row_padding_h)
    val gameStepRowPaddingV: Dp
        @Composable @ReadOnlyComposable get() = dimensionResource(R.dimen.patient_game_step_row_padding_v)
    val gameStepBadgeSize: Dp
        @Composable @ReadOnlyComposable get() = dimensionResource(R.dimen.patient_game_step_badge_size)
    val gameStepIconGap: Dp
        @Composable @ReadOnlyComposable get() = dimensionResource(R.dimen.patient_game_step_icon_gap)
    val gameStepEmojiGap: Dp
        @Composable @ReadOnlyComposable get() = dimensionResource(R.dimen.patient_game_step_emoji_gap)
    val gameSuccessSectionGap: Dp
        @Composable @ReadOnlyComposable get() = dimensionResource(R.dimen.patient_game_success_section_gap)
    val gameSuccessTitleGap: Dp
        @Composable @ReadOnlyComposable get() = dimensionResource(R.dimen.patient_game_success_title_gap)
    val gameSuccessActionsGap: Dp
        @Composable @ReadOnlyComposable get() = dimensionResource(R.dimen.patient_game_success_actions_gap)
    val gameSuccessActionsTopGap: Dp
        @Composable @ReadOnlyComposable get() = dimensionResource(R.dimen.patient_game_success_actions_top_gap)
    val gameSuccessBodyLineHeight: TextUnit
        @Composable @ReadOnlyComposable get() = textDimen(R.dimen.patient_game_success_body_line_height)
    val gameResultEmojiGap: Dp
        @Composable @ReadOnlyComposable get() = dimensionResource(R.dimen.patient_game_result_emoji_gap)
    val gameBackIconSize: Dp
        @Composable @ReadOnlyComposable get() = dimensionResource(R.dimen.patient_game_back_icon_size)
    val gameBackIconGap: Dp
        @Composable @ReadOnlyComposable get() = dimensionResource(R.dimen.patient_game_back_icon_gap)
    val puzzleTrayGap: Dp
        @Composable @ReadOnlyComposable get() = dimensionResource(R.dimen.puzzle_tray_gap)
    val puzzleTrayMinWidthFour: Dp
        @Composable @ReadOnlyComposable get() = dimensionResource(R.dimen.puzzle_tray_min_width_four)
    val puzzleTrayMinWidthSixPlus: Dp
        @Composable @ReadOnlyComposable get() = dimensionResource(R.dimen.puzzle_tray_min_width_six_plus)
    val puzzleTrayMaxHeight: Dp
        @Composable @ReadOnlyComposable get() = dimensionResource(R.dimen.puzzle_tray_max_height)
    val puzzleTrayMinHeightFourPieces: Dp
        @Composable @ReadOnlyComposable get() = dimensionResource(R.dimen.puzzle_tray_min_height_4)
    val puzzleTrayMinHeightSixPieces: Dp
        @Composable @ReadOnlyComposable get() = dimensionResource(R.dimen.puzzle_tray_min_height_6)
    val puzzleTrayMinHeightNinePieces: Dp
        @Composable @ReadOnlyComposable get() = dimensionResource(R.dimen.puzzle_tray_min_height_9)
    val puzzleScreenPaddingH: Dp
        @Composable @ReadOnlyComposable get() = dimensionResource(R.dimen.puzzle_screen_h_padding)
    val puzzleScreenPaddingV: Dp
        @Composable @ReadOnlyComposable get() = dimensionResource(R.dimen.puzzle_screen_v_padding)
    val puzzleBoardCorner: Dp
        @Composable @ReadOnlyComposable get() = dimensionResource(R.dimen.puzzle_board_corner)
    val puzzleBoardBorder: Dp
        @Composable @ReadOnlyComposable get() = dimensionResource(R.dimen.puzzle_board_border)

    val puzzleTrayCorner: Dp
        @Composable @ReadOnlyComposable get() = dimensionResource(R.dimen.puzzle_tray_corner)
    val puzzleTrayPaddingH: Dp
        @Composable @ReadOnlyComposable get() = dimensionResource(R.dimen.puzzle_tray_padding_h)
    val puzzleTrayPaddingV: Dp
        @Composable @ReadOnlyComposable get() = dimensionResource(R.dimen.puzzle_tray_padding_v)
    val puzzleStatusTextSize: TextUnit
        @Composable @ReadOnlyComposable get() = textDimen(R.dimen.puzzle_status_text_size)
    val puzzleSuccessCorner: Dp
        @Composable @ReadOnlyComposable get() = dimensionResource(R.dimen.puzzle_success_corner)
    val puzzleSuccessBorder: Dp
        @Composable @ReadOnlyComposable get() = dimensionResource(R.dimen.puzzle_success_border)
    val puzzleSuccessPadding: Dp
        @Composable @ReadOnlyComposable get() = dimensionResource(R.dimen.puzzle_success_padding)
    val puzzleSuccessTitleGap: Dp
        @Composable @ReadOnlyComposable get() = dimensionResource(R.dimen.puzzle_success_title_gap)
    val puzzleSuccessButtonGap: Dp
        @Composable @ReadOnlyComposable get() = dimensionResource(R.dimen.puzzle_success_button_gap)
    val puzzleSuccessButtonCorner: Dp
        @Composable @ReadOnlyComposable get() = dimensionResource(R.dimen.puzzle_success_button_corner)
    val puzzleSuccessButtonHeight: Dp
        @Composable @ReadOnlyComposable get() = dimensionResource(R.dimen.puzzle_success_button_height)
    val puzzleSuccessSecondaryGap: Dp
        @Composable @ReadOnlyComposable get() = dimensionResource(R.dimen.puzzle_success_secondary_gap)
    val puzzleSuccessTitleSize: TextUnit
        @Composable @ReadOnlyComposable get() = textDimen(R.dimen.puzzle_success_title_size)
    val puzzleSuccessPrimaryButtonTextSize: TextUnit
        @Composable @ReadOnlyComposable get() = textDimen(R.dimen.puzzle_success_primary_button_text_size)
    val puzzleSuccessSecondaryButtonTextSize: TextUnit
        @Composable @ReadOnlyComposable get() = textDimen(R.dimen.puzzle_success_secondary_button_text_size)
    val puzzleTraySectionGap: Dp
        @Composable @ReadOnlyComposable get() = dimensionResource(R.dimen.puzzle_tray_section_gap)
    val puzzleTrayLabelBottomGap: Dp
        @Composable @ReadOnlyComposable get() = dimensionResource(R.dimen.puzzle_tray_label_bottom_gap)
    val puzzleDragFallbackSize: Dp
        @Composable @ReadOnlyComposable get() = dimensionResource(R.dimen.puzzle_drag_fallback_size)
    val puzzleTopBarTitleSize: TextUnit
        @Composable @ReadOnlyComposable get() = textDimen(R.dimen.puzzle_topbar_title_size)
    val puzzleTopBarSubtitleSize: TextUnit
        @Composable @ReadOnlyComposable get() = textDimen(R.dimen.puzzle_topbar_subtitle_size)
    val puzzleSlotStrokeNormal: Dp
        @Composable @ReadOnlyComposable get() = dimensionResource(R.dimen.puzzle_slot_stroke_normal)
    val puzzleSlotStrokeMagnet: Dp
        @Composable @ReadOnlyComposable get() = dimensionResource(R.dimen.puzzle_slot_stroke_magnet)
    val puzzlePieceBorderWidth: Dp
        @Composable @ReadOnlyComposable get() = dimensionResource(R.dimen.puzzle_piece_border_width)

    @Composable
    @ReadOnlyComposable
    private fun textDimen(@DimenRes id: Int): TextUnit = dimensionResource(id).value.sp
}
