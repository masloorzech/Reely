package com.example.reely.domain.model

import java.time.LocalDate

data class MovieDetails(
    val id: Int,
    val title: String,
    val originalTitle: String?,
    val overview: String,
    val posterPath: String?,
    val backdropPath: String?,
    val releaseDate: LocalDate?,
    val runtimeMinutes: Int?,
    val originalLanguage: String?,
    val publicRating: Double?,
    val publicVoteCount: Int?,
    val genres: List<Genre>,
)