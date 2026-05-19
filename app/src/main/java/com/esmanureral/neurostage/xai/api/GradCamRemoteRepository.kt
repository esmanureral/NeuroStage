package com.esmanureral.neurostage.xai.api

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import com.esmanureral.neurostage.BuildConfig
import com.esmanureral.neurostage.R
import com.esmanureral.neurostage.xai.GradCamRegionMapper
import com.esmanureral.neurostage.xai.GradCamResult
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.ByteArrayOutputStream
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.max
import kotlin.math.roundToInt
import kotlinx.coroutines.delay
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

@Singleton
class GradCamRemoteRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val legacyApi: GradCamApiService,
    private val gradioClient: GradCamGradioClient,
) {
    private val baseUrl: String
        get() {
            val raw = BuildConfig.GRAD_CAM_API_BASE_URL.trim()
                .ifEmpty { GradCamApiConfig.BASE_URL }
            return if (raw.endsWith("/")) raw else "$raw/"
        }

    private val usesGradioApi: Boolean
        get() = GradCamApiConfig.isHuggingFaceSpace(baseUrl)

    suspend fun fetch(bitmap: Bitmap): GradCamResult {
        if (usesGradioApi) {
            wakeUpHuggingFaceSpace()
        }

        val uploadBitmap = bitmap.scaleForUpload(GradCamApiConfig.UPLOAD_MAX_SIDE_PX)
        val jpegBytes = encodeJpeg(uploadBitmap)
        if (uploadBitmap !== bitmap && !uploadBitmap.isRecycled) {
            uploadBitmap.recycle()
        }

        return if (usesGradioApi) {
            mapGradioResult(fetchGradioWithRetry(jpegBytes))
        } else {
            mapLegacyResponse(fetchLegacyWithRetry(jpegBytes))
        }
    }

    private suspend fun wakeUpHuggingFaceSpace() {
        var lastError: Throwable? = null
        repeat(GradCamApiConfig.HF_WAKE_MAX_ATTEMPTS) { attempt ->
            try {
                gradioClient.wakeUp(baseUrl)
                return
            } catch (e: Exception) {
                lastError = e
                if (attempt < GradCamApiConfig.HF_WAKE_MAX_ATTEMPTS - 1) {
                    delay(GradCamApiConfig.HF_WAKE_RETRY_DELAY_MS)
                }
            }
        }
        throw GradCamNetworkErrors.wrap(
            lastError ?: IOException("Hugging Face Space uyandırılamadı."),
        )
    }

    private suspend fun fetchGradioWithRetry(jpegBytes: ByteArray): GradCamGradioResult {
        var lastError: Throwable? = null
        repeat(GradCamApiConfig.HF_PREDICT_MAX_ATTEMPTS) { attempt ->
            try {
                return gradioClient.predict(baseUrl, jpegBytes)
            } catch (e: Exception) {
                lastError = e
                if (attempt < GradCamApiConfig.HF_PREDICT_MAX_ATTEMPTS - 1 &&
                    GradCamNetworkErrors.isRetryable(e)
                ) {
                    delay(GradCamApiConfig.HF_WAKE_RETRY_DELAY_MS)
                }
            }
        }
        throw GradCamNetworkErrors.wrap(
            lastError ?: IOException("Grad-CAM isteği başarısız."),
        )
    }

    private suspend fun fetchLegacyWithRetry(jpegBytes: ByteArray): GradCamApiResponse {
        var lastError: Throwable? = null
        repeat(1) {
            val filePart = MultipartBody.Part.createFormData(
                name = "file",
                filename = "mri_${System.currentTimeMillis()}.jpg",
                body = jpegBytes.toRequestBody("image/jpeg".toMediaType()),
            )
            try {
                return legacyApi.predictGradCam(filePart)
            } catch (e: Exception) {
                lastError = e
            }
        }
        throw GradCamNetworkErrors.wrap(
            lastError ?: IOException("Grad-CAM isteği başarısız."),
        )
    }

    private fun mapGradioResult(result: GradCamGradioResult): GradCamResult {
        val peakActivation = (result.confidencePercent / 100f).coerceIn(0f, 1f)

        return GradCamResult(
            heatmapBitmap = result.overlayBitmap,
            activeRegion = context.getString(R.string.gradcam_masked_region_hint),
            peakActivation = peakActivation,
            hfPredictionSummary = result.resultText.takeIf { it.isNotBlank() },
            hfClassProbabilities = result.probabilities,
            hfPredictedStageLabel = result.stageLabelTr,
        )
    }

    private fun mapLegacyResponse(response: GradCamApiResponse): GradCamResult {
        if (!response.status.equals("success", ignoreCase = true)) {
            throw IOException("Grad-CAM API durumu: ${response.status}")
        }

        val heatmapBitmap = decodeHeatmapImage(response.heatmapImage)
            ?: throw IOException("Grad-CAM ısı haritası çözümlenemedi.")

        val activeRegion = resolveActiveRegion(response)
        val peakActivation = response.peakActivation?.toFloat()?.coerceIn(0f, 1f) ?: 0f

        return GradCamResult(
            heatmapBitmap = heatmapBitmap,
            activeRegion = activeRegion,
            peakActivation = peakActivation,
        )
    }

    private fun encodeJpeg(bitmap: Bitmap): ByteArray =
        ByteArrayOutputStream().use { output ->
            if (!bitmap.compress(Bitmap.CompressFormat.JPEG, 88, output)) {
                throw IOException("MR görüntüsü JPEG olarak kodlanamadı.")
            }
            output.toByteArray()
        }

    private fun resolveActiveRegion(response: GradCamApiResponse): String {
        response.activeRegion?.trim()?.takeIf { it.isNotEmpty() }?.let { return it }
        val row = response.peakRow
        val col = response.peakCol
        if (row != null && col != null) {
            return GradCamRegionMapper.detectActiveRegionFromGrid(context, row, col)
        }
        return context.getString(R.string.brain_region_unknown)
    }

    private fun decodeHeatmapImage(dataUrl: String): Bitmap? {
        val base64Payload = dataUrl
            .substringAfter("base64,", dataUrl)
            .trim()
        if (base64Payload.isEmpty()) return null
        val bytes = Base64.decode(base64Payload, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }
}

private fun Bitmap.scaleForUpload(maxSide: Int): Bitmap {
    val longest = max(width, height)
    if (longest <= maxSide) return this
    val scale = maxSide.toFloat() / longest
    val targetW = (width * scale).roundToInt().coerceAtLeast(1)
    val targetH = (height * scale).roundToInt().coerceAtLeast(1)
    return Bitmap.createScaledBitmap(this, targetW, targetH, true)
}
