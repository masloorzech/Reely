package com.example.reely.domain.model

import java.time.LocalDate

data class RecentWatchedMovie(
    val viewingId: Long,
    val movieId: Int,
    val title: String,
    val posterPath: String?,
    val releaseDate: LocalDate?,
    val watchedAt: LocalDate,
    val runtime: Int?,
    val overview: String,
    val averageUserRating: Double?,
    val genres: List<Genre>,
)

fun RecentWatchedMovie.toPreview() : MoviePreview{
    return MoviePreview(
        id = movieId,
        title = title,
        posterPath = posterPath,
        releaseDate = releaseDate
    )
}