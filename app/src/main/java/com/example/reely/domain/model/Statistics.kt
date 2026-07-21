package com.example.reely.domain.model

import java.time.YearMonth

data class StatisticsSourceData(
    val movies: List<StatisticsSourceMovie>,
    val viewings: List<MovieViewing>,
    val ratings: List<MovieRating>,
    val watchlistMovieIds: Set<Int>,
)

data class StatisticsSourceMovie(
    val id: Int,
    val title: String,
    val posterPath: String?,
    val runtimeMinutes: Int?,
    val genres: List<Genre>,
)

data class ReelyStatistics(
    val uniqueMoviesCount: Int,
    val viewingsCount: Int,
    val rewatchCount: Int,

    val totalWatchedMinutes: Long,

    val watchlistMovieCount: Int,
    val watchlistMinutes: Long,

    val unknownWatchedRuntimeCount: Int,
    val unknownWatchlistRuntimeCount: Int,

    val togetherAverageRating: Double?,

    val profiles: List<ProfileStatistics>,
    val genres: List<GenreStatistics>,

    val topTogetherMovies: List<StatisticsMovie>,
    val biggestDisagreements: List<MovieRatingComparison>,

    val comparedMoviesCount: Int,
    val identicalRatingsCount: Int,
    val averageRatingDifference: Double?,

    val monthlyActivity: List<MonthlyActivityStatistics>,
    val mostActiveMonth: MonthlyActivityStatistics?,

    val longestWatchedMovie: StatisticsMovie?,

    val watchedTimeFunFact: TimeFunFact,
) {
    val hasWatchedMovies: Boolean
        get() = viewingsCount > 0
}

data class StatisticsMovie(
    val movieId: Int,
    val title: String,
    val posterPath: String?,
    val runtimeMinutes: Int?,

    val viewingCount: Int,

    val genres: List<Genre>,

    val ratings: Map<ProfileId, Int>,

    val togetherAverageRating: Double?,
)

data class ProfileStatistics(
    val profile: Profile,

    val averageRating: Double?,
    val ratedMoviesCount: Int,

    val ratingDistribution: Map<Int, Int>,

    val topMovies: List<StatisticsMovie>,
    val lowestRatedMovies: List<StatisticsMovie>,
)

data class GenreStatistics(
    val genre: Genre,

    val uniqueMoviesCount: Int,
    val viewingsCount: Int,
    val watchedMinutes: Long,

    val togetherAverageRating: Double?,

    val profileAverageRatings: Map<ProfileId, Double>,
)

data class MovieRatingComparison(
    val movie: StatisticsMovie,

    val firstProfileRating: Int,
    val secondProfileRating: Int,

    val difference: Int,
)

data class MonthlyActivityStatistics(
    val month: YearMonth,
    val viewingsCount: Int,
    val watchedMinutes: Long,
)

data class TimeFunFact(
    val headline: String,
    val description: String,
)