package com.esmanureral.neurostage.di

import com.esmanureral.neurostage.BuildConfig
import com.esmanureral.neurostage.xai.api.GradCamApiConfig
import com.esmanureral.neurostage.xai.api.GradCamApiService
import com.esmanureral.neurostage.xai.api.GradCamGradioClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import java.util.concurrent.TimeUnit
import javax.inject.Singleton
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val baseUrl = resolveGradCamBaseUrl()
        val isHfSpace = GradCamApiConfig.isHuggingFaceSpace(baseUrl)

        val builder = OkHttpClient.Builder()
            .connectTimeout(if (isHfSpace) 120 else 60, TimeUnit.SECONDS)
            .readTimeout(if (isHfSpace) 300 else 120, TimeUnit.SECONDS)
            .writeTimeout(if (isHfSpace) 120 else 60, TimeUnit.SECONDS)
            .callTimeout(if (isHfSpace) 360 else 180, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)

        val hfToken = BuildConfig.HF_TOKEN.trim()
        if (hfToken.isNotEmpty()) {
            builder.addInterceptor(huggingFaceAuthInterceptor(hfToken))
        }

        return builder.build()
    }

    @Provides
    @Singleton
    fun provideGradCamGradioClient(client: OkHttpClient): GradCamGradioClient =
        GradCamGradioClient(client)

    @Provides
    @Singleton
    fun provideGradCamApiService(client: OkHttpClient): GradCamApiService {
        val baseUrl = resolveGradCamBaseUrl()
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GradCamApiService::class.java)
    }

    private fun resolveGradCamBaseUrl(): String {
        val configured = BuildConfig.GRAD_CAM_API_BASE_URL.trim()
        val raw = configured.ifEmpty { GradCamApiConfig.BASE_URL }
        return if (raw.endsWith("/")) raw else "$raw/"
    }

    private fun huggingFaceAuthInterceptor(token: String): Interceptor = Interceptor { chain ->
        val request = chain.request().newBuilder()
            .header("Authorization", "Bearer $token")
            .build()
        chain.proceed(request)
    }
}
