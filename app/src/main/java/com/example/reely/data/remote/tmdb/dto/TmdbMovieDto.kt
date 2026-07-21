package com.example.reely.data.remote.tmdb.dto


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TmdbMovieDto(
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

    @SerialName("genre_ids")
    val genreIds: List<Int> = emptyList(),

    val adult: Boolean? = null,

    @SerialName("original_language")
    val originalLanguage: String? = null
)