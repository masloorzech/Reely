package com.example.reely.domain.usecase.movie

import com.example.reely.domain.model.MovieCredits
import com.example.reely.domain.repository.MovieRepository
import javax.inject.Inject

class GetMovieCreditsUseCase @Inject constructor(
    private val repository: MovieRepository
) {

    suspend operator fun invoke(movieId: Int) : Result<MovieCredits>{

        return repository.getMovieCredits(movieId)

    }

}