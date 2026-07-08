package com.example.reely.data.local

import com.example.reely.data.local.model.LocalMovie
import com.example.reely.data.local.model.LocalMovieUserState

interface MoviesLocalDataSource {
    suspend fun saveMovieData(movie: LocalMovie)

    suspend fun getMovieData(movieId: Int): LocalMovie?

    suspend fun getUserMovies(): List<LocalMovie>

    suspend fun changeMovieUserState(movieId: Int, userId: Int, movieUserState : LocalMovieUserState)
}