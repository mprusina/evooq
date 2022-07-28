package com.mprusina.evooq.data.api

import retrofit2.http.GET

interface PatientService {
    @GET("patients")
    suspend fun getPatients(): String
}