package com.example.reely.data.local.room.mapper

import com.example.reely.data.local.model.LocalMovie
import com.example.reely.data.local.model.LocalMovieStatus
import com.example.reely.data.local.model.LocalMovieUserState
import com.example.reely.data.local.room.entity.MovieEntity
import com.example.reely.data.local.room.entity.MovieUserStateEntity

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

fun MovieEntity.toLocalMovie(
    userStates: List<MovieUserStateEntity> = emptyList()
): LocalMovie {
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
        genres = emptyList(),
        userStates = userStates
            .mapNotNull { entity ->
                val localUserState = entity.toLocalUserStateOrNull()

                if (localUserState != null) {
                    entity.userId to localUserState
                } else {
                    null
                }
            }
            .toMap()
    )
}

fun LocalMovieUserState.toEntity(
    movieId: Int,
    userId: Int
): MovieUserStateEntity {
    return MovieUserStateEntity(
        movieId = movieId,
        userId = userId,
        status = status.name,
        watchedAt = watchedAt,
        rating = rating
    )
}

private fun MovieUserStateEntity.toLocalUserStateOrNull(): LocalMovieUserState? {
    val localStatus = runCatching {
        LocalMovieStatus.valueOf(status)
    }.getOrNull() ?: return null

    return LocalMovieUserState(
        status = localStatus,
        watchedAt = watchedAt,
        rating = rating
    )
}