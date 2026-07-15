package com.example.reely.data.remote

import com.example.reely.domain.model.MovieCredits
import com.example.reely.domain.model.MovieDetails
import com.example.reely.domain.model.MoviePreview
import com.example.reely.domain.model.MovieWatchProviders

interface MoviesRemoteDataSource {

    suspend fun getPopularMovies(): List<MoviePreview>

    suspend fun search(
        query: String,
    ): List<MoviePreview>

    suspend fun getMovieDetails(
        id: Int,
    ): MovieDetails

    suspend fun getMovieCredits(
        movieId: Int,
    ): MovieCredits

    suspend fun getMovieProviders(
        movieId: Int,
    ): MovieWatchProviders
}