package com.example.reely.data.remote.tmdb.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TmdbMovieDetailsDto(
    val id: Int,

    val title: String? = null,

    @SerialName("original_title")
    val originalTitle: String? = null,

    val overview: String? = null,

    @SerialName("poster_path")
    val posterPath: String? = null,

    @SerialName("backdrop_path")
    val backdropPath: String? = null,

    @SerialName("release_date")
    val releaseDate: String? = null,

    @SerialName("vote_average")
    val voteAverage: Double? = null,

    @SerialName("vote_count")
    val voteCount: Int? = null,

    val popularity: Double? = null,

    val runtime: Int? = null,

    val genres: List<TmdbGenreDto> = emptyList(),

    val tagline: String? = null,

    val status: String? = null,

    val adult: Boolean? = null,

    @SerialName("original_language")
    val originalLanguage: String? = null
)

@Serializable
data class TmdbGenreDto(
    val id: Int,
    val name: String
)