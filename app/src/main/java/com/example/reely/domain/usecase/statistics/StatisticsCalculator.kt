package com.example.reely.domain.usecase.statistics

import com.example.reely.domain.model.GenreStatistics
import com.example.reely.domain.model.MonthlyActivityStatistics
import com.example.reely.domain.model.MovieRatingComparison
import com.example.reely.domain.model.Profile
import com.example.reely.domain.model.ProfileStatistics
import com.example.reely.domain.model.ReelyStatistics
import com.example.reely.domain.model.StatisticsMovie
import com.example.reely.domain.model.StatisticsSourceData
import com.example.reely.domain.model.TimeFunFact
import java.time.YearMonth
import java.util.Locale
import javax.inject.Inject
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.roundToInt

class StatisticsCalculator @Inject constructor() {

    fun calculate(
        sourceData: StatisticsSourceData,
        profiles: List<Profile>,
        currentMonth: YearMonth,
    ): ReelyStatistics {
        val sortedProfiles = profiles.sortedBy { profile ->
            profile.position.ordinal
        }

        val moviesById = sourceData.movies.associateBy { movie ->
            movie.id
        }

        val viewingsByMovieId = sourceData.viewings.groupBy { viewing ->
            viewing.movieId
        }

        val watchedMovieIds = viewingsByMovieId.keys

        val ratingsByMovieId = sourceData.ratings
            .filter { rating ->
                rating.movieId in watchedMovieIds
            }
            .groupBy { rating ->
                rating.movieId
            }

        val statisticsMovies = watchedMovieIds.mapNotNull { movieId ->
            val movie = moviesById[movieId]
                ?: return@mapNotNull null

            val movieRatings = ratingsByMovieId[movieId]
                .orEmpty()
                .associate { rating ->
                    rating.profileId to rating.rating.value
                }

            StatisticsMovie(
                movieId = movie.id,
                title = movie.title,
                posterPath = movie.posterPath,
                runtimeMinutes = movie.runtimeMinutes,
                viewingCount = viewingsByMovieId[movieId].orEmpty().size,
                genres = movie.genres,
                ratings = movieRatings,
                togetherAverageRating = movieRatings.values
                    .takeIf { ratings ->
                        ratings.size >= 2
                    }
                    ?.average(),
            )
        }

        val totalWatchedMinutes = calculateWatchedMinutes(
            sourceData = sourceData,
            moviesById = moviesById,
        )

        val unknownWatchedRuntimeCount = sourceData.viewings.count { viewing ->
            moviesById[viewing.movieId]
                ?.runtimeMinutes
                ?.takeIf { runtime ->
                    runtime > 0
                } == null
        }

        val watchlistMovies = sourceData.watchlistMovieIds.mapNotNull { movieId ->
            moviesById[movieId]
        }

        val watchlistMinutes = watchlistMovies.sumOf { movie ->
            movie.runtimeMinutes
                ?.takeIf { runtime ->
                    runtime > 0
                }
                ?.toLong()
                ?: 0L
        }

        val unknownWatchlistRuntimeCount = watchlistMovies.count { movie ->
            movie.runtimeMinutes
                ?.takeIf { runtime ->
                    runtime > 0
                } == null
        }

        val watchedScores = sourceData.ratings
            .asSequence()
            .filter { rating ->
                rating.movieId in watchedMovieIds
            }
            .map { rating ->
                rating.rating.value
            }
            .toList()

        val profileStatistics = buildProfileStatistics(
            profiles = sortedProfiles,
            movies = statisticsMovies,
        )

        val genreStatistics = buildGenreStatistics(
            profiles = sortedProfiles,
            movies = statisticsMovies,
        )

        val ratingComparisons = buildRatingComparisons(
            profiles = sortedProfiles,
            movies = statisticsMovies,
        )

        val monthlyActivity = buildMonthlyActivity(
            sourceData = sourceData,
            moviesById = moviesById,
            currentMonth = currentMonth,
        )

        val allMonthlyActivity = buildAllMonthlyActivity(
            sourceData = sourceData,
            moviesById = moviesById,
        )

        val topTogetherMovies = statisticsMovies
            .filter { movie ->
                movie.togetherAverageRating != null
            }
            .sortedWith(
                compareByDescending<StatisticsMovie> { movie ->
                    movie.togetherAverageRating ?: 0.0
                }
                    .thenByDescending { movie ->
                        movie.viewingCount
                    }
                    .thenBy { movie ->
                        movie.title
                    },
            )
            .take(TOP_MOVIES_LIMIT)

        return ReelyStatistics(
            uniqueMoviesCount = statisticsMovies.size,
            viewingsCount = sourceData.viewings.size,
            rewatchCount = max(
                sourceData.viewings.size - statisticsMovies.size,
                0,
            ),
            totalWatchedMinutes = totalWatchedMinutes,
            watchlistMovieCount = watchlistMovies.size,
            watchlistMinutes = watchlistMinutes,
            unknownWatchedRuntimeCount = unknownWatchedRuntimeCount,
            unknownWatchlistRuntimeCount = unknownWatchlistRuntimeCount,
            togetherAverageRating = watchedScores
                .takeIf { scores ->
                    scores.isNotEmpty()
                }
                ?.average(),
            profiles = profileStatistics,
            genres = genreStatistics,
            topTogetherMovies = topTogetherMovies,
            biggestDisagreements = ratingComparisons
                .sortedWith(
                    compareByDescending<MovieRatingComparison> { comparison ->
                        comparison.difference
                    }
                        .thenByDescending { comparison ->
                            comparison.movie.togetherAverageRating ?: 0.0
                        }
                        .thenBy { comparison ->
                            comparison.movie.title
                        },
                )
                .take(DISAGREEMENTS_LIMIT),
            comparedMoviesCount = ratingComparisons.size,
            identicalRatingsCount = ratingComparisons.count { comparison ->
                comparison.difference == 0
            },
            averageRatingDifference = ratingComparisons
                .map { comparison ->
                    comparison.difference
                }
                .takeIf { differences ->
                    differences.isNotEmpty()
                }
                ?.average(),
            monthlyActivity = monthlyActivity,
            mostActiveMonth = allMonthlyActivity.maxWithOrNull(
                compareBy<MonthlyActivityStatistics> { month ->
                    month.viewingsCount
                }.thenBy { month ->
                    month.watchedMinutes
                },
            ),
            longestWatchedMovie = statisticsMovies
                .filter { movie ->
                    (movie.runtimeMinutes ?: 0) > 0
                }
                .maxByOrNull { movie ->
                    movie.runtimeMinutes ?: 0
                },
            watchedTimeFunFact = buildWatchedTimeFunFact(
                totalMinutes = totalWatchedMinutes,
            ),
        )
    }

