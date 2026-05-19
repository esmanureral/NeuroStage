package com.esmanureral.neurostage.xai.api

import com.esmanureral.neurostage.BuildConfig
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import retrofit2.HttpException

object GradCamNetworkErrors {
    fun isRetryable(cause: Throwable): Boolean {
        val root = rootCause(cause)
        return isTimeoutError(root) ||
            root is ConnectException ||
            root is UnknownHostException
    }

    fun toUserMessage(cause: Throwable): String {
        val root = rootCause(cause)
        val baseUrl = BuildConfig.GRAD_CAM_API_BASE_URL.trim()
            .ifEmpty { GradCamApiConfig.BASE_URL }
        val isHf = GradCamApiConfig.isHuggingFaceSpace(baseUrl)

        if (isTimeoutError(root)) {
            return if (isHf) {
                "Grad-CAM sunucusu yanıt vermedi (Hugging Face Space uykuda olabilir). " +
                    "İnternet bağlantınızı kontrol edip 1–2 dakika bekledikten sonra tekrar deneyin. " +
                    "Space özel ise HF_TOKEN tanımlı olmalı."
            } else {
                "Grad-CAM isteği zaman aşımına uğradı ($baseUrl). Sunucunun çalıştığından emin olun."
            }
        }

        return when (root) {
            is ConnectException -> buildString {
                append("Grad-CAM sunucusuna bağlanılamadı ($baseUrl). ")
                if (baseUrl.contains("10.0.2.2")) {
                    append("Yerel API çalışmıyor olabilir: scripts klasöründe uvicorn gradcam_api:app --host 0.0.0.0 --port 8000 ")
                }
                append("İnternet ve adresi kontrol edin. ")
                append("Özel Space için HF_TOKEN gerekir.")
            }

            is SocketTimeoutException ->
                toUserMessage(IOException("timeout", root))

            is UnknownHostException ->
                "Grad-CAM sunucu adresi çözülemedi ($baseUrl). GRAD_CAM_API_BASE_URL değerini kontrol edin."

            is HttpException -> when (root.code()) {
                401, 403 ->
                    "Grad-CAM erişim reddedildi. Özel Hugging Face Space için local.properties dosyasına HF_TOKEN ekleyin."

                else ->
                    "Grad-CAM sunucu hatası (${root.code()}): ${root.message()}"
            }

            else -> root.message?.takeIf { it.isNotBlank() }
                ?: "Grad-CAM ağ hatası ($baseUrl)"
        }
    }

    fun wrap(cause: Throwable): IOException =
        IOException(toUserMessage(cause), cause)

    private fun rootCause(cause: Throwable): Throwable =
        generateSequence(cause) { it.cause }.last()

    private fun isTimeoutError(cause: Throwable): Boolean {
        var current: Throwable? = cause
        while (current != null) {
            if (current is SocketTimeoutException) return true
            val message = current.message.orEmpty()
            if (
                message.contains("ETIMEDOUT", ignoreCase = true) ||
                message.contains("timed out", ignoreCase = true) ||
                message.contains("timeout", ignoreCase = true)
            ) {
                return true
            }
            current = current.cause
        }
        return false
    }
}
