package com.example.reely.data.repository

import com.example.reely.data.local.MovieLocalDataSource
import com.example.reely.data.local.PopularMovieCacheLocalDataSource
import com.example.reely.data.local.RecentSearchesLocalDataSource
import com.example.reely.data.local.room.mapper.toCacheEntity
import com.example.reely.data.local.room.mapper.toDomain
import com.example.reely.data.remote.MoviesRemoteDataSource
import com.example.reely.domain.model.MovieCredits
import com.example.reely.domain.model.MovieDetails
import com.example.reely.domain.model.MoviePreview
import com.example.reely.domain.model.MovieRating
import com.example.reely.domain.model.MovieViewing
import com.example.reely.domain.model.MovieWatchProviders
import com.example.reely.domain.model.Rating
import com.example.reely.domain.model.RecentWatchedMovie
import com.example.reely.domain.model.WatchlistEntry
import com.example.reely.domain.repository.MovieRepository
import java.time.Clock
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Singleton
class MovieRepositoryImpl @Inject constructor(
    private val localDataSource: MovieLocalDataSource,
    private val recentSearchesLocalDataSource: RecentSearchesLocalDataSource,
    private val popularCacheDataSource: PopularMovieCacheLocalDataSource,
    private val remoteDataSource: MoviesRemoteDataSource,
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
                IllegalArgumentException("Movie ID must be positive")
            )
        }

        return runCatching {
            remoteDataSource.getMovieProviders(movieId)
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
            localDataSource.addToWatchlist(movie)
        }
    }

    override suspend fun markAsWatched(
        movie: MovieDetails,
        watchedAt: LocalDate,
        firstProfileRating: Rating,
        secondProfileRating: Rating,
    ): Result<Long> {
        return runCatching {
            localDataSource.markAsWatched(
                movie = movie,
                watchedAt = watchedAt,
                firstProfileRating = firstProfileRating,
                secondProfileRating = secondProfileRating,
            )
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

    private companion object {
        const val POPULAR_CACHE_TTL_MILLIS =
            6L * 60L * 60L * 1_000L
    }
}