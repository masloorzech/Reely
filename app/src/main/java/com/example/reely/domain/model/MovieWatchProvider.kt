package com.example.reely.domain.model

data class MovieWatchProviders(
    val movieId: Int,
    val countryCode: String = "PL",
    val justWatchLink: String? = null,
    val flatrate: List<WatchProvider> = emptyList(),
    val rent: List<WatchProvider> = emptyList(),
    val buy: List<WatchProvider> = emptyList(),
    val ads: List<WatchProvider> = emptyList(),
    val free: List<WatchProvider> = emptyList()
)

data class WatchProvider(
    val providerId: Int,
    val providerName: String,
    val logoPaths: LogoPaths? = null,
    val displayPriority: Int? = null
)

data class LogoPaths(
    val localLogoPath: String? = null,
    val remoteLogoPath: String? = null
)

fun LogoPaths.getAvaliable(): String?{
    return localLogoPath ?: remoteLogoPath
}