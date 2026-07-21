package com.example.reely.data.remote.tmdb.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TmdbMovieWatchProvidersDto(
    val id: Int,
    val results: Map<String, TmdbWatchProviderRegionDto> = emptyMap()
)

@Serializable
data class TmdbWatchProviderRegionDto(
    val link: String? = null,

    val flatrate: List<TmdbWatchProviderDto> = emptyList(),

    val rent: List<TmdbWatchProviderDto> = emptyList(),

    val buy: List<TmdbWatchProviderDto> = emptyList(),

    val ads: List<TmdbWatchProviderDto> = emptyList(),

    val free: List<TmdbWatchProviderDto> = emptyList()
)

@Serializable
data class TmdbWatchProviderDto(
    @SerialName("provider_id")
    val providerId: Int,

    @SerialName("provider_name")
    val providerName: String? = null,

    @SerialName("logo_path")
    val logoPath: String? = null,

    @SerialName("display_priority")
    val displayPriority: Int? = null
)