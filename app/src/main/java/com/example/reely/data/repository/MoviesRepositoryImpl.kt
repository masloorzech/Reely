package com.example.reely.data.repository

import com.example.reely.data.local.MoviesLocalDataSource
import com.example.reely.data.local.mapper.toDomain
import com.example.reely.data.local.mapper.toLocal
import com.example.reely.data.local.mapper.toLocalOrNull
import com.example.reely.data.local.model.LocalMovieUserState
import com.example.reely.data.remote.MoviesRemoteDataSource
import com.example.reely.data.remote.mapper.toDomain
import com.example.reely.data.remote.model.toDomain
import com.example.reely.domain.model.Movie
import com.example.reely.domain.model.MovieCredits
import com.example.reely.domain.model.MovieUserState
import com.example.reely.domain.model.MovieWatchProviders
import com.example.reely.domain.model.SHARED_WATCHLIST_USER_ID
import com.example.reely.domain.model.UserId
import com.example.reely.domain.repository.MoviesRepository
import javax.inject.Inject

class MoviesRepositoryImpl @Inject constructor(
    private val remoteDataSource: MoviesRemoteDataSource,
    private val localDataSource: MoviesLocalDataSource,
) : MoviesRepository{


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

            val localMovie = localDataSource.getMovieData(id)

            remoteMovie.copy(
                userStates = localMovie
                    ?.userStates
                    ?.mapKeys { entry -> UserId(entry.key) }
                    ?.mapValues { entry -> entry.value.toDomain() }
                    .orEmpty()
            )
        }
    }

    override suspend fun saveMovieData(movie: Movie): Result<Unit> {
        return runCatching {
            val localMovie = localDataSource.getMovieData(movie.id)

            if (localMovie == null) {
                localDataSource.saveMovieData(movie.toLocal())
            }
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
            val localState = MovieUserState.InWatchlist.toLocalOrNull()
                ?: return@runCatching

            localDataSource.changeMovieUserState(
                movieId = movie.id,
                userId = SHARED_WATCHLIST_USER_ID.value,
                movieUserState = localState
            )
        }
    }

    override suspend fun getUserMovies(): Result<List<Movie>> {
        return runCatching {
            localDataSource.getUserMovies()
                .map { it.toDomain() }
        }
    }


}