    private fun calculateWatchedMinutes(
        sourceData: StatisticsSourceData,
        moviesById: Map<Int, com.example.reely.domain.model.StatisticsSourceMovie>,
    ): Long {
        return sourceData.viewings.sumOf { viewing ->
            moviesById[viewing.movieId]
                ?.runtimeMinutes
                ?.takeIf { runtime ->
                    runtime > 0
                }
                ?.toLong()
                ?: 0L
        }
    }

    private fun buildProfileStatistics(
        profiles: List<Profile>,
        movies: List<StatisticsMovie>,
    ): List<ProfileStatistics> {
        return profiles.map { profile ->
            val ratedMovies = movies.filter { movie ->
                profile.id in movie.ratings
            }

            val scores = ratedMovies.mapNotNull { movie ->
                movie.ratings[profile.id]
            }

            ProfileStatistics(
                profile = profile,
                averageRating = scores
                    .takeIf { values ->
                        values.isNotEmpty()
                    }
                    ?.average(),
                ratedMoviesCount = scores.size,
                ratingDistribution = (1..10).associateWith { score ->
                    scores.count { value ->
                        value == score
                    }
                },
                topMovies = ratedMovies
                    .sortedWith(
                        compareByDescending<StatisticsMovie> { movie ->
                            movie.ratings[profile.id] ?: 0
                        }
                            .thenByDescending { movie ->
                                movie.viewingCount
                            }
                            .thenBy { movie ->
                                movie.title
                            },
                    )
                    .take(TOP_MOVIES_LIMIT),
                lowestRatedMovies = ratedMovies
                    .sortedWith(
                        compareBy<StatisticsMovie> { movie ->
                            movie.ratings[profile.id] ?: Int.MAX_VALUE
                        }
                            .thenBy { movie ->
                                movie.title
                            },
                    )
                    .take(LOWEST_MOVIES_LIMIT),
            )
        }
    }

