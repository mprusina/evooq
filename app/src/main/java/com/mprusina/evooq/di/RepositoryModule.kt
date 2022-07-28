package com.mprusina.evooq.di

import com.mprusina.evooq.data.DrugRepository
import com.mprusina.evooq.data.DrugRepositoryImpl
import com.mprusina.evooq.data.PatientRepository
import com.mprusina.evooq.data.PatientRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun drugRepository(drugRepository: DrugRepositoryImpl): DrugRepository

    @Binds
    abstract fun patientRepository(patientRepository: PatientRepositoryImpl): PatientRepository

}