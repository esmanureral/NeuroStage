package com.esmanureral.neurostage.xai.api

import okhttp3.MultipartBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface GradCamApiService {
    @Multipart
    @POST("predict-gradcam")
    suspend fun predictGradCam(
        @Part file: MultipartBody.Part,
    ): GradCamApiResponse
}
