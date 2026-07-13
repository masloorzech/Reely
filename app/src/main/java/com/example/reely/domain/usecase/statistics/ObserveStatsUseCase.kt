package com.example.reely.domain.usecase.statistics

import com.example.reely.domain.model.GenreStatsItem
import com.example.reely.domain.model.RatingDistributionItem
import com.example.reely.domain.model.StatsOverview
import com.example.reely.domain.model.UserStatsItem
import com.example.reely.domain.repository.MoviesRepository
import com.example.reely.domain.repository.UsersRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlin.math.roundToInt
import javax.inject.Inject

class ObserveStatsUseCase @Inject constructor(
    private val usersRepository: UsersRepository,
    private val moviesRepository: MoviesRepository
) {
    operator fun invoke(): Flow<StatsOverview> {
        return combine(
            usersRepository.observeUsers(),
            moviesRepository.observeWatchedMovies()
        ) { users, watchedMovies ->

            val mainUser = users.firstOrNull()
                ?: return@combine StatsOverview()

            val mainUserMovies = watchedMovies.filter { movie ->
                movie.userWatches.containsKey(mainUser.id)
            }

            val mainUserRatings = mainUserMovies.mapNotNull { movie ->
                movie.userWatches[mainUser.id]?.rating
            }

            val totalWatchTimeMinutes = mainUserMovies.sumOf { movie ->
                movie.runtime ?: 0
            }

            val userStats = users.map { user ->
                val userWatchedMovies = watchedMovies.filter { movie ->
                    movie.userWatches.containsKey(user.id)
                }

                val userRatings = userWatchedMovies.mapNotNull { movie ->
                    movie.userWatches[user.id]?.rating
                }

                UserStatsItem(
                    user = user,
                    watchedCount = userWatchedMovies.size,
                    ratedCount = userRatings.size,
                    averageRating = userRatings.averageOrNull(),
                    topGenre = null
                )
            }

            StatsOverview(
                totalWatchedMovies = mainUserMovies.size,
                totalWatchTimeMinutes = totalWatchTimeMinutes,
                averageRating = mainUserRatings.averageOrNull(),
                userStats = userStats,
                ratingDistribution = mainUserRatings.buildRatingDistribution()
            )
        }
    }
}

private fun List<String>.buildGenreStats(): List<GenreStatsItem> {
    if (isEmpty()) return emptyList()

    val total = size.toFloat()

    return groupingBy { genreName ->
        genreName
    }
        .eachCount()
        .map { (genreName, count) ->
            GenreStatsItem(
                genreName = genreName,
                watchedCount = count,
                percentage = count / total
            )
        }
        .sortedByDescending { genreStatsItem ->
            genreStatsItem.watchedCount
        }
}

private fun List<Double>.buildRatingDistribution(): List<RatingDistributionItem> {
    if (isEmpty()) return emptyList()

    val roundedRatings = map { rating ->
        rating.roundToInt()
    }

    val total = roundedRatings.size.toFloat()

    return (1..5).map { rating ->
        val count = roundedRatings.count { roundedRating ->
            roundedRating == rating
        }

        RatingDistributionItem(
            rating = rating,
            count = count,
            percentage = count / total
        )
    }.sortedByDescending { ratingDistributionItem ->
        ratingDistributionItem.rating
    }
}

private fun List<Double>.averageOrNull(): Double? {
    return if (isEmpty()) null else average()
}