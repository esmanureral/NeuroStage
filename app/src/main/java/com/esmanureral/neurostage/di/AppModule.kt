package com.esmanureral.neurostage.di

import android.content.Context
import android.content.SharedPreferences
import com.esmanureral.neurostage.TFLiteClassifier
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences =
        context.getSharedPreferences("neurostage_prefs", Context.MODE_PRIVATE)

    @Provides
    @Singleton
    @Named("alzheimer")
    fun provideAlzheimerClassifier(@ApplicationContext context: Context): TFLiteClassifier =
        TFLiteClassifier(
            context = context,
            modelFileName = "alzheimer_preprocessed.tflite",
            inputSize = 260,
            numClasses = 4,
            normalize = false
        )

    @Provides
    @Singleton
    @Named("mriFilter")
    fun provideMriFilterClassifier(@ApplicationContext context: Context): TFLiteClassifier =
        TFLiteClassifier(
            context = context,
            modelFileName = "mri_filter_v2_noquant.tflite",
            inputSize = 224,
            numClasses = 2,
            normalize = true   // MobileNetV3Small: [0,1] range
        )
}