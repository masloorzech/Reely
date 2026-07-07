package com.example.reely.data.local

import com.example.reely.data.local.model.LocalMovie

interface MoviesLocalDataSource {
    suspend fun saveMovieData(movie: LocalMovie)

    suspend fun getMovieData(movieId: Int): LocalMovie?
}