    private fun buildGenreStatistics(
        profiles: List<Profile>,
        movies: List<StatisticsMovie>,
    ): List<GenreStatistics> {
        val allGenres = movies
            .flatMap { movie ->
                movie.genres
            }
            .distinctBy { genre ->
                genre.id
            }

        return allGenres.map { genre ->
            val moviesInGenre = movies.filter { movie ->
                movie.genres.any { movieGenre ->
                    movieGenre.id == genre.id
                }
            }

            val profileAverageRatings = profiles.mapNotNull { profile ->
                val scores = moviesInGenre.mapNotNull { movie ->
                    movie.ratings[profile.id]
                }

                if (scores.isEmpty()) {
                    null
                } else {
                    profile.id to scores.average()
                }
            }.toMap()

            GenreStatistics(
                genre = genre,
                uniqueMoviesCount = moviesInGenre.size,
                viewingsCount = moviesInGenre.sumOf { movie ->
                    movie.viewingCount
                },
                watchedMinutes = moviesInGenre.sumOf { movie ->
                    val runtime = movie.runtimeMinutes
                        ?.takeIf { value ->
                            value > 0
                        }
                        ?.toLong()
                        ?: 0L

                    runtime * movie.viewingCount
                },
                togetherAverageRating = moviesInGenre
                    .mapNotNull { movie ->
                        movie.togetherAverageRating
                    }
                    .takeIf { averages ->
                        averages.isNotEmpty()
                    }
                    ?.average(),
                profileAverageRatings = profileAverageRatings,
            )
        }
            .filter { genre ->
                genre.uniqueMoviesCount > 0
            }
            .sortedWith(
                compareByDescending<GenreStatistics> { genre ->
                    genre.uniqueMoviesCount
                }
                    .thenByDescending { genre ->
                        genre.viewingsCount
                    }
                    .thenBy { genre ->
                        genre.genre.name
                    },
            )
    }

    private fun buildRatingComparisons(
        profiles: List<Profile>,
        movies: List<StatisticsMovie>,
    ): List<MovieRatingComparison> {
        val firstProfile = profiles.getOrNull(0)
            ?: return emptyList()

        val secondProfile = profiles.getOrNull(1)
            ?: return emptyList()

        return movies.mapNotNull { movie ->
            val firstRating = movie.ratings[firstProfile.id]
                ?: return@mapNotNull null

            val secondRating = movie.ratings[secondProfile.id]
                ?: return@mapNotNull null

            MovieRatingComparison(
                movie = movie,
                firstProfileRating = firstRating,
                secondProfileRating = secondRating,
                difference = abs(firstRating - secondRating),
            )
        }
    }

