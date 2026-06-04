package com.example.portfoliomadeeasy.model

data class FinancialGoal(
    var id: String = "",
    val title: String = "",
    val targetAmount: Double = 0.0,
    val currentAmount: Double = 0.0,
    val completed: Boolean = false,
    val userId: String = ""
)