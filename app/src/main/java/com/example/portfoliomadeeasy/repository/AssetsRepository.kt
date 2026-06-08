package com.example.portfoliomadeeasy.repository

import android.util.Log
import com.example.portfoliomadeeasy.remote.ApiClient
import com.example.portfoliomadeeasy.remote.model.Asset
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

class AssetsRepository(
    private val finnhubKey: String,
    private val metalsKey: String
) {
    private val cryptoList = listOf("bitcoin", "ethereum", "dogecoin")
    private val stockList = listOf("AAPL", "MSFT", "GOOGL")
    private val metalList = listOf("gold", "silver")

    suspend fun getCryptoAssets(): List<Asset> {
        val prices = ApiClient.coingecko.getCryptoPrice(ids = cryptoList.joinToString(","))
        return cryptoList.map { coin ->
            Asset(
                name = coin.uppercase(),
                type = "crypto",
                price = prices[coin]?.get("usd")
            )
        }
    }

    suspend fun getStockAssets(): List<Asset> {
        return stockList.map { symbol ->
            val resp = ApiClient.finnhub.getStockQuote(symbol, finnhubKey)
            Asset(
                name = symbol,
                type = "stock",
                price = resp.c ?: 0.0
            )
        }
    }

    suspend fun getMetalAssets(): List<Asset> {
        val rates = ApiClient.metals.getLatestRates(
            apiKey = metalsKey,
            baseCurrency = "USD",
            unit = "toz"
        )

        return metalList.map { symbol ->
            Asset(
                name = symbol,
                type = "metal",
                price = rates.metals[symbol]
            )
        }
    }

    suspend fun getAllAssets(): List<Asset> = coroutineScope {
        val crypto = async {
            try{
                getCryptoAssets()
            } catch (e: Exception) {
                Log.e("REPOSITORY", "Greska pri dobavljanju (crypto): ${e.message}", e)
                emptyList()
            }
        }

        val stocks = async {
            try{
                getStockAssets()
            } catch (e: Exception) {
                Log.e("REPOSITORY", "Greska pri dobavljanju (stocks): ${e.message}", e)
                emptyList()
            }
        }

        val metals = async {
            try{
                getMetalAssets()
            } catch (e: Exception) {
                Log.e("REPOSITORY", "Greska pri dobavljanju (metals): ${e.message}", e)
                emptyList()
            }
        }

        crypto.await() + stocks.await() + metals.await()
    }
}