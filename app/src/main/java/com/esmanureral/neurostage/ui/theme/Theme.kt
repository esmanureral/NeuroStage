package com.esmanureral.neurostage.ui.theme

import androidx.annotation.DimenRes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.esmanureral.neurostage.R

private val NeuroColorScheme = darkColorScheme(
    primary = NsRose,
    onPrimary = NsWhite,
    primaryContainer = NsCard,
    onPrimaryContainer = NsWhite,
    secondary = NsLavender,
    onSecondary = NsWhite,
    background = NsBgTop,
    onBackground = NsWhite,
    surface = NsCard,
    onSurface = NsWhite,
    surfaceVariant = NsPanel,
    onSurfaceVariant = NsTextMid,
    outline = NsDivider,
    error = NsCoral,
    onError = NsWhite,
)

@Composable
fun NeuroStageTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = NeuroColorScheme,
        typography = Typography,
        content = content,
    )
}

object ScanColors {
    val resultBackground: Color
        @Composable @ReadOnlyComposable get() = colorResource(R.color.scan_result_background)
    val xaiLoadingBackground: Color
        @Composable @ReadOnlyComposable get() = colorResource(R.color.scan_xai_loading_background)
    val xaiLoadingBorder: Color
        @Composable @ReadOnlyComposable get() = colorResource(R.color.scan_xai_loading_border)
    val xaiLoadingAccent: Color
        @Composable @ReadOnlyComposable get() = colorResource(R.color.scan_xai_loading_accent)
    val xaiLoadingTitle: Color
        @Composable @ReadOnlyComposable get() = colorResource(R.color.scan_xai_loading_title)
    val xaiLoadingSubtitle: Color
        @Composable @ReadOnlyComposable get() = colorResource(R.color.scan_xai_loading_subtitle)
    val xaiSummaryAccent: Color
        @Composable @ReadOnlyComposable get() = colorResource(R.color.scan_xai_summary_accent)
    val scoreBarTrack: Color
        @Composable @ReadOnlyComposable get() = colorResource(R.color.scan_score_bar_track)
    val mriLabelScrim: Color
        @Composable @ReadOnlyComposable get() = colorResource(R.color.scan_mri_label_scrim)
}

