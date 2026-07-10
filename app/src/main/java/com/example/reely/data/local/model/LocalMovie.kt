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
    val genres: List<String> = emptyList()
)

data class LocalMovieUserWatch(
    val watchedAt: String? = null,
    val rating: Double? = null
)
data class LocalWatchlistStatus(
    val isAdded: Boolean,
    val addedAt: String?
)
data class LocalMovieWithUserData(
    val movie: LocalMovie,
    val watchList: LocalWatchlistStatus,
    val userWatches: Map<Int, LocalMovieUserWatch>
)