package com.example.reely.data.repository

import com.example.reely.data.local.MoviesLocalDataSource
import com.example.reely.data.local.mapper.toDomain
import com.example.reely.data.local.mapper.toLocal
import com.example.reely.data.remote.MoviesRemoteDataSource
import com.example.reely.data.remote.mapper.toDomain
import com.example.reely.data.remote.model.toDomain
import com.example.reely.domain.model.Movie
import com.example.reely.domain.model.MovieCredits
import com.example.reely.domain.model.MovieWatchProviders
import com.example.reely.domain.model.UserId
import com.example.reely.domain.model.UserMovieWatch
import com.example.reely.domain.repository.MoviesRepository
import javax.inject.Inject

class MoviesRepositoryImpl @Inject constructor(
    private val remoteDataSource: MoviesRemoteDataSource,
    private val localDataSource: MoviesLocalDataSource,
) : MoviesRepository {

    override suspend fun getPopularMovies(): Result<List<Movie>> {
        return runCatching {
            remoteDataSource.getPopularMovies()
                .map { it.toDomain() }
        }
    }

    override suspend fun search(query: String): Result<List<Movie>> {
        return runCatching {
            remoteDataSource.search(query)
                .map { it.toDomain() }
        }
    }

    override suspend fun getMovieDetails(id: Int): Result<Movie> {
        return runCatching {
            val remoteMovie = remoteDataSource.getMovieDetails(id)
                .toDomain()

            val localMovieWithUserData = localDataSource.getMovieWithUserData(id)

            if (localMovieWithUserData == null) {
                remoteMovie
            } else {
                remoteMovie.copy(
                    isInWatchlist = localMovieWithUserData.isInWatchlist,
                    userWatches = localMovieWithUserData.userWatches
                        .mapKeys { entry ->
                            com.example.reely.domain.model.UserId(entry.key)
                        }
                        .mapValues { entry ->
                            entry.value.toDomain()
                        }
                )
            }
        }
    }

    override suspend fun saveMovieData(movie: Movie): Result<Unit> {
        return runCatching {
            localDataSource.saveMovieData(movie.toLocal())
        }
    }

    override suspend fun getMovieCast(movieId: Int): Result<MovieCredits> {
        return runCatching {
            remoteDataSource.getMovieCastMembers(movieId).toDomain()
        }
    }

    override suspend fun getMovieWatchProviders(movieId: Int): Result<MovieWatchProviders> {
        return runCatching {
            remoteDataSource.getMovieProviders(movieId).toDomain()
        }
    }

    override suspend fun addMovieToWatchlist(movie: Movie): Result<Unit> {
        return runCatching {
            localDataSource.saveMovieData(movie.toLocal())
            localDataSource.addToWatchlist(movieId = movie.id)
        }
    }

    override suspend fun removeMovieFromWatchlist(movieId: Int): Result<Unit> {
        return runCatching {
            localDataSource.removeFromWatchlist(movieId)
        }
    }

    override suspend fun getUserMovies(): Result<List<Movie>> {
        return runCatching {
            localDataSource.getUserMovies()
                .map { it.toDomain() }
        }
    }

    override suspend fun markMovieAsWatched(
        movie: Movie,
        userWatches: Map<UserId, UserMovieWatch>
    ): Result<Unit> {
        return runCatching {
            localDataSource.saveMovieData(movie.toLocal())

            userWatches.forEach { entry ->
                localDataSource.upsertUserWatch(
                    movieId = movie.id,
                    userId = entry.key.value,
                    userWatch = entry.value.toLocal()
                )
            }

            localDataSource.removeFromWatchlist(movie.id)
        }
    }

    override suspend fun updateMovieWatches(
        movieId: Int,
        userWatches: Map<UserId, UserMovieWatch>
    ): Result<Unit> {
        return runCatching {
            userWatches.forEach { entry ->
                localDataSource.upsertUserWatch(
                    movieId = movieId,
                    userId = entry.key.value,
                    userWatch = entry.value.toLocal()
                )
            }
        }
    }

    override suspend fun removeMovieFromWatched(movieId: Int): Result<Unit> {
        return runCatching {
            localDataSource.deleteUserWatchesForMovie(movieId)
        }
    }

}