package com.example.reely.data.remote

import com.example.reely.data.remote.model.RemoteMovie

interface MoviesRemoteDataSource {
    suspend fun getPopularMovies(): List<RemoteMovie>
    suspend fun search(query: String): List<RemoteMovie>
    suspend fun getMovieDetails(id: Int): RemoteMovie
}