    private fun buildMonthlyActivity(
        sourceData: StatisticsSourceData,
        moviesById: Map<Int, com.example.reely.domain.model.StatisticsSourceMovie>,
        currentMonth: YearMonth,
    ): List<MonthlyActivityStatistics> {
        val activityByMonth = buildAllMonthlyActivity(
            sourceData = sourceData,
            moviesById = moviesById,
        ).associateBy { activity ->
            activity.month
        }

        return (MONTHS_VISIBLE - 1 downTo 0).map { monthsAgo ->
            val month = currentMonth.minusMonths(monthsAgo.toLong())

            activityByMonth[month] ?: MonthlyActivityStatistics(
                month = month,
                viewingsCount = 0,
                watchedMinutes = 0,
            )
        }
    }

    private fun buildAllMonthlyActivity(
        sourceData: StatisticsSourceData,
        moviesById: Map<Int, com.example.reely.domain.model.StatisticsSourceMovie>,
    ): List<MonthlyActivityStatistics> {
        return sourceData.viewings
            .groupBy { viewing ->
                YearMonth.from(viewing.watchedAt)
            }
            .map { (month, viewings) ->
                MonthlyActivityStatistics(
                    month = month,
                    viewingsCount = viewings.size,
                    watchedMinutes = viewings.sumOf { viewing ->
                        moviesById[viewing.movieId]
                            ?.runtimeMinutes
                            ?.takeIf { runtime ->
                                runtime > 0
                            }
                            ?.toLong()
                            ?: 0L
                    },
                )
            }
    }

    private fun buildWatchedTimeFunFact(
        totalMinutes: Long,
    ): TimeFunFact {
        val polishLocale = Locale.forLanguageTag("pl-PL")

        return when {
            totalMinutes <= 0L -> {
                TimeFunFact(
                    headline = "Wasza filmowa podróż dopiero się zaczyna",
                    description = "Obejrzyjcie pierwszy film, a Reely przeliczy wspólny czas.",
                )
            }

            totalMinutes >= MOON_TRIP_MINUTES -> {
                val moonTrips = totalMinutes.toDouble() / MOON_TRIP_MINUTES

                val formattedTrips = String.format(
                    polishLocale,
                    "%.1f",
                    moonTrips,
                )

                TimeFunFact(
                    headline = "To około $formattedTrips podróży na Księżyc",
                    description = "Licząc około 3 dni lotu w jedną stronę — bez przesiadek i napisów końcowych.",
                )
            }

            totalMinutes >= FULL_DAY_MINUTES -> {
                val days = totalMinutes.toDouble() / FULL_DAY_MINUTES

                val formattedDays = String.format(
                    polishLocale,
                    "%.1f",
                    days,
                )

                TimeFunFact(
                    headline = "$formattedDays dnia oglądania bez przerwy",
                    description = "Tyle czasu spędziliście razem w filmowych światach.",
                )
            }

            totalMinutes >= WORKDAY_MINUTES -> {
                val workdays = (
                        totalMinutes.toDouble() / WORKDAY_MINUTES
                        ).roundToInt()

                TimeFunFact(
                    headline = "To prawie $workdays ośmiogodzinnych dni pracy",
                    description = "Tylko zdecydowanie przyjemniejszych i z większą ilością popcornu.",
                )
            }

            else -> {
                val evenings = max(
                    (
                            totalMinutes.toDouble() /
                                    MOVIE_EVENING_MINUTES
                            ).roundToInt(),
                    1,
                )

                TimeFunFact(
                    headline = "To około $evenings filmowych wieczorów",
                    description = "Licząc po dwie godziny na jeden wspólny seans.",
                )
            }
        }
    }

    private companion object {
        const val TOP_MOVIES_LIMIT = 10
        const val LOWEST_MOVIES_LIMIT = 5
        const val DISAGREEMENTS_LIMIT = 5
        const val MONTHS_VISIBLE = 12

        const val MOVIE_EVENING_MINUTES = 120.0
        const val WORKDAY_MINUTES = 8.0 * 60.0
        const val FULL_DAY_MINUTES = 24.0 * 60.0

        const val MOON_TRIP_MINUTES = 3.0 * 24.0 * 60.0
    }
}