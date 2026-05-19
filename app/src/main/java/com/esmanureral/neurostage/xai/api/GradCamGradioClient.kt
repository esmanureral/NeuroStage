package com.esmanureral.neurostage.xai.api

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import com.esmanureral.neurostage.BuildConfig
import java.io.IOException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

data class GradCamGradioResult(
    val overlayBitmap: Bitmap,
    val resultText: String,
    val stageLabelTr: String?,
    val confidencePercent: Int,
    val probabilities: Map<String, Float>,
)

class GradCamGradioClient(
    private val httpClient: OkHttpClient,
) {
    suspend fun predict(baseUrl: String, jpegBytes: ByteArray): GradCamGradioResult =
        withContext(Dispatchers.IO) {
            val root = normalizeBaseUrl(baseUrl)
            val uploadedPath = uploadImage(root, jpegBytes)
            val eventId = startPrediction(root, uploadedPath)
            val data = pollResult(root, eventId)
            parseResult(root, data)
        }

    suspend fun wakeUp(baseUrl: String) = withContext(Dispatchers.IO) {
        val root = normalizeBaseUrl(baseUrl)
        val request = authorizedRequest("${root}gradio_api/info")
            .get()
            .build()
        httpClient.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                throw IOException("Space uyanamadı (HTTP ${response.code})")
            }
        }
    }

    private fun uploadImage(root: String, jpegBytes: ByteArray): String {
        val tempFile = File.createTempFile("mri_upload_", ".jpg")
        try {
            tempFile.writeBytes(jpegBytes)
            val body = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart(
                    "files",
                    "mri.jpg",
                    tempFile.asRequestBody("image/jpeg".toMediaType()),
                )
                .build()

            val request = authorizedRequest("${root}gradio_api/upload")
                .post(body)
                .build()

            httpClient.newCall(request).execute().use { response ->
                val raw = response.body?.string().orEmpty()
                if (!response.isSuccessful) {
                    throw IOException("Görüntü yüklenemedi (HTTP ${response.code}): $raw")
                }
                return parseUploadedPath(raw)
            }
        } finally {
            tempFile.delete()
        }
    }

    private fun startPrediction(root: String, uploadedPath: String): String {
        val imagePayload = JSONObject().apply {
            put("path", uploadedPath)
            put("meta", JSONObject().put("_type", "gradio.FileData"))
            put("orig_name", "mri.jpg")
            put("mime_type", "image/jpeg")
        }
        val payload = JSONObject().put("data", JSONArray().put(imagePayload)).toString()

        val request = authorizedRequest("${root}gradio_api/call/predict_gradcam")
            .post(payload.toRequestBody("application/json".toMediaType()))
            .build()

        httpClient.newCall(request).execute().use { response ->
            val raw = response.body?.string().orEmpty()
            if (!response.isSuccessful) {
                throw IOException("Grad-CAM çağrısı başarısız (HTTP ${response.code}): $raw")
            }
            val json = JSONObject(raw)
            return json.optString("event_id").ifBlank {
                throw IOException("Gradio event_id alınamadı.")
            }
        }
    }

    /**
     * Gradio: tek GET bağlantısı iş bitene kadar açık kalır (SSE).
     * Hızlı "generating" yanıtı gelirse kısa aralıklarla tekrar dener.
     */
    private suspend fun pollResult(root: String, eventId: String): JSONArray {
        val url = "${root}gradio_api/call/predict_gradcam/$eventId"
        repeat(POLL_MAX_ATTEMPTS) { attempt ->
            val request = authorizedRequest(url)
                .get()
                .header("Accept", "text/event-stream")
                .build()

            httpClient.newCall(request).execute().use { response ->
                val raw = response.body?.string().orEmpty()
                if (!response.isSuccessful) {
                    throw IOException("Grad-CAM sonucu alınamadı (HTTP ${response.code}): $raw")
                }

                extractResultData(raw)?.let { return it }

                if (!isStillProcessing(raw)) {
                    throw IOException("Grad-CAM sonucu alınamadı: ${raw.take(280)}")
                }
            }

            if (attempt < POLL_MAX_ATTEMPTS - 1) {
                delay(if (attempt < 5) POLL_DELAY_FAST_MS else POLL_DELAY_MS)
            }
        }
        throw IOException("Grad-CAM sunucusu 90 sn içinde yanıt vermedi.")
    }

    private fun extractResultData(raw: String): JSONArray? {
        parsePollResponse(raw)?.takeIf { it.length() >= 2 }?.let { return it }

        val trimmed = raw.trim()
        if (trimmed.startsWith("{")) {
            val json = runCatching { JSONObject(trimmed) }.getOrNull() ?: return null
            if (json.optString("event") == "error") {
                throw IOException(json.optString("message", "Gradio hata döndü."))
            }
            extractDataArray(json)?.takeIf { it.length() >= 2 }?.let { return it }
        }
        return null
    }

    private fun parseResult(root: String, data: JSONArray): GradCamGradioResult {
        val overlayBitmap = decodeGradioImage(root, data.opt(0))
            ?: throw IOException("Maskeli Grad-CAM görseli çözümlenemedi.")

        val resultText = jsonElementToText(data.opt(1))
        val probabilities = parseProbabilities(data.opt(2))
        val (stageLabel, confidence) = parsePredictionFromText(resultText, probabilities)

        return GradCamGradioResult(
            overlayBitmap = overlayBitmap,
            resultText = resultText,
            stageLabelTr = stageLabel,
            confidencePercent = confidence,
            probabilities = probabilities,
        )
    }

    private fun parseProbabilities(element: Any?): Map<String, Float> {
        val jsonObject = when (element) {
            is JSONObject -> element
            is String -> runCatching { JSONObject(element) }.getOrNull()
            else -> null
        } ?: return emptyMap()
        val map = mutableMapOf<String, Float>()
        jsonObject.keys().forEach { key ->
            map[key] = jsonObject.optDouble(key, 0.0).toFloat()
        }
        return map
    }

    private fun parseUploadedPath(raw: String): String {
        val trimmed = raw.trim()
        if (trimmed.isEmpty()) throw IOException("Gradio upload yanıtı boş.")
        return when {
            trimmed.startsWith("[") -> {
                val arr = JSONArray(trimmed)
                if (arr.length() == 0) throw IOException("Gradio upload yanıtı boş.")
                extractPathFromElement(arr.opt(0))
            }
            trimmed.startsWith("{") -> {
                val obj = JSONObject(trimmed)
                val files = obj.optJSONArray("files")
                if (files != null && files.length() > 0) {
                    extractPathFromElement(files.opt(0))
                } else {
                    extractPathFromElement(obj)
                }
            }
            else -> throw IOException("Gradio upload yanıtı tanınamadı: $trimmed")
        }
    }

    private fun extractPathFromElement(element: Any?): String {
        when (element) {
            is String -> if (element.isNotBlank()) return element
            is JSONObject -> {
                element.optString("path").takeIf { it.isNotBlank() }?.let { return it }
                element.optString("name").takeIf { it.isNotBlank() }?.let { return it }
                element.optString("url").takeIf { it.isNotBlank() }?.let { return it }
            }
        }
        throw IOException("Upload path çözümlenemedi.")
    }

    private fun parsePollResponse(raw: String): JSONArray? {
        val trimmed = raw.trim()
        if (trimmed.isEmpty()) return null
        if (trimmed.startsWith("[")) {
            val arr = JSONArray(trimmed)
            return arr.takeIf { it.length() >= 2 }
        }
        if (trimmed.contains("data:")) {
            return parseSsePollData(trimmed)
        }
        if (!trimmed.startsWith("{")) return null
        val json = JSONObject(trimmed)
        return extractDataArray(json)?.takeIf { it.length() >= 2 }
    }

    private fun parseSsePollData(raw: String): JSONArray? {
        var lastComplete: JSONArray? = null
        var lastAny: JSONArray? = null
        for (line in raw.lines()) {
            val trimmedLine = line.trim()
            if (!trimmedLine.startsWith("data:")) continue
            val payload = trimmedLine.removePrefix("data:").trim()
            if (payload.isEmpty() || payload == "null") continue
            when {
                payload.startsWith("[") -> {
                    val arr = JSONArray(payload)
                    if (arr.length() >= 2) {
                        lastAny = arr
                        lastComplete = arr
                    }
                }
                payload.startsWith("{") -> {
                    val obj = JSONObject(payload)
                    extractDataArray(obj)?.takeIf { it.length() >= 2 }?.let {
                        lastAny = it
                        lastComplete = it
                    }
                }
            }
        }
        return lastComplete ?: lastAny
    }

    private fun extractDataArray(json: JSONObject): JSONArray? {
        if (!json.has("data") || json.isNull("data")) return null
        return when (val value = json.opt("data")) {
            is JSONArray -> value
            is String -> runCatching { JSONArray(value) }.getOrNull()
            else -> null
        }
    }

    private fun isStillProcessing(raw: String): Boolean {
        val trimmed = raw.trim()
        if (trimmed.contains("process_generating")) return true
        if (trimmed.contains("\"status\":\"pending\"")) return true
        if (trimmed.contains("event: generating")) return true
        if (!trimmed.startsWith("{")) {
            return trimmed.contains("data:") && !trimmed.contains("event: error")
        }
        val json = runCatching { JSONObject(trimmed) }.getOrNull() ?: return false
        if (json.optString("msg") == "process_generating") return true
        if (json.optString("status") == "pending") return true
        val event = json.optString("event")
        return event == "generating" || event == "process_generating"
    }

    private fun jsonElementToText(element: Any?): String {
        return when (element) {
            null, JSONObject.NULL -> ""
            is String -> element
            is JSONObject -> {
                element.optString("text")
                    .ifBlank { element.optString("value") }
                    .ifBlank { element.optString("data") }
            }
            else -> element.toString()
        }
    }

    private fun parsePredictionFromText(
        resultText: String,
        probabilities: Map<String, Float>,
    ): Pair<String?, Int> {
        val tahminRegex = Regex("""Tahmin:\s*(.+?)(?:\n|$)""")
        val guvenRegex = Regex("""Güven:\s*%?(\d+(?:[.,]\d+)?)""")
        val stage = tahminRegex.find(resultText)?.groupValues?.getOrNull(1)?.trim()
        val confidenceFromText = guvenRegex.find(resultText)?.groupValues?.getOrNull(1)
            ?.replace(',', '.')
            ?.toFloatOrNull()
            ?.toInt()

        if (confidenceFromText != null) {
            return stage to confidenceFromText
        }

        val top = probabilities.maxByOrNull { it.value }
        return stage to (top?.value?.toInt() ?: 0)
    }

    private fun decodeGradioImage(root: String, element: Any?): Bitmap? {
        when (element) {
            is JSONObject -> {
                decodeImageUrl(element.optString("url"), root)?.let { return it }
                val path = element.optString("path")
                if (path.isNotBlank()) {
                    val fileUrl = if (path.startsWith("http")) path else "${root}file=$path"
                    decodeImageUrl(fileUrl, root)?.let { return it }
                }
            }
            is String -> decodeImageUrl(element, root)?.let { return it }
        }
        return null
    }

    private fun decodeImageUrl(url: String, root: String): Bitmap? {
        if (url.isBlank()) return null
        return when {
            url.startsWith("data:") -> decodeBase64Payload(url.substringAfter("base64,", url))
            url.startsWith("http") -> downloadBitmap(url)
            else -> downloadBitmap("${root}file=$url")
        }
    }

    private fun decodeBase64Payload(payload: String): Bitmap? {
        if (payload.isBlank()) return null
        val bytes = Base64.decode(payload, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }

    private fun downloadBitmap(url: String): Bitmap? {
        val request = authorizedRequest(url).get().build()
        httpClient.newCall(request).execute().use { response ->
            if (!response.isSuccessful) return null
            val bytes = response.body?.bytes() ?: return null
            return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        }
    }

    private fun authorizedRequest(url: String): Request.Builder {
        val builder = Request.Builder().url(url)
        val token = BuildConfig.HF_TOKEN.trim()
        if (token.isNotEmpty()) {
            builder.header("Authorization", "Bearer $token")
        }
        return builder
    }

    private fun normalizeBaseUrl(baseUrl: String): String {
        val raw = baseUrl.trim().ifEmpty { GradCamApiConfig.BASE_URL }
        return if (raw.endsWith("/")) raw else "$raw/"
    }

    companion object {
        private const val POLL_MAX_ATTEMPTS = 30
        private const val POLL_DELAY_FAST_MS = 800L
        private const val POLL_DELAY_MS = 2_000L
    }
}
