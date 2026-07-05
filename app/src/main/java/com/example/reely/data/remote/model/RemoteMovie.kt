package com.example.reely.data.remote.model

import com.example.reely.data.remote.tmdb.toTmdbPosterUrl
import com.example.reely.domain.model.Movie
import com.example.reely.domain.model.PosterPaths

data class RemoteMovie(
    val id: Int,
    val title: String,
    val overview: String,
    val posterPath: String?,
    val backdropPath: String?,
    val releaseDate: String?,
    val rating: Double?,
    val voteCount: Int?,
    val popularity: Double?,
    val runtime: Int? = null,
    val genres: List<String> = emptyList()
)

fun RemoteMovie.toDomain(): Movie {
    return Movie(
        id = id,
        title = title,
        overview = overview,
        posterPaths = PosterPaths(
            localPosterPath = null,
            remotePosterPath = posterPath?.toTmdbPosterUrl()
        ),
        releaseDate = releaseDate,
    )
}