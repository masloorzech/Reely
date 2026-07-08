package com.example.reely.data.remote

import com.example.reely.data.remote.model.RemoteCastMember
import com.example.reely.data.remote.model.RemoteMovie
import com.example.reely.data.remote.model.RemoteMovieCredits
import com.example.reely.data.remote.model.RemoteMovieWatchProviders

interface MoviesRemoteDataSource {
    suspend fun getPopularMovies(): List<RemoteMovie>
    suspend fun search(query: String): List<RemoteMovie>
    suspend fun getMovieDetails(id: Int): RemoteMovie

    suspend fun getMovieCastMembers(movieId : Int): RemoteMovieCredits

    suspend fun getMovieProviders(movieId: Int): RemoteMovieWatchProviders
}