package com.example.reely.data.local

import com.example.reely.data.local.model.LocalMovie
import com.example.reely.data.local.model.LocalMovieUserWatch
import com.example.reely.data.local.model.LocalMovieWithUserData

interface MoviesLocalDataSource {

    suspend fun saveMovieData(movie: LocalMovie)

    suspend fun getMovieData(movieId: Int): LocalMovie?

    suspend fun getMovieWithUserData(movieId: Int): LocalMovieWithUserData?

    suspend fun getUserMovies(): List<LocalMovieWithUserData>

    suspend fun addToWatchlist(
        movieId: Int,
        addedAt: String? = null,
        addedByUserId: Int? = null
    )

    suspend fun removeFromWatchlist(movieId: Int)

    suspend fun upsertUserWatch(
        movieId: Int,
        userId: Int,
        userWatch: LocalMovieUserWatch
    )

    suspend fun deleteUserWatch(
        movieId: Int,
        userId: Int
    )
}