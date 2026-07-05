package com.example.reely.domain.repository

import com.example.reely.domain.model.Movie

interface MoviesRepository {

    suspend fun search(
        query : String
    ) : Result<List<Movie>>

    suspend fun getPopularMovies(): Result<List<Movie>>
    suspend fun getMovieDetails(id: Int): Result<Movie>

}