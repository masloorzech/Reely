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
        if (totalMinutes <= 0L) {
            return TimeFunFact(
                headline = "Wasza filmowa podróż dopiero się zaczyna",
                description = "Obejrzyjcie pierwszy film, a Reely przeliczy wspólny czas.",
            )
        }

        val polishLocale = Locale.forLanguageTag("pl-PL")

        val coffeeBreaks = approximateCount(
            totalMinutes = totalMinutes,
            unitMinutes = COFFEE_BREAK_MINUTES,
        )

        val seriesEpisodes = approximateCount(
            totalMinutes = totalMinutes,
            unitMinutes = SERIES_EPISODE_MINUTES,
        )

        val podcasts = approximateCount(
            totalMinutes = totalMinutes,
            unitMinutes = PODCAST_EPISODE_MINUTES,
        )

        val footballMatches = approximateCount(
            totalMinutes = totalMinutes,
            unitMinutes = FOOTBALL_MATCH_MINUTES,
        )

        val classicMovies = approximateCount(
            totalMinutes = totalMinutes,
            unitMinutes = CLASSIC_MOVIE_MINUTES,
        )

        val movieEvenings = approximateCount(
            totalMinutes = totalMinutes,
            unitMinutes = MOVIE_EVENING_MINUTES,
        )

        val miniSeries = approximateCount(
            totalMinutes = totalMinutes,
            unitMinutes = MINI_SERIES_MINUTES,
        )

        val workdays = approximateCount(
            totalMinutes = totalMinutes,
            unitMinutes = WORKDAY_MINUTES,
        )

        val sleepNights = approximateCount(
            totalMinutes = totalMinutes,
            unitMinutes = SLEEP_NIGHT_MINUTES,
        )

        val audiobooks = approximateCount(
            totalMinutes = totalMinutes,
            unitMinutes = AUDIOBOOK_MINUTES,
        )

        val weekendMarathons = approximateCount(
            totalMinutes = totalMinutes,
            unitMinutes = WEEKEND_MARATHON_MINUTES,
        )

        val candidates = buildList {
            add(
                TimeFunFact(
                    headline = "To około $coffeeBreaks przerw kawowych",
                    description = "Licząc po 15 minut na kawę. Popcorn nadal wygrywa.",
                ),
            )

            add(
                TimeFunFact(
                    headline = "To około $seriesEpisodes odcinków serialu",
                    description = "Przy założeniu, że jeden odcinek trwa około 45 minut.",
                ),
            )

            add(
                TimeFunFact(
                    headline = "To około $podcasts godzinnych podcastów",
                    description = "Tylko tutaj przez większość czasu mówią aktorzy.",
                ),
            )

            add(
                TimeFunFact(
                    headline = "To około $footballMatches meczów piłkarskich",
                    description = "Licząc po 90 minut i bez doliczonego czasu gry.",
                ),
            )

            add(
                TimeFunFact(
                    headline = "To około $classicMovies klasycznych filmów",
                    description = "Przyjmując po 90 minut na jeden film.",
                ),
            )

            add(
                TimeFunFact(
                    headline = "To około $movieEvenings filmowych wieczorów",
                    description = "Licząc po dwie godziny na jeden wspólny seans.",
                ),
            )

            if (totalMinutes >= MINI_SERIES_MINUTES) {
                add(
                    TimeFunFact(
                        headline = "To około $miniSeries krótkich sezonów serialu",
                        description = "Każdy po osiem odcinków trwających około 45 minut.",
                    ),
                )
            }

            if (totalMinutes >= WORKDAY_MINUTES) {
                add(
                    TimeFunFact(
                        headline = "To około $workdays ośmiogodzinnych dni pracy",
                        description = "Zdecydowanie przyjemniejszych i z większą ilością popcornu.",
                    ),
                )

                add(
                    TimeFunFact(
                        headline = "To około $sleepNights pełnych nocy snu",
                        description = "Choć maraton filmowy prawdopodobnie nie pozwoliłby zasnąć.",
                    ),
                )
            }

            if (totalMinutes >= AUDIOBOOK_MINUTES) {
                add(
                    TimeFunFact(
                        headline = "To około $audiobooks dziesięciogodzinnych audiobooków",
                        description = "W filmach przynajmniej nie trzeba wyobrażać sobie efektów specjalnych.",
                    ),
                )
            }

            if (totalMinutes >= WEEKEND_MARATHON_MINUTES) {
                add(
                    TimeFunFact(
                        headline = "To około $weekendMarathons weekendowych maratonów",
                        description = "Licząc po 16 godzin oglądania na każdy maraton.",
                    ),
                )
            }

            if (totalMinutes >= FULL_DAY_MINUTES) {
                val days = formatDecimal(
                    value = totalMinutes.toDouble() / FULL_DAY_MINUTES,
                    locale = polishLocale,
                )

                add(
                    TimeFunFact(
                        headline = "To $days dnia oglądania bez przerwy",
                        description = "Bez snu, pauzy i zastanawiania się, co obejrzeć dalej.",
                    ),
                )
            }

            if (totalMinutes >= MOON_TRIP_MINUTES) {
                val moonTrips = formatDecimal(
                    value = totalMinutes.toDouble() / MOON_TRIP_MINUTES,
                    locale = polishLocale,
                )

                add(
                    TimeFunFact(
                        headline = "To około $moonTrips podróży na Księżyc",
                        description = "Licząc około trzech dni lotu w jedną stronę.",
                    ),
                )
            }

            if (totalMinutes >= FULL_WEEK_MINUTES) {
                val weeks = formatDecimal(
                    value = totalMinutes.toDouble() / FULL_WEEK_MINUTES,
                    locale = polishLocale,
                )

                add(
                    TimeFunFact(
                        headline = "To $weeks tygodnia oglądania bez przerwy",
                        description = "Tydzień, w którym każdy dzień byłby filmowym wieczorem.",
                    ),
                )
            }

            if (totalMinutes >= FULL_MONTH_MINUTES) {
                val months = formatDecimal(
                    value = totalMinutes.toDouble() / FULL_MONTH_MINUTES,
                    locale = polishLocale,
                )

                add(
                    TimeFunFact(
                        headline = "To $months miesiąca nieprzerwanego oglądania",
                        description = "Cały kalendarz wypełniony napisami początkowymi i końcowymi.",
                    ),
                )
            }

            if (totalMinutes >= FULL_YEAR_MINUTES) {
                val years = formatDecimal(
                    value = totalMinutes.toDouble() / FULL_YEAR_MINUTES,
                    locale = polishLocale,
                )

                add(
                    TimeFunFact(
                        headline = "To $years roku oglądania bez przerwy",
                        description = "Na tym etapie kanapa oficjalnie staje się członkiem rodziny.",
                    ),
                )
            }
        }

        /*
         * Wybór jest deterministyczny:
         * ten sam łączny czas zawsze daje ten sam tekst,
         * więc fakt nie skacze podczas rekompozycji.
         */
        val selectedIndex = (
                totalMinutes / FUN_FACT_CHANGE_INTERVAL_MINUTES
                ).mod(candidates.size.toLong()).toInt()

        return candidates[selectedIndex]
    }

    private fun approximateCount(
        totalMinutes: Long,
        unitMinutes: Double,
    ): Int {
        return max(
            (totalMinutes.toDouble() / unitMinutes).roundToInt(),
            1,
        )
    }

    private fun formatDecimal(
        value: Double,
        locale: Locale,
    ): String {
        return String.format(
            locale,
            "%.1f",
            value,
        )
    }

    private companion object {
        const val TOP_MOVIES_LIMIT = 10
        const val LOWEST_MOVIES_LIMIT = 5
        const val DISAGREEMENTS_LIMIT = 5
        const val MONTHS_VISIBLE = 12

        const val COFFEE_BREAK_MINUTES = 15.0
        const val SERIES_EPISODE_MINUTES = 45.0
        const val PODCAST_EPISODE_MINUTES = 60.0
        const val FOOTBALL_MATCH_MINUTES = 90.0
        const val CLASSIC_MOVIE_MINUTES = 90.0
        const val MOVIE_EVENING_MINUTES = 120.0

        const val MINI_SERIES_MINUTES = 8.0 * 45.0

        const val WORKDAY_MINUTES = 8.0 * 60.0
        const val SLEEP_NIGHT_MINUTES = 8.0 * 60.0
        const val AUDIOBOOK_MINUTES = 10.0 * 60.0

        const val WEEKEND_MARATHON_MINUTES = 16.0 * 60.0

        const val FULL_DAY_MINUTES = 24.0 * 60.0
        const val MOON_TRIP_MINUTES = 3.0 * FULL_DAY_MINUTES
        const val FULL_WEEK_MINUTES = 7.0 * FULL_DAY_MINUTES
        const val FULL_MONTH_MINUTES = 30.0 * FULL_DAY_MINUTES
        const val FULL_YEAR_MINUTES = 365.0 * FULL_DAY_MINUTES
        const val FUN_FACT_CHANGE_INTERVAL_MINUTES = 30L
    }
}