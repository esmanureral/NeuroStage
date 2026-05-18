package com.esmanureral.neurostage.xai.api

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import com.esmanureral.neurostage.R
import com.esmanureral.neurostage.xai.GradCamRegionMapper
import com.esmanureral.neurostage.xai.GradCamResult
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.ByteArrayOutputStream
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

@Singleton
class GradCamRemoteRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val api: GradCamApiService,
) {
    suspend fun fetch(bitmap: Bitmap): GradCamResult {
        val jpegBytes = ByteArrayOutputStream().use { output ->
            if (!bitmap.compress(Bitmap.CompressFormat.JPEG, 92, output)) {
                throw IOException("MR görüntüsü JPEG olarak kodlanamadı.")
            }
            output.toByteArray()
        }

        val filePart = MultipartBody.Part.createFormData(
            name = "file",
            filename = "mri_${System.currentTimeMillis()}.jpg",
            body = jpegBytes.toRequestBody("image/jpeg".toMediaType()),
        )

        val response = try {
            api.predictGradCam(filePart)
        } catch (e: Exception) {
            throw GradCamNetworkErrors.wrap(e)
        }

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
