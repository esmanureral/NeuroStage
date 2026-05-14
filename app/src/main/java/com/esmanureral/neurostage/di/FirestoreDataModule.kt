package com.esmanureral.neurostage.di

import com.esmanureral.neurostage.patients.FirestorePatientRepository
import com.esmanureral.neurostage.patients.PatientRepository
import com.esmanureral.neurostage.scans.FirestoreScanRepository
import com.esmanureral.neurostage.scans.ScanRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
@Suppress("unused")
abstract class FirestoreDataModule {
    @Binds
    @Singleton
    abstract fun bindPatientRepo(impl: FirestorePatientRepository): PatientRepository
    @Binds
    @Singleton
    abstract fun bindScanRepo(impl: FirestoreScanRepository): ScanRepository
}