package com.example.reely.data.local.room.mapper



import com.example.reely.data.local.model.LocalMovie
import com.example.reely.data.local.model.LocalMovieUserWatch
import com.example.reely.data.local.room.entity.MovieEntity
import com.example.reely.data.local.room.entity.MovieUserWatchEntity

fun LocalMovie.toEntity(): MovieEntity {
    return MovieEntity(
        id = id,
        title = title,
        overview = overview,
        localPosterPath = localPosterPath,
        remotePosterPath = remotePosterPath,
        backdropPath = backdropPath,
        releaseDate = releaseDate,
        rating = rating,
        voteCount = voteCount,
        popularity = popularity,
        runtime = runtime
    )
}

fun MovieEntity.toLocalMovie(): LocalMovie {
    return LocalMovie(
        id = id,
        title = title,
        overview = overview,
        localPosterPath = localPosterPath,
        remotePosterPath = remotePosterPath,
        backdropPath = backdropPath,
        releaseDate = releaseDate,
        rating = rating,
        voteCount = voteCount,
        popularity = popularity,
        runtime = runtime,
        genres = emptyList()
    )
}

fun LocalMovieUserWatch.toEntity(
    movieId: Int,
    userId: Int
): MovieUserWatchEntity {
    return MovieUserWatchEntity(
        movieId = movieId,
        userId = userId,
        watchedAt = watchedAt,
        rating = rating
    )
}

fun MovieUserWatchEntity.toLocalUserWatch(): LocalMovieUserWatch {
    return LocalMovieUserWatch(
        watchedAt = watchedAt,
        rating = rating
    )
}