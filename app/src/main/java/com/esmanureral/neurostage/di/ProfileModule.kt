package com.esmanureral.neurostage.di

import com.esmanureral.neurostage.profile.FirestoreUserProfileRepository
import com.esmanureral.neurostage.profile.UserProfileRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
@Suppress("unused")
abstract class ProfileModule {
    @Binds
    @Singleton
    abstract fun bindUserProfileRepository(impl: FirestoreUserProfileRepository): UserProfileRepository
}