package com.example.portfoliomadeeasy.model

data class UserAsset(
    var id: String = "",
    val name: String = "",
    val type: String = "",
    var buyPrice: Double = 0.0,
    val currentPrice: Double = 0.0,
    var quantity: Double = 0.0,
    val userId: String = ""
) {
    val totalValue: Double
        get() = (currentPrice) * quantity

    val profitLoss: Double
        get() = totalValue - (buyPrice * quantity)

    val profitLossPercent: Double
        get() = if (buyPrice > 0.0) (profitLoss / (buyPrice * quantity )) * 100 else 0.0
}