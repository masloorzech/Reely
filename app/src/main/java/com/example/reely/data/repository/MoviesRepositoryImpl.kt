package com.example.reely.data.repository

import com.example.reely.data.local.MovieLocalDataSource
import com.example.reely.data.local.PopularMovieCacheLocalDataSource
import com.example.reely.data.local.RecentSearchesLocalDataSource
import com.example.reely.data.local.WatchProviderLocalDataSource
import com.example.reely.data.local.room.entity.WatchProviderEntity
import com.example.reely.data.local.room.mapper.toCacheEntity
import com.example.reely.data.local.room.mapper.toDomain
import com.example.reely.data.remote.MoviesRemoteDataSource
import com.example.reely.domain.model.CachedMovieWatchProviders
import com.example.reely.domain.model.MovieCredits
import com.example.reely.domain.model.MovieDetails
import com.example.reely.domain.model.MoviePreview
import com.example.reely.domain.model.MovieProviderOffer
import com.example.reely.domain.model.MovieRating
import com.example.reely.domain.model.MovieViewing
import com.example.reely.domain.model.MovieWatchProviders
import com.example.reely.domain.model.ProviderRefreshResult
import com.example.reely.domain.model.Rating
import com.example.reely.domain.model.RecentWatchedMovie
import com.example.reely.domain.model.WatchMonetizationType
import com.example.reely.domain.model.WatchProvider
import com.example.reely.domain.model.WatchlistEntry
import com.example.reely.domain.repository.MovieRepository
import java.time.Clock
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import java.time.Instant

