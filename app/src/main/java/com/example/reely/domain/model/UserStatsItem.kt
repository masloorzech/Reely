package com.example.reely.domain.model

data class StatsOverview(
    val totalWatchedMovies: Int = 0,
    val totalRatedMovies: Int = 0,
    val averageRating: Double? = null,
    val topGenre: GenreStatsItem? = null,
    val userStats: List<UserStatsItem> = emptyList(),
    val genreStats: List<GenreStatsItem> = emptyList(),
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