package com.esmanureral.neurostage.xai.api

import com.esmanureral.neurostage.BuildConfig
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

object GradCamNetworkErrors {
    fun toUserMessage(cause: Throwable): String {
        val root = generateSequence(cause) { it.cause }.last()
        val baseUrl = BuildConfig.GRAD_CAM_API_BASE_URL.trim()
        return when (root) {
            is ConnectException -> buildString {
                append("Grad-CAM sunucusuna bağlanılamadı ($baseUrl). ")
                append("Kontrol listesi: ")
                append("1) Terminalde API çalışıyor mu → uvicorn gradcam_api:app --host 0.0.0.0 --port 8000 ")
                append("2) Emülatör kullanıyorsanız adres http://10.0.2.2:8000/ ")
                append("3) Gerçek telefonda bilgisayarın Wi‑Fi IP adresi (10.0.2.2 çalışmaz) ")
                append("4) Windows Güvenlik Duvarı 8000 portuna izin veriyor mu")
            }

            is SocketTimeoutException ->
                "Grad-CAM isteği zaman aşımına uğradı ($baseUrl). Sunucu CPU'da yavaş olabilir; model dosyası yüklü mü kontrol edin."

            is UnknownHostException ->
                "Grad-CAM sunucu adresi çözülemedi ($baseUrl). local.properties içindeki GRAD_CAM_API_BASE_URL değerini kontrol edin."

            else -> root.message?.takeIf { it.isNotBlank() }
                ?: "Grad-CAM ağ hatası ($baseUrl)"
        }
    }

    fun wrap(cause: Throwable): IOException =
        IOException(toUserMessage(cause), cause)
}
