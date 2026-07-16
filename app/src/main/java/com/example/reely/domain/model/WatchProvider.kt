package com.example.reely.domain.model

data class MovieWatchProviders(
    val movieId: Int,
    val countryCode: String,
    val justWatchLink: String?,
    val flatrate: List<WatchProvider>,
    val rent: List<WatchProvider>,
    val buy: List<WatchProvider>,
    val ads: List<WatchProvider>,
    val free: List<WatchProvider>,
)

data class WatchProvider(
    val providerId: Int,
    val providerName: String,
    val logoPath: String?,
    val displayPriority: Int?,
)