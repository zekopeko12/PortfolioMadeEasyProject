package com.example.portfoliomadeeasy.remote.api

import com.example.portfoliomadeeasy.remote.model.FinnhubQuoteResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface FinnhubApi {
    @GET("quote")
    suspend fun getStockQuote(
        @Query("symbol") symbol: String,
        @Query("token") apiKey: String
    ): FinnhubQuoteResponse
}