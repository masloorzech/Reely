package com.example.reely.domain.usecase.movie

import com.example.reely.domain.model.Rating
import com.example.reely.domain.repository.MovieRepository
import jakarta.inject.Inject


class UpdateMovieRatingsUseCase @Inject constructor(
    private val repository: MovieRepository,
) {

    suspend operator fun invoke(
        movieId: Int,
        firstProfileRating: Rating,
        secondProfileRating: Rating,
    ): Result<Unit> {
        require(movieId > 0) {
            "Movie ID must be positive"
        }

        return repository.updateRatings(
            movieId = movieId,
            firstProfileRating = firstProfileRating,
            secondProfileRating = secondProfileRating,
        )
    }
}