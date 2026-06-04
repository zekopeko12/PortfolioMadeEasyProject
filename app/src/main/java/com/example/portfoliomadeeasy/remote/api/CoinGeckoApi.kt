package com.example.portfoliomadeeasy.remote.api

import retrofit2.http.GET
import retrofit2.http.Query

interface CoinGeckoApi {
    @GET("simple/price")
    suspend fun getCryptoPrice(
        @Query("ids") ids: String = "bitcoin",
        @Query("vs_currencies") vsCurrency: String = "usd"
    ): Map<String, Map<String, Double>>
}