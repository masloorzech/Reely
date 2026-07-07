package com.example.reely.data.local.model

data class LocalMovie(
    val id: Int,
    val title: String,
    val overview: String,
    val localPosterPath: String?,
    val remotePosterPath: String?,
    val backdropPath: String?,
    val releaseDate: String?,
    val rating: Double?,
    val voteCount: Int?,
    val popularity: Double?,
    val runtime: Int? = null,
    val genres: List<String> = emptyList(),
    val userStates: Map<Int, LocalMovieUserState> = emptyMap()
)

data class LocalMovieUserState(
    val status: LocalMovieStatus,
    val watchedAt: String? = null,
    val rating: Double? = null
)

enum class LocalMovieStatus {
    WATCHLIST,
    WATCHED
}