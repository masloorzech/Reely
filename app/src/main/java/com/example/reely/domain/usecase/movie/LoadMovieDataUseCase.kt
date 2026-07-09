package com.example.reely.domain.usecase.movie

import com.example.reely.domain.model.Movie
import com.example.reely.domain.repository.MoviesRepository
import javax.inject.Inject

class LoadMovieDataUseCase @Inject constructor(
    private val repository: MoviesRepository
){

    suspend operator fun invoke(movieId: Int): Result<Movie>{
        return repository.getMovieDetails(movieId)
    }

}