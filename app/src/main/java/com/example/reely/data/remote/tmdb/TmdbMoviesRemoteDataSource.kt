package com.example.reely.data.remote.tmdb

import com.example.reely.data.remote.MoviesRemoteDataSource
import com.example.reely.data.remote.tmdb.mapper.toMovieCredits
import com.example.reely.data.remote.tmdb.mapper.toMovieDetails
import com.example.reely.data.remote.tmdb.mapper.toMoviePreview
import com.example.reely.data.remote.tmdb.mapper.toMovieWatchProviders
import com.example.reely.domain.model.MovieCredits
import com.example.reely.domain.model.MovieDetails
import com.example.reely.domain.model.MoviePreview
import com.example.reely.domain.model.MovieWatchProviders
import javax.inject.Inject

class TmdbMoviesRemoteDataSource @Inject constructor(
    private val api: TmdbApi,
) : MoviesRemoteDataSource {

    override suspend fun getPopularMovies(): List<MoviePreview> {
        return api.getPopularMovies()
            .results
            .map { dto ->
                dto.toMoviePreview()
            }
    }

    override suspend fun search(
        query: String,
    ): List<MoviePreview> {
        return api.searchMovies(query)
            .results
            .map { dto ->
                dto.toMoviePreview()
            }
    }

    override suspend fun getMovieDetails(
        id: Int,
    ): MovieDetails {
        return api.getMovieDetails(id)
            .toMovieDetails()
    }

    override suspend fun getMovieCredits(
        movieId: Int,
    ): MovieCredits {
        return api.getMovieCredits(movieId)
            .toMovieCredits()
    }

    override suspend fun getMovieProviders(
        movieId: Int,
    ): MovieWatchProviders {
        return api.getMovieWatchProviders(movieId)
            .toMovieWatchProviders(
                countryCode = POLAND_COUNTRY_CODE,
            )
    }

    private companion object {
        const val POLAND_COUNTRY_CODE = "PL"
    }
}