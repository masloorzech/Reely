package com.example.reely.domain.model

import com.example.reely.presentation.ui.screens.list.MovieListTab
import com.example.reely.presentation.ui.screens.list.MovieSortOption

data class Movie(
    val id: Int,
    val title: String,
    val overview: String,
    val posterPaths: PosterPaths?,
    val releaseDate: String?,
    val publicRating: PublicRating?,
    val runtime: Int? = null,
    val genres: List<String> = emptyList(),
    val isInWatchlist: Boolean = false,
    val userWatches: Map<UserId, UserMovieWatch> = emptyMap()
)
data class UserMovieWatch(
    val watchedAt: String? = null,
    val rating: Double? = null
)

data class PublicRating(
    val average: Double?,
    val voteCount: Int?
)

data class PosterPaths(
    val localPosterPath: String?,
    val remotePosterPath: String?,
)


fun Movie.getAvailablePosterPath(): String? {
    return posterPaths?.localPosterPath ?: posterPaths?.remotePosterPath
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

fun Movie.isWatched(): Boolean {
    return userWatches.isNotEmpty()
}

fun Movie.ourAverageRatingRounded(): Double? {
    val ratings = userWatches.values
        .mapNotNull { userWatch ->
            userWatch.rating
        }

    if (ratings.isEmpty()) return null

    return kotlin.math.round(ratings.average() * 10) / 10
}

fun Movie.latestWatchedAt(): String? {
    return userWatches.values
        .mapNotNull { userWatch ->
            userWatch.watchedAt
        }
        .maxOrNull()
}
