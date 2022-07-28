package com.mprusina.evooq.data.api

import retrofit2.http.GET

interface DrugService {
    @GET("drugs")
    suspend fun getDrugs(): String
}