@Singleton
class MovieRepositoryImpl @Inject constructor(
    private val localDataSource: MovieLocalDataSource,
    private val recentSearchesLocalDataSource: RecentSearchesLocalDataSource,
    private val popularCacheDataSource: PopularMovieCacheLocalDataSource,
    private val remoteDataSource: MoviesRemoteDataSource,
    private val watchProviderLocalDataSource: WatchProviderLocalDataSource,
    private val clock: Clock,
) : MovieRepository {

    override fun observePopularMovies(): Flow<List<MoviePreview>> {
        return popularCacheDataSource
            .observeMovies()
            .map { entities ->
                entities.map { entity ->
                    entity.toDomain()
                }
            }
    }

    override fun observeRecentSearches() : Flow<List<String>>{
        return recentSearchesLocalDataSource.observeRecentSearches()
    }


    override suspend fun refreshPopularMovies(
        forceRefresh: Boolean,
    ): Result<Unit> {
        return runCatching {
            val now = clock.millis()
            val lastFetchedAt =
                popularCacheDataSource.getLatestFetchedAt()

            val cacheIsFresh =
                lastFetchedAt != null &&
                        now - lastFetchedAt < POPULAR_CACHE_TTL_MILLIS

            if (!forceRefresh && cacheIsFresh) {
                return@runCatching
            }

            val movies = remoteDataSource.getPopularMovies()

            if (movies.isEmpty()) {
                return@runCatching
            }

            val cacheEntries = movies.mapIndexed { index, movie ->
                movie.toCacheEntity(
                    position = index,
                    fetchedAt = now,
                )
            }

            popularCacheDataSource.replaceMovies(cacheEntries)
        }
    }

    override suspend fun searchMovies(
        query: String,
    ): Result<List<MoviePreview>> {
        val normalizedQuery = query.trim()

        if (normalizedQuery.isBlank()) {
            return Result.success(emptyList())
        }

        recentSearchesLocalDataSource.add(query)

        return runCatching {
            remoteDataSource.search(normalizedQuery)
        }
    }

    override suspend fun getMovieDetails(
        movieId: Int,
    ): Result<MovieDetails> {
        if (movieId <= 0) {
            return Result.failure(
                IllegalArgumentException("Movie ID must be positive")
            )
        }

        return runCatching {
            remoteDataSource.getMovieDetails(movieId)
        }
    }

    override suspend fun getMovieCredits(
        movieId: Int,
    ): Result<MovieCredits> {
        if (movieId <= 0) {
            return Result.failure(
                IllegalArgumentException("Movie ID must be positive")
            )
        }

        return runCatching {
            remoteDataSource.getMovieCredits(movieId)
        }
    }

    override suspend fun getMovieProviders(
        movieId: Int,
    ): Result<MovieWatchProviders> {
        if (movieId <= 0) {
            return Result.failure(
                IllegalArgumentException(
                    "Movie ID must be positive"
                )
            )
        }

        return runCatching {
            val providers =
                remoteDataSource.getMovieProviders(movieId)


            if (localDataSource.getMovie(movieId) != null) {
                watchProviderLocalDataSource
                    .replaceMovieProviders(
                        providers = providers,
                        syncedAt = nowInstant(),
                    )
            }

            providers
        }
    }

    override fun observeWatchlist(): Flow<List<WatchlistEntry>> {
        return localDataSource
            .observeWatchlist()
            .map { rows ->
                rows.map { row ->
                    row.toDomain()
                }
            }
    }

    override fun observeViewings():
            Flow<List<MovieViewing>> {
        return localDataSource
            .observeViewings()
            .map { entities ->
                entities.map { entity ->
                    entity.toDomain()
                }
            }
    }

    override fun observeViewingsForMovie(
        movieId: Int,
    ): Flow<List<MovieViewing>> {
        return localDataSource
            .observeViewingsForMovie(movieId)
            .map { entities ->
                entities.map { entity ->
                    entity.toDomain()
                }
            }
    }

    override fun observeRatingsForMovie(
        movieId: Int,
    ): Flow<List<MovieRating>> {
        return localDataSource
            .observeRatingsForMovie(movieId)
            .map { entities ->
                entities.map { entity ->
                    entity.toDomain()
                }
            }
    }

    override suspend fun isOnWatchlist(
        movieId: Int,
    ): Boolean {
        return localDataSource.isOnWatchlist(movieId)
    }

    override suspend fun addToWatchlist(
        movie: MovieDetails,
    ): Result<Boolean> {
        return runCatching {
            val added =
                localDataSource.addToWatchlist(movie)

            if (added) {
                runCatching {
                    refreshMovieProvidersInternal(
                        movieId = movie.id,
                        forceRefresh = true,
                    )
                }
            }

            added
        }
    }

    override suspend fun markAsWatched(
        movie: MovieDetails,
        watchedAt: LocalDate,
        firstProfileRating: Rating,
        secondProfileRating: Rating,
    ): Result<Long> {
        return runCatching {
            val viewingId =
                localDataSource.markAsWatched(
                    movie = movie,
                    watchedAt = watchedAt,
                    firstProfileRating =
                        firstProfileRating,
                    secondProfileRating =
                        secondProfileRating,
                )

            runCatching {
                refreshMovieProvidersInternal(
                    movieId = movie.id,
                    forceRefresh = false,
                )
            }

            viewingId
        }
    }

    override suspend fun rewatchMovie(
        movie: MovieDetails,
        watchedAt: LocalDate,
        firstProfileRating: Rating,
        secondProfileRating: Rating,
    ): Result<Long> {
        return runCatching {
            localDataSource.rewatchMovie(
                movie = movie,
                watchedAt = watchedAt,
                firstProfileRating = firstProfileRating,
                secondProfileRating = secondProfileRating,
            )
        }
    }

    override suspend fun updateRatings(
        movieId: Int,
        firstProfileRating: Rating,
        secondProfileRating: Rating,
    ): Result<Unit> {
        return runCatching {
            localDataSource.updateRatings(
                movieId = movieId,
                firstProfileRating = firstProfileRating,
                secondProfileRating = secondProfileRating,
            )
        }
    }

    override suspend fun removeFromWatchlist(
        movieId: Int,
    ): Result<Boolean> {
        return runCatching {
            localDataSource.removeFromWatchlist(movieId)
        }
    }

    override suspend fun deleteViewing(
        viewingId: Long,
    ): Result<Boolean> {
        return runCatching {
            localDataSource.deleteViewing(viewingId)
        }
    }

    override fun observeRecentWatchedMovies(
        limit: Int,
    ): Flow<List<RecentWatchedMovie>> {
        return localDataSource
            .observeRecentWatchedMovies(limit)
            .map { rows ->
                rows.map { row ->
                    row.toDomain()
                }
            }
    }

    override suspend fun removeFromWatched(
        movieId: Int,
    ): Result<Boolean> {
        return runCatching {
            localDataSource.removeFromWatched(movieId)
        }
    }

    override fun observeWatchedMovies():
            Flow<List<RecentWatchedMovie>> {
        return localDataSource.observeWatchedMovies()
    }

    override fun observeWatchProviderCatalog():
            Flow<List<WatchProvider>> {
        return watchProviderLocalDataSource
            .observeProviderCatalog(
                POLAND_COUNTRY_CODE
            )
            .map { entities ->
                entities.map { entity ->
                    entity.toDomain()
                }
            }
    }

    override fun observeSelectedWatchProviderIds():
            Flow<Set<Int>> {
        return watchProviderLocalDataSource
            .observeSelectedProviderIds(
                POLAND_COUNTRY_CODE
            )
            .map { providerIds ->
                providerIds.toSet()
            }
    }

    override fun observeSelectedWatchProviderTypes():
            Flow<Set<WatchMonetizationType>> {
        return watchProviderLocalDataSource
            .observeSelectedMonetizationTypes()
            .map { types ->
                types.toSet()
            }
    }

    override fun observeCachedMovieProviders():
            Flow<List<CachedMovieWatchProviders>> {
        return combine(
            watchProviderLocalDataSource.observeMovieOffers(
                POLAND_COUNTRY_CODE
            ),
            watchProviderLocalDataSource.observeMovieSyncStates(
                POLAND_COUNTRY_CODE
            ),
        ) { offerEntities, syncStates ->

            val offersByMovieId = offerEntities
                .groupBy { entity ->
                    entity.movieId
                }
                .mapValues { (_, entities) ->
                    entities.mapTo(
                        mutableSetOf()
                    ) { entity ->
                        MovieProviderOffer(
                            providerId =
                                entity.providerId,
                            monetizationType =
                                entity.monetizationType,
                        )
                    }
                }

            syncStates.map { syncState ->
                CachedMovieWatchProviders(
                    movieId = syncState.movieId,
                    countryCode =
                        syncState.countryCode,
                    justWatchLink =
                        syncState.justWatchLink,
                    offers =
                        offersByMovieId[
                            syncState.movieId
                        ].orEmpty(),
                    syncedAt = syncState.syncedAt,
                )
            }
        }
    }

    override suspend fun refreshWatchProviderCatalog(
        forceRefresh: Boolean,
    ): Result<Unit> {
        return runCatching {
            val now = nowInstant()

            val lastFetchedAt =
                watchProviderLocalDataSource
                    .getLatestCatalogFetchedAt(
                        POLAND_COUNTRY_CODE
                    )

            val cacheIsFresh =
                lastFetchedAt != null &&
                        now.toEpochMilli() -
                        lastFetchedAt.toEpochMilli() <
                        PROVIDER_CATALOG_TTL_MILLIS

            if (!forceRefresh && cacheIsFresh) {
                return@runCatching
            }

            val providers =
                remoteDataSource.getMovieProviderCatalog(
                    POLAND_COUNTRY_CODE
                )

            if (providers.isEmpty()) {
                return@runCatching
            }

            watchProviderLocalDataSource
                .replaceProviderCatalog(
                    countryCode =
                        POLAND_COUNTRY_CODE,
                    providers = providers.map {
                            provider ->
                        WatchProviderEntity(
                            providerId =
                                provider.providerId,
                            providerName =
                                provider.providerName,
                            logoPath =
                                provider.logoPath,
                            displayPriority =
                                provider.displayPriority,
                            countryCode =
                                POLAND_COUNTRY_CODE,
                            fetchedAt = now,
                        )
                    },
                )
        }
    }

    override suspend fun refreshMovieProviders(
        movieId: Int,
        forceRefresh: Boolean,
    ): Result<Unit> {
        if (movieId <= 0) {
            return Result.failure(
                IllegalArgumentException(
                    "Movie ID must be positive"
                )
            )
        }

        if (localDataSource.getMovie(movieId) == null) {
            return Result.failure(
                IllegalStateException(
                    "Movie is not stored locally"
                )
            )
        }

        return runCatching {
            refreshMovieProvidersInternal(
                movieId = movieId,
                forceRefresh = forceRefresh,
            )
        }
    }

    override suspend fun refreshLibraryProviders(
        forceRefresh: Boolean,
    ): Result<ProviderRefreshResult> {
        return runCatching {
            refreshWatchProviderCatalog(
                forceRefresh = forceRefresh,
            ).getOrThrow()

            val movieIds =
                localDataSource.getAllMovieIds()

            val failedMovieIds =
                mutableListOf<Int>()

            var refreshedMovies = 0

            movieIds.forEach { movieId ->
                runCatching {
                    refreshMovieProvidersInternal(
                        movieId = movieId,
                        forceRefresh = forceRefresh,
                    )
                }
                    .onSuccess {
                        refreshedMovies += 1
                    }
                    .onFailure {
                        failedMovieIds += movieId
                    }
            }

            ProviderRefreshResult(
                totalMovies = movieIds.size,
                refreshedMovies = refreshedMovies,
                failedMovieIds = failedMovieIds,
            )
        }
    }

    override suspend fun updateWatchProviderPreferences(
        providerIds: Set<Int>,
        monetizationTypes:
        Set<WatchMonetizationType>,
    ): Result<Unit> {
        return runCatching {
            val safeTypes =
                monetizationTypes.ifEmpty {
                    setOf(
                        WatchMonetizationType.FLATRATE
                    )
                }

            watchProviderLocalDataSource
                .replacePreferences(
                    countryCode =
                        POLAND_COUNTRY_CODE,
                    providerIds = providerIds,
                    types = safeTypes,
                    selectedAt = nowInstant(),
                )
        }
    }

    private suspend fun refreshMovieProvidersInternal(
        movieId: Int,
        forceRefresh: Boolean,
    ) {
        val now = nowInstant()

        val lastSync =
            watchProviderLocalDataSource
                .getMovieSyncState(
                    movieId = movieId,
                    countryCode =
                        POLAND_COUNTRY_CODE,
                )

        val cacheIsFresh =
            lastSync != null &&
                    now.toEpochMilli() -
                    lastSync.syncedAt.toEpochMilli() <
                    MOVIE_PROVIDERS_TTL_MILLIS

        if (!forceRefresh && cacheIsFresh) {
            return
        }

        val providers =
            remoteDataSource.getMovieProviders(movieId)

        watchProviderLocalDataSource
            .replaceMovieProviders(
                providers = providers,
                syncedAt = now,
            )
    }

    private fun nowInstant(): Instant {
        return Instant.ofEpochMilli(
            clock.millis()
        )
    }
    private companion object {

        const val POLAND_COUNTRY_CODE = "PL"

        const val POPULAR_CACHE_TTL_MILLIS =
            6L * 60L * 60L * 1_000L

        const val PROVIDER_CATALOG_TTL_MILLIS =
            7L * 24L * 60L * 60L * 1_000L

        const val MOVIE_PROVIDERS_TTL_MILLIS =
            24L * 60L * 60L * 1_000L
    }
}