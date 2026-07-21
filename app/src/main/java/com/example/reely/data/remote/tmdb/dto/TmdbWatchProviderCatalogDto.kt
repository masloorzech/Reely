package com.example.reely.data.remote.tmdb.dto

import kotlinx.serialization.Serializable

@Serializable
data class TmdbWatchProviderCatalogDto(
    val results: List<TmdbWatchProviderDto> =
        emptyList(),
)