package com.mprusina.evooq.di

import com.mprusina.evooq.data.api.DrugService
import com.mprusina.evooq.data.api.PatientService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    private const val BASE_URL = "http://192.168.1.11:7200/"

    @Singleton
    @Provides
    fun providesPatientApiService(retrofit: Retrofit): PatientService {
        return retrofit.create(PatientService::class.java)
    }

    @Singleton
    @Provides
    fun providesDrugApiService(retrofit: Retrofit): DrugService {
        return retrofit.create(DrugService::class.java)
    }

    @Singleton
    @Provides
    fun providesRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(ScalarsConverterFactory.create())
            .build()
    }

}