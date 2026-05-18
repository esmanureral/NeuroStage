package com.esmanureral.neurostage.xai.api

import com.google.gson.annotations.SerializedName

data class GradCamApiResponse(
    val status: String,
    val stage: String,
    val confidence: Double,
    @SerializedName("active_region")
    val activeRegion: String? = null,
    @SerializedName("peak_row")
    val peakRow: Int? = null,
    @SerializedName("peak_col")
    val peakCol: Int? = null,
    @SerializedName("peak_activation")
    val peakActivation: Double? = null,
    @SerializedName("heatmap_image")
    val heatmapImage: String,
)
