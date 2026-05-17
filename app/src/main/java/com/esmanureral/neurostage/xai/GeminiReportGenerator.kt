package com.esmanureral.neurostage.xai

import android.graphics.Bitmap
import android.util.Base64
import android.util.Log
import com.esmanureral.neurostage.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.util.concurrent.TimeUnit

class GeminiReportGenerator(private val context: android.content.Context) {

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    suspend fun generate(
        bitmap: Bitmap,
        stageLabel: String,
        topMean: Float,
        topStd: Float,
        allScores: FloatArray,
        allStdScores: FloatArray?,
        patientAge: Int?,
        patientGender: String?,
        activeRegion: String?,
        saliencyPeakScore: Float?,
    ): GeminiReport = withContext(Dispatchers.IO) {

        val patientDesc = when {
            patientGender != null && patientAge != null -> context.getString(
                com.esmanureral.neurostage.R.string.xai_patient_desc_both,
                patientGender,
                patientAge
            )

            patientGender != null -> context.getString(
                com.esmanureral.neurostage.R.string.xai_patient_desc_gender,
                patientGender
            )

            patientAge != null -> context.getString(
                com.esmanureral.neurostage.R.string.xai_patient_desc_age,
                patientAge
            )

            else -> context.getString(com.esmanureral.neurostage.R.string.xai_patient_desc_none)
        }

        val classLabels =
            context.resources.getStringArray(com.esmanureral.neurostage.R.array.dementia_stage_labels)
        val allProbs = classLabels.mapIndexed { i, label ->
            val mean = (allScores.getOrElse(i) { 0f } * 100).toInt()
            val std =
                allStdScores?.getOrElse(i) { 0f }?.let { " ±${"%.1f".format(it * 100)}%" } ?: ""
            "$label: %$mean$std"
        }.joinToString(" | ")

        val saliencyLine = if (activeRegion != null) {
            if (saliencyPeakScore != null) {
                context.getString(
                    com.esmanureral.neurostage.R.string.xai_saliency_success_peak,
                    activeRegion,
                    (saliencyPeakScore * 100).toInt()
                )
            } else {
                context.getString(
                    com.esmanureral.neurostage.R.string.xai_saliency_success,
                    activeRegion
                )
            }
        } else {
            context.getString(com.esmanureral.neurostage.R.string.xai_saliency_fail)
        }

        val prompt = context.getString(
            com.esmanureral.neurostage.R.string.xai_prompt,
            patientDesc,
            stageLabel,
            (topMean * 100).toInt(),
            topStd * 100,
            allProbs,
            saliencyLine
        )


        val imageBase64 = run {
            val out = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 85, out)
            Base64.encodeToString(out.toByteArray(), Base64.NO_WRAP)
        }

        val body = JSONObject().apply {
            put("model", "meta-llama/llama-4-scout-17b-16e-instruct")
            put("temperature", 0.4)
            put("max_tokens", 1024)
            put("messages", JSONArray().apply {
                put(JSONObject().apply {
                    put("role", "user")
                    put("content", JSONArray().apply {

                        put(JSONObject().apply {
                            put("type", "image_url")
                            put("image_url", JSONObject().apply {
                                put("url", "data:image/jpeg;base64,$imageBase64")
                            })
                        })

                        put(JSONObject().apply {
                            put("type", "text")
                            put("text", prompt)
                        })
                    })
                })
            })
        }.toString()

        val request = Request.Builder()
            .url("https://api.groq.com/openai/v1/chat/completions")
            .addHeader("Authorization", "Bearer ${BuildConfig.GROK_API_KEY}")
            .addHeader("Content-Type", "application/json")
            .post(body.toRequestBody("application/json".toMediaType()))
            .build()

        val responseText = runCatching {
            client.newCall(request).execute().use { response ->
                val rawBody = response.body?.string() ?: ""
                Log.d("GroqReport", "HTTP ${response.code} — body: ${rawBody.take(300)}")
                if (!response.isSuccessful) {
                    error("HTTP ${response.code}: $rawBody")
                }
                val json = JSONObject(rawBody)
                json.getJSONArray("choices")
                    .getJSONObject(0)
                    .getJSONObject("message")
                    .getString("content")
                    .trim()
            }
        }.onFailure {
            Log.e("GroqReport", "❌ API hatası [${it.javaClass.simpleName}]: ${it.message}", it)
        }.getOrNull()

        if (responseText.isNullOrBlank()) {
            val regionInfo = if (activeRegion != null) context.getString(
                com.esmanureral.neurostage.R.string.xai_fallback_region_info,
                activeRegion
            ) else ""
            GeminiReport(
                text = context.getString(
                    com.esmanureral.neurostage.R.string.xai_fallback_report,
                    patientDesc,
                    stageLabel,
                    (topMean * 100).toInt(),
                    regionInfo
                )
            )
        } else {
            GeminiReport(text = responseText)
        }
    }
}