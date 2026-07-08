package com.example.reely.domain.usecase

import com.example.reely.domain.model.MovieCredits
import com.example.reely.domain.repository.MoviesRepository
import javax.inject.Inject

class GetMovieCreditsUseCase @Inject constructor(
    private val repository: MoviesRepository
) {

    suspend operator fun invoke(movieId: Int) : Result<MovieCredits>{

        return repository.getMovieCast(movieId)

    }

}