package com.esmanureral.neurostage.di

import android.content.Context
import android.content.SharedPreferences
import com.esmanureral.neurostage.TFLiteClassifier
import com.esmanureral.neurostage.util.Constants
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
        context.getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE)

    @Provides
    @Singleton
    @Named("alzheimer")
    fun provideAlzheimerClassifier(@ApplicationContext context: Context): TFLiteClassifier =
        TFLiteClassifier(
            context = context,
            modelFileName = Constants.Model.ALZHEIMER_FILE_NAME,
            inputSize = Constants.Model.ALZHEIMER_INPUT_SIZE,
            numClasses = Constants.Model.ALZHEIMER_NUM_CLASSES
        )

    @Provides
    @Singleton
    @Named("mriFilter")
    fun provideMriFilterClassifier(@ApplicationContext context: Context): TFLiteClassifier =
        TFLiteClassifier(
            context = context,
            modelFileName = Constants.Model.MRI_FILTER_FILE_NAME,
            inputSize = Constants.Model.MRI_FILTER_INPUT_SIZE,
            numClasses = Constants.Model.MRI_FILTER_NUM_CLASSES,
            normalize = true
        )
}