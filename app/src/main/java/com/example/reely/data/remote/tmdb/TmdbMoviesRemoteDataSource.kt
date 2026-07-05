package com.example.reely.data.remote.tmdb

import com.example.reely.data.remote.MoviesRemoteDataSource
import com.example.reely.data.remote.model.RemoteMovie
import com.example.reely.data.remote.tmdb.mapper.toRemoteMovie
import javax.inject.Inject

class TmdbMoviesRemoteDataSource @Inject constructor(
    private val api: TmdbApi
) : MoviesRemoteDataSource {

    override suspend fun getPopularMovies(): List<RemoteMovie> {
        return api.getPopularMovies()
            .results
            .map { it.toRemoteMovie() }
    }

    override suspend fun search(query: String): List<RemoteMovie> {
        return api.searchMovies(query)
            .results
            .map { it.toRemoteMovie() }
    }

    override suspend fun getMovieDetails(id: Int): RemoteMovie {
        return api.getMovieDetails(id)
            .toRemoteMovie()
    }
}