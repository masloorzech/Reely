package com.example.reely.domain.repository

import com.example.reely.domain.model.Movie
import com.example.reely.domain.model.MovieCredits
import com.example.reely.domain.model.MovieWatchProviders
import com.example.reely.domain.model.UserId
import com.example.reely.domain.model.UserMovieWatch

interface MoviesRepository {

    suspend fun search(
        query : String
    ) : Result<List<Movie>>

    suspend fun getPopularMovies(): Result<List<Movie>>
    suspend fun getMovieDetails(id: Int): Result<Movie>

    suspend fun saveMovieData(movie: Movie): Result<Unit>

    suspend fun getMovieCast(movieId: Int): Result<MovieCredits>

    suspend fun getMovieWatchProviders(movieId: Int): Result<MovieWatchProviders>

    suspend fun addMovieToWatchlist(movie: Movie): Result<Unit>

    suspend fun getUserMovies(): Result<List<Movie>>

    suspend fun removeMovieFromWatchlist(movieId: Int): Result<Unit>

    suspend fun markMovieAsWatched(
        movie: Movie,
        userWatches: Map<UserId, UserMovieWatch>
    ): Result<Unit>

    suspend fun updateMovieWatches(
        movieId: Int,
        userWatches: Map<UserId, UserMovieWatch>
    ): Result<Unit>

    suspend fun removeMovieFromWatched(movieId: Int): Result<Unit>

}
