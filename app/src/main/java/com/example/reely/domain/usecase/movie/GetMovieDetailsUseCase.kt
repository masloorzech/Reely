package com.example.reely.domain.usecase.movie

import com.example.reely.domain.model.MovieDetails
import com.example.reely.domain.repository.MovieRepository
import javax.inject.Inject

class GetMovieDetailsUseCase @Inject constructor(
    private val repository: MovieRepository
) {

    suspend operator fun invoke(movieId: Int) : Result<MovieDetails>{

        return repository.getMovieDetails(movieId)

    }

}