package com.example.portfoliomadeeasy.remote.api

import com.example.portfoliomadeeasy.remote.model.MetalsDevLatestResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface MetalsDevApi {
    @GET("v1/latest")
    suspend fun getLatestRates(
        @Query("api_key") apiKey: String,
        @Query("currency") baseCurrency: String = "USD",
        @Query("unit") unit: String = "toz",
    ): MetalsDevLatestResponse
}