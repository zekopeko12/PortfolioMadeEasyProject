package com.example.portfoliomadeeasy.remote.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class FinnhubQuoteResponse (
    val c: Double?,
    val h: Double?,
    val l: Double?,
    val o: Double?,
    val pc: Double?,
    val t: Long?
)