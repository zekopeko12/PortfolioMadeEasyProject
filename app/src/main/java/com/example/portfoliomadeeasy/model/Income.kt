package com.example.portfoliomadeeasy.model

import java.util.Date

data class Income(
    var id: String = "",
    val title: String = "",
    val amount: Double = 0.0,
    val date: Date = Date(),
    val userId: String = ""
)