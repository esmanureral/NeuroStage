package com.esmanureral.neurostage.xai.api

import okhttp3.MultipartBody
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Url

interface GradCamApiService {
    @GET
    suspend fun healthCheck(@Url url: String): GradCamHealthResponse

    @Multipart
    @POST(GradCamApiConfig.PREDICT_PATH)
    suspend fun predictGradCam(
        @Part file: MultipartBody.Part,
    ): GradCamApiResponse
}
