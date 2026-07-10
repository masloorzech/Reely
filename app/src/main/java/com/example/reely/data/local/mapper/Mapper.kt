package com.example.reely.data.local.mapper

import com.example.reely.data.local.model.LocalMovie
import com.example.reely.data.local.model.LocalMovieUserWatch
import com.example.reely.data.local.model.LocalMovieWithUserData
import com.example.reely.data.local.model.LocalUser
import com.example.reely.domain.model.Movie
import com.example.reely.domain.model.PosterPaths
import com.example.reely.domain.model.PublicRating
import com.example.reely.domain.model.User
import com.example.reely.domain.model.UserId
import com.example.reely.domain.model.UserMovieWatch
import com.example.reely.domain.model.UserWatchListStatus

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
        genres = genres
    )
}

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
        runtime = runtime,
        genres = genres,
        userWatches = emptyMap()
    )
}

fun LocalMovieWithUserData.toDomain(): Movie {
    return movie.toDomain().copy(
        userWatchlistStatus = UserWatchListStatus(
            isInWatchlist = watchList.isAdded,
            addedAt = watchList.addedAt
        ),
        userWatches = userWatches
            .mapKeys { entry -> UserId(entry.key) }
            .mapValues { entry -> entry.value.toDomain() }
    )
}

fun LocalMovieUserWatch.toDomain(): UserMovieWatch {
    return UserMovieWatch(
        watchedAt = watchedAt,
        rating = rating
    )
}

fun UserMovieWatch.toLocal(): LocalMovieUserWatch {
    return LocalMovieUserWatch(
        watchedAt = watchedAt,
        rating = rating
    )
}

fun LocalUser.toDomain(): User {
    return User(
        id = UserId(id),
        nickname = nickname,
        avatarPath = avatarPath
    )
}