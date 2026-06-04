package com.example.portfoliomadeeasy.remote.model

import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class MetalsDevLatestResponse(
    val status: String,
    val currency: String,
    val unit: String,
    val metals: Map<String, Double>,
    val currencies: Map<String, Double>,
    val timestamp: String? = null
)