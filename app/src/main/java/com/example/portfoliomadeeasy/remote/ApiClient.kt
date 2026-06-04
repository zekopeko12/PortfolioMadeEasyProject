package com.example.portfoliomadeeasy.remote

import com.example.portfoliomadeeasy.remote.api.CoinGeckoApi
import com.example.portfoliomadeeasy.remote.api.FinnhubApi
import com.example.portfoliomadeeasy.remote.api.MetalsDevApi
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import kotlin.getValue

object ApiClient {
    val coingecko: CoinGeckoApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.coingecko.com/api/v3/")
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(CoinGeckoApi::class.java)
    }

    val finnhub: FinnhubApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://finnhub.io/api/v1/")
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(FinnhubApi::class.java)
    }

    val metals: MetalsDevApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.metals.dev/")
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(MetalsDevApi::class.java)
    }
}