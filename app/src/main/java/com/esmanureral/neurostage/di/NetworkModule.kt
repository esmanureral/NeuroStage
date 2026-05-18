package com.esmanureral.neurostage.di

import com.esmanureral.neurostage.BuildConfig
import com.esmanureral.neurostage.xai.api.GradCamApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import java.util.concurrent.TimeUnit
import javax.inject.Singleton
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient =
        OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .build()

    @Provides
    @Singleton
    fun provideGradCamApiService(client: OkHttpClient): GradCamApiService {
        val baseUrl = BuildConfig.GRAD_CAM_API_BASE_URL.trim()
        require(baseUrl.isNotEmpty()) {
            "GRAD_CAM_API_BASE_URL local.properties içinde tanımlı olmalı."
        }
        val normalizedBaseUrl = if (baseUrl.endsWith("/")) baseUrl else "$baseUrl/"
        return Retrofit.Builder()
            .baseUrl(normalizedBaseUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GradCamApiService::class.java)
    }
}
