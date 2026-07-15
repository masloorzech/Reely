package com.example.reely.data.remote.model

data class RemoteMovieDetails(
    val id: Int,
    val title: String?,
    val originalTitle: String?,
    val overview: String?,
    val posterPath: String?,
    val backdropPath: String?,
    val releaseDate: String?,
    val runtime: Int?,
    val originalLanguage: String?,
    val voteAverage: Double?,
    val voteCount: Int?,
    val genres: List<RemoteGenre>?,
)
