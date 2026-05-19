package com.esmanureral.neurostage.xai.api

object GradCamApiConfig {
    const val BASE_URL = "https://esmanurerl-neurostage-gradcam-api.hf.space/"
    const val PREDICT_PATH = "predict-gradcam"

    const val UPLOAD_MAX_SIDE_PX = 260
    const val HF_WAKE_MAX_ATTEMPTS = 2
    const val HF_WAKE_RETRY_DELAY_MS = 4_000L
    const val HF_PREDICT_MAX_ATTEMPTS = 2

    fun isHuggingFaceSpace(baseUrl: String): Boolean =
        baseUrl.contains("hf.space", ignoreCase = true)
}
