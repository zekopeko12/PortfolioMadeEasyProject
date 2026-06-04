package com.example.portfoliomadeeasy.model

import com.example.portfoliomadeeasy.remote.model.Asset

data class AssetState(
    val assets: List<Asset> = emptyList(),
    val userAssets: List<UserAsset> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)