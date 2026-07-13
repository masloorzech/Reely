package com.example.reely.domain.model

data class StatsOverview(
    val totalWatchedMovies: Int = 0,
    val totalWatchTimeMinutes: Int = 0,
    val averageRating: Double? = null,
    val userStats: List<UserStatsItem> = emptyList(),
    val ratingDistribution: List<RatingDistributionItem> = emptyList()
)

data class UserStatsItem(
    val user: User,
    val watchedCount: Int,
    val ratedCount: Int,
    val averageRating: Double?,
    val topGenre: GenreStatsItem?
)

data class GenreStatsItem(
    val genreName: String,
    val watchedCount: Int,
    val percentage: Float
)

data class RatingDistributionItem(
    val rating: Int,
    val count: Int,
    val percentage: Float
)