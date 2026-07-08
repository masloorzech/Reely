package com.example.reely.domain.model

import androidx.core.R
import kotlin.math.round

data class Movie(
    val id: Int,
    val title: String,
    val overview: String,
    val posterPaths: PosterPaths?,
    val releaseDate: String?,
    val publicRating: PublicRating?,
    val runtime: Int? = null,
    val genres: List<String> = emptyList(),
    val userStates: Map<UserId, MovieUserState> = emptyMap()
)

sealed interface MovieUserState {
    data object NotWatched : MovieUserState
    data object InWatchlist : MovieUserState

    data class Watched(
        val watchedAt: String? = null,
        val rating: Double? = null
    ) : MovieUserState {
        init {
            require(rating == null || rating in 0.0..10.0) {
                "Rating must be between 0.0 and 10.0"
            }
        }
    }
}

data class PublicRating(
    val average: Double?,
    val voteCount: Int?
)

data class PosterPaths(
    val localPosterPath: String?,
    val remotePosterPath: String?,
)

fun Movie.userState(userId: UserId): MovieUserState {
    return userStates[userId] ?: MovieUserState.NotWatched
}

fun Movie.userRating(userId: UserId): Double? {
    return when (val state = userState(userId)) {
        is MovieUserState.Watched -> state.rating
        else -> null
    }
}

fun Movie.ourAverageRatingRounded(): Double? {
    val ratings = userStates.values
        .filterIsInstance<MovieUserState.Watched>()
        .mapNotNull { it.rating }

    if (ratings.isEmpty()) return null

    return round(ratings.average() * 10) / 10
}

fun Movie.isWatchedBy(userId: UserId): Boolean {
    return userState(userId) is MovieUserState.Watched
}

fun Movie.getAvailablePosterPath() : String?{
    return posterPaths?.localPosterPath?:posterPaths?.remotePosterPath
}

fun Movie.isInWatchlist(): Boolean {
    return userState(SHARED_WATCHLIST_USER_ID) is MovieUserState.InWatchlist
}

fun Movie.runtimeText(): String? {
    val runtime = runtime ?: return null

    val hours = runtime / 60
    val minutes = runtime % 60

    return if (hours > 0) {
        "${hours}h ${minutes}m"
    } else {
        "${minutes}m"
    }
}
fun Movie.genresText(): String {
    return genres.joinToString(" · ")
}