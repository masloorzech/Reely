package com.example.reely.data.local.mapper

import com.example.reely.data.local.model.LocalMovie
import com.example.reely.data.local.model.LocalMovieStatus
import com.example.reely.data.local.model.LocalMovieUserState
import com.example.reely.domain.model.Movie
import com.example.reely.domain.model.MovieUserState
import com.example.reely.domain.model.PosterPaths
import com.example.reely.domain.model.PublicRating
import com.example.reely.domain.model.UserId


fun LocalMovie.toDomain(): Movie {
    return Movie(
        id = id,
        title = title,
        overview = overview,
        posterPaths = PosterPaths(
            localPosterPath = localPosterPath,
            remotePosterPath = remotePosterPath
        ),
        releaseDate = releaseDate,
        publicRating = PublicRating(
            average = rating,
            voteCount = voteCount
        ),
        userStates = userStates
            .mapKeys { entry ->
                UserId(entry.key)
            }
            .mapValues { entry ->
                entry.value.toDomain()
            }
    )
}

fun LocalMovieUserState.toDomain(): MovieUserState {
    return when (status) {
        LocalMovieStatus.WATCHLIST -> MovieUserState.InWatchlist

        LocalMovieStatus.WATCHED -> MovieUserState.Watched(
            watchedAt = watchedAt,
            rating = rating
        )
    }
}

fun MovieUserState.toLocalOrNull(): LocalMovieUserState? {
    return when (this) {
        MovieUserState.NotWatched -> null

        MovieUserState.InWatchlist -> LocalMovieUserState(
            status = LocalMovieStatus.WATCHLIST
        )

        is MovieUserState.Watched -> LocalMovieUserState(
            status = LocalMovieStatus.WATCHED,
            watchedAt = watchedAt,
            rating = rating
        )
    }
}

fun Movie.toLocal(): LocalMovie {
    return LocalMovie(
        id = id,
        title = title,
        overview = overview,
        localPosterPath = posterPaths?.localPosterPath,
        remotePosterPath = posterPaths?.remotePosterPath,
        backdropPath = null,
        releaseDate = releaseDate,
        rating = publicRating?.average,
        voteCount = publicRating?.voteCount,
        popularity = null,
        runtime = runtime,
        genres = genres,
        userStates = userStates
            .mapNotNull { entry ->
                val localState = entry.value.toLocalUserState()

                if (localState != null) {
                    entry.key.value to localState
                } else {
                    null
                }
            }
            .toMap()
    )
}

private fun MovieUserState.toLocalUserState(): LocalMovieUserState? {
    return when (this) {
        MovieUserState.NotWatched -> null

        MovieUserState.InWatchlist -> LocalMovieUserState(
            status = LocalMovieStatus.WATCHLIST
        )

        is MovieUserState.Watched -> LocalMovieUserState(
            status = LocalMovieStatus.WATCHED,
            watchedAt = watchedAt,
            rating = rating
        )
    }
}