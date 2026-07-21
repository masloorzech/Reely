package com.example.reely.domain.usecase.movie

import com.example.reely.domain.model.MovieProviderFilter
import com.example.reely.domain.model.ProviderFilterScope
import com.example.reely.domain.model.WatchMonetizationType
import com.example.reely.domain.model.WatchlistEntry
import com.example.reely.domain.repository.MovieRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class GetRandomWatchlistMovieUseCase @Inject constructor(
    private val repository: MovieRepository,
) {

    suspend operator fun invoke(
        previousMovieId: Int? = null,
    ): Result<WatchlistEntry?> {
        return runCatching {
            val watchlist =
                repository.observeWatchlist().first()

            if (watchlist.isEmpty()) {
                return@runCatching null
            }

            val myProviderIds =
                repository
                    .observeSelectedWatchProviderIds()
                    .first()

            val selectedTypes =
                repository
                    .observeSelectedWatchProviderTypes()
                    .first()
                    .ifEmpty {
                        setOf(
                            WatchMonetizationType.FLATRATE
                        )
                    }

            val providersByMovieId =
                repository
                    .observeCachedMovieProviders()
                    .first()
                    .associateBy { cached ->
                        cached.movieId
                    }

            val providerFilter =
                MovieProviderFilter(
                    isEnabled =
                        myProviderIds.isNotEmpty(),

                    scope =
                        ProviderFilterScope.MY_PROVIDERS,

                    monetizationTypes =
                        selectedTypes,
                )

            val matchingMovies =
                watchlist.filter { entry ->
                    providerFilter.matches(
                        offers =
                            providersByMovieId[
                                entry.movie.id
                            ]
                                ?.offers
                                .orEmpty(),

                        myProviderIds =
                            myProviderIds,
                    )
                }

            val candidates =
                matchingMovies.withoutPreviousMovie(
                    previousMovieId =
                        previousMovieId,
                )

            candidates.randomOrNull()
        }
    }
}

private fun List<WatchlistEntry>.withoutPreviousMovie(
    previousMovieId: Int?,
): List<WatchlistEntry> {
    if (
        previousMovieId == null ||
        size <= 1
    ) {
        return this
    }

    return filterNot { entry ->
        entry.movie.id == previousMovieId
    }
}