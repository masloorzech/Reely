package com.example.reely.data.remote.tmdb

import com.example.reely.data.remote.tmdb.dto.TmdbMovieCreditsDto
import com.example.reely.data.remote.tmdb.dto.TmdbMovieDetailsDto
import com.example.reely.data.remote.tmdb.dto.TmdbMovieWatchProvidersDto
import com.example.reely.data.remote.tmdb.dto.TmdbMoviesResponseDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TmdbApi {

    @GET("trending/movie/{time_window}")
    suspend fun getPopularMovies(
        @Path("time_window") timeWindow: String = "week",
        @Query("language") language: String = "pl-PL"
    ): TmdbMoviesResponseDto

    @GET("search/movie")
    suspend fun searchMovies(
        @Query("query") query: String,
        @Query("language") language: String = "pl-PL",
        @Query("page") page: Int = 1,
        @Query("include_adult") includeAdult: Boolean = false
    ): TmdbMoviesResponseDto

    @GET("movie/{movie_id}")
    suspend fun getMovieDetails(
        @Path("movie_id") movieId: Int,
        @Query("language") language: String = "pl-PL"
    ): TmdbMovieDetailsDto

    @GET("movie/{movie_id}/credits")
    suspend fun getMovieCredits(
        @Path("movie_id") movieId: Int,
        @Query("language") language: String = "pl-PL"
    ): TmdbMovieCreditsDto

    @GET("movie/{movie_id}/watch/providers")
    suspend fun getMovieWatchProviders(
        @Path("movie_id") movieId: Int
    ): TmdbMovieWatchProvidersDto

}