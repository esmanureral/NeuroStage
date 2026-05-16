package com.esmanureral.neurostage.di

import com.esmanureral.neurostage.data.patient.BrainExerciseRepository
import com.esmanureral.neurostage.data.patient.BrainExerciseRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class PatientModule {

    @Binds
    @Singleton
    abstract fun bindBrainExerciseRepository(
        impl: BrainExerciseRepositoryImpl,
    ): BrainExerciseRepository
}