object ScanDimens {
    val waveDepth: Dp
        @Composable @ReadOnlyComposable get() = dimensionResource(R.dimen.scan_wave_depth)
    val headerHorizontalPadding: Dp
        @Composable @ReadOnlyComposable get() = dimensionResource(R.dimen.scan_header_h_padding)
    val headerVerticalPadding: Dp
        @Composable @ReadOnlyComposable get() = dimensionResource(R.dimen.scan_header_v_padding)
    val backButtonOffset: Dp
        @Composable @ReadOnlyComposable get() = dimensionResource(R.dimen.scan_back_button_offset)
    val headerTitleGap: Dp
        @Composable @ReadOnlyComposable get() = dimensionResource(R.dimen.scan_header_title_gap)
    val headerContentGap: Dp
        @Composable @ReadOnlyComposable get() = dimensionResource(R.dimen.scan_header_content_gap)
    val sectionGapL: Dp
        @Composable @ReadOnlyComposable get() = dimensionResource(R.dimen.scan_section_gap_l)
    val contentHorizontalPadding: Dp
        @Composable @ReadOnlyComposable get() = dimensionResource(R.dimen.scan_content_h_padding)
    val uploadCardCorner: Dp
        @Composable @ReadOnlyComposable get() = dimensionResource(R.dimen.scan_upload_card_corner)
    val uploadBorderWidth: Dp
        @Composable @ReadOnlyComposable get() = dimensionResource(R.dimen.scan_upload_border_width)
    val uploadZoneGap: Dp
        @Composable @ReadOnlyComposable get() = dimensionResource(R.dimen.scan_upload_zone_gap)
    val uploadIconCircle: Dp
        @Composable @ReadOnlyComposable get() = dimensionResource(R.dimen.scan_upload_icon_circle)
    val uploadIconSize: Dp
        @Composable @ReadOnlyComposable get() = dimensionResource(R.dimen.scan_upload_icon_size)
    val errorHorizontalPadding: Dp
        @Composable @ReadOnlyComposable get() = dimensionResource(R.dimen.scan_error_h_padding)
    val errorVerticalPadding: Dp
        @Composable @ReadOnlyComposable get() = dimensionResource(R.dimen.scan_error_v_padding)
    val pickerSectionGap: Dp
        @Composable @ReadOnlyComposable get() = dimensionResource(R.dimen.scan_picker_section_gap)
    val pickerRowGap: Dp
        @Composable @ReadOnlyComposable get() = dimensionResource(R.dimen.scan_picker_row_gap)
    val pickerCorner: Dp
        @Composable @ReadOnlyComposable get() = dimensionResource(R.dimen.scan_picker_corner)
    val pickerButtonVerticalPadding: Dp
        @Composable @ReadOnlyComposable get() = dimensionResource(R.dimen.scan_picker_button_v_padding)
    val pickerIconGap: Dp
        @Composable @ReadOnlyComposable get() = dimensionResource(R.dimen.scan_picker_icon_gap)
    val pickerIconSize: Dp
        @Composable @ReadOnlyComposable get() = dimensionResource(R.dimen.scan_picker_icon_size)
    val disclaimerTop: Dp
        @Composable @ReadOnlyComposable get() = dimensionResource(R.dimen.scan_disclaimer_top)
    val disclaimerBottom: Dp
        @Composable @ReadOnlyComposable get() = dimensionResource(R.dimen.scan_disclaimer_bottom)
    val toolbarHorizontalPadding: Dp
        @Composable @ReadOnlyComposable get() = dimensionResource(R.dimen.scan_toolbar_h_padding)
    val toolbarVerticalPadding: Dp
        @Composable @ReadOnlyComposable get() = dimensionResource(R.dimen.scan_toolbar_v_padding)
    val toolbarTitleStart: Dp
        @Composable @ReadOnlyComposable get() = dimensionResource(R.dimen.scan_toolbar_title_start)
    val previewImageHorizontalPadding: Dp
        @Composable @ReadOnlyComposable get() = dimensionResource(R.dimen.scan_preview_image_h_padding)
    val previewImageVerticalPadding: Dp
        @Composable @ReadOnlyComposable get() = dimensionResource(R.dimen.scan_preview_image_v_padding)
    val previewImageCorner: Dp
        @Composable @ReadOnlyComposable get() = dimensionResource(R.dimen.scan_preview_image_corner)
    val previewImageInnerCorner: Dp
        @Composable @ReadOnlyComposable get() = dimensionResource(R.dimen.scan_preview_image_inner_corner)
    val previewErrorHorizontalPadding: Dp
        @Composable @ReadOnlyComposable get() = dimensionResource(R.dimen.scan_preview_error_h_padding)
    val previewErrorVerticalPadding: Dp
        @Composable @ReadOnlyComposable get() = dimensionResource(R.dimen.scan_preview_error_v_padding)
    val sheetTopCorner: Dp
        @Composable @ReadOnlyComposable get() = dimensionResource(R.dimen.scan_sheet_top_corner)
    val sheetPadding: Dp
        @Composable @ReadOnlyComposable get() = dimensionResource(R.dimen.scan_sheet_padding)
    val sheetGap: Dp
        @Composable @ReadOnlyComposable get() = dimensionResource(R.dimen.scan_sheet_gap)
    val sheetButtonCorner: Dp
        @Composable @ReadOnlyComposable get() = dimensionResource(R.dimen.scan_sheet_button_corner)
    val sheetButtonVerticalPadding: Dp
        @Composable @ReadOnlyComposable get() = dimensionResource(R.dimen.scan_sheet_button_v_padding)
    val sheetRowGap: Dp
        @Composable @ReadOnlyComposable get() = dimensionResource(R.dimen.scan_sheet_row_gap)
    val progressOuter: Dp
        @Composable @ReadOnlyComposable get() = dimensionResource(R.dimen.scan_progress_outer)
    val progressStroke: Dp
        @Composable @ReadOnlyComposable get() = dimensionResource(R.dimen.scan_progress_stroke)
    val progressInner: Dp
        @Composable @ReadOnlyComposable get() = dimensionResource(R.dimen.scan_progress_inner)
    val progressRingBorder: Dp
        @Composable @ReadOnlyComposable get() = dimensionResource(R.dimen.scan_progress_ring_border)
    val analyzeTitleGap: Dp
        @Composable @ReadOnlyComposable get() = dimensionResource(R.dimen.scan_analyze_title_gap)
    val analyzeSubtitleGap: Dp
        @Composable @ReadOnlyComposable get() = dimensionResource(R.dimen.scan_analyze_subtitle_gap)
    val analyzeLabelsHorizontalPadding: Dp
        @Composable @ReadOnlyComposable get() = dimensionResource(R.dimen.scan_analyze_labels_h_padding)
    val analyzeLabelGap: Dp
        @Composable @ReadOnlyComposable get() = dimensionResource(R.dimen.scan_analyze_label_gap)
    val analyzeDotSize: Dp
        @Composable @ReadOnlyComposable get() = dimensionResource(R.dimen.scan_analyze_dot_size)
    val resultHeaderHorizontalPadding: Dp
        @Composable @ReadOnlyComposable get() = dimensionResource(R.dimen.scan_result_header_h_padding)
    val resultHeaderVerticalPadding: Dp
        @Composable @ReadOnlyComposable get() = dimensionResource(R.dimen.scan_result_header_v_padding)
    val resultContentHorizontalPadding: Dp
        @Composable @ReadOnlyComposable get() = dimensionResource(R.dimen.scan_result_content_h_padding)
    val resultContentVerticalPadding: Dp
        @Composable @ReadOnlyComposable get() = dimensionResource(R.dimen.scan_result_content_v_padding)
    val resultBlockGap: Dp
        @Composable @ReadOnlyComposable get() = dimensionResource(R.dimen.scan_result_block_gap)
    val resultCardCorner: Dp
        @Composable @ReadOnlyComposable get() = dimensionResource(R.dimen.scan_result_card_corner)
    val resultCardPaddingHorizontal: Dp
        @Composable @ReadOnlyComposable get() = dimensionResource(R.dimen.scan_result_card_padding_h)
    val resultCardPaddingVertical: Dp
        @Composable @ReadOnlyComposable get() = dimensionResource(R.dimen.scan_result_card_padding_v)
    val resultStageDot: Dp
        @Composable @ReadOnlyComposable get() = dimensionResource(R.dimen.scan_result_stage_dot)
    val resultStageDotGap: Dp
        @Composable @ReadOnlyComposable get() = dimensionResource(R.dimen.scan_result_stage_dot_gap)
    val resultScoresPadding: Dp
        @Composable @ReadOnlyComposable get() = dimensionResource(R.dimen.scan_result_scores_padding)
    val resultScoresGap: Dp
        @Composable @ReadOnlyComposable get() = dimensionResource(R.dimen.scan_result_scores_gap)
    val resultScoreLabelWidth: Dp
        @Composable @ReadOnlyComposable get() = dimensionResource(R.dimen.scan_result_score_label_width)
    val resultScoreBarHeight: Dp
        @Composable @ReadOnlyComposable get() = dimensionResource(R.dimen.scan_result_score_bar_height)
    val resultScoreBarCorner: Dp
        @Composable @ReadOnlyComposable get() = dimensionResource(R.dimen.scan_result_score_bar_corner)
    val resultScorePercentWidth: Dp
        @Composable @ReadOnlyComposable get() = dimensionResource(R.dimen.scan_result_score_pct_width)
    val resultScoreRowGap: Dp
        @Composable @ReadOnlyComposable get() = dimensionResource(R.dimen.scan_result_score_row_gap)
    val xaiLoadingCorner: Dp
        @Composable @ReadOnlyComposable get() = dimensionResource(R.dimen.scan_xai_loading_corner)
    val xaiLoadingBorder: Dp
        @Composable @ReadOnlyComposable get() = dimensionResource(R.dimen.scan_xai_loading_border)
    val xaiLoadingPadding: Dp
        @Composable @ReadOnlyComposable get() = dimensionResource(R.dimen.scan_xai_loading_padding)
    val xaiLoadingGap: Dp
        @Composable @ReadOnlyComposable get() = dimensionResource(R.dimen.scan_xai_loading_gap)
    val xaiSpinnerSize: Dp
        @Composable @ReadOnlyComposable get() = dimensionResource(R.dimen.scan_xai_spinner_size)
    val xaiSpinnerStroke: Dp
        @Composable @ReadOnlyComposable get() = dimensionResource(R.dimen.scan_xai_spinner_stroke)
    val xaiTextGap: Dp
        @Composable @ReadOnlyComposable get() = dimensionResource(R.dimen.scan_xai_text_gap)
    val reportCardGap: Dp
        @Composable @ReadOnlyComposable get() = dimensionResource(R.dimen.scan_report_card_gap)
    val reportIconGap: Dp
        @Composable @ReadOnlyComposable get() = dimensionResource(R.dimen.scan_report_icon_gap)
    val reportIconSize: Dp
        @Composable @ReadOnlyComposable get() = dimensionResource(R.dimen.scan_report_icon_size)
    val reportBodyLineHeight: TextUnit
        @Composable @ReadOnlyComposable get() = textDimen(R.dimen.scan_report_body_line_height)
    val reportSectionLineHeight: TextUnit
        @Composable @ReadOnlyComposable get() = textDimen(R.dimen.scan_report_section_line_height)
    val reportHeadingLetterSpacing: TextUnit
        @Composable @ReadOnlyComposable get() = textDimen(R.dimen.scan_report_heading_letter_spacing)
    val resultDisclaimerTop: Dp
        @Composable @ReadOnlyComposable get() = dimensionResource(R.dimen.scan_result_disclaimer_top)
    val resultDisclaimerBottom: Dp
        @Composable @ReadOnlyComposable get() = dimensionResource(R.dimen.scan_result_disclaimer_bottom)
    val errorBannerCorner: Dp
        @Composable @ReadOnlyComposable get() = dimensionResource(R.dimen.scan_error_banner_corner)
    val errorBannerPadding: Dp
        @Composable @ReadOnlyComposable get() = dimensionResource(R.dimen.scan_error_banner_padding)
    val primaryButtonCorner: Dp
        @Composable @ReadOnlyComposable get() = dimensionResource(R.dimen.scan_primary_button_corner)
    val primaryButtonVerticalPadding: Dp
        @Composable @ReadOnlyComposable get() = dimensionResource(R.dimen.scan_primary_button_v_padding)
    val footerBottom: Dp
        @Composable @ReadOnlyComposable get() = dimensionResource(R.dimen.scan_footer_bottom)
    val mriLabelHorizontalPadding: Dp
        @Composable @ReadOnlyComposable get() = dimensionResource(R.dimen.scan_mri_label_h_padding)
    val mriLabelVerticalPadding: Dp
        @Composable @ReadOnlyComposable get() = dimensionResource(R.dimen.scan_mri_label_v_padding)

    @Composable
    @ReadOnlyComposable
    private fun textDimen(@DimenRes id: Int): TextUnit = dimensionResource(id).value.sp
}
