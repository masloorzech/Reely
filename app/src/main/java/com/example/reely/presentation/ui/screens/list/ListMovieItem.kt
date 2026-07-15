package com.example.reely.presentation.ui.screens.list

import com.example.reely.domain.model.Genre
import com.example.reely.domain.model.MoviePreview
import com.example.reely.domain.model.RecentWatchedMovie
import com.example.reely.domain.model.WatchlistEntry
import java.time.Instant
import java.time.LocalDate

data class ListMovieItem(
    val id: Int,
    val title: String,
    val posterPath: String?,
    val genres: List<Genre>,
    val releaseDate: LocalDate?,
    val overview: String?,
    val runtime: Int?,
    val publicRating: Double?,
    val userRating: Double?,
    val addedAt: Instant?,
    val watchedAt: LocalDate?,
) {
    val displayedRating: Double?
        get() = userRating ?: publicRating

    val genresText: String
        get() = genres
            .map { genre -> genre.name.trim() }
            .filter { name -> name.isNotEmpty() }
            .joinToString(separator = " • ")
}

internal fun WatchlistEntry.toListMovieItem(): ListMovieItem {
    return ListMovieItem(
        id = movie.id,
        title = movie.title,
        posterPath = movie.posterPath,
        genres = movie.genres,
        releaseDate = movie.releaseDate,
        publicRating = movie.publicRating,
        userRating = null,
        addedAt = addedAt,
        runtime = movie.runtimeMinutes,
        overview = movie.overview,
        watchedAt = null,
    )
}

internal fun RecentWatchedMovie.toListMovieItem(): ListMovieItem {
    return ListMovieItem(
        id = movieId,
        title = title,
        posterPath = posterPath,
        genres = genres,
        releaseDate = releaseDate,
        publicRating = publicRating,
        userRating = averageUserRating,
        addedAt = null,
        runtime = runtime,
        overview = overview,
        watchedAt = watchedAt,
    )
}


internal fun ListMovieItem.toPreview() : MoviePreview{
    return MoviePreview(
        id = id,
        title = title,
        posterPath = posterPath,
        releaseDate = releaseDate
    )
}
