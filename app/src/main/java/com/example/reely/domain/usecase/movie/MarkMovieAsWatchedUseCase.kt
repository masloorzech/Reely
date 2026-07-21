package com.example.reely.domain.usecase.movie

import com.example.reely.domain.model.MovieDetails
import com.example.reely.domain.model.Rating
import com.example.reely.domain.repository.MovieRepository
import jakarta.inject.Inject
import java.time.LocalDate


class MarkMovieAsWatchedUseCase @Inject constructor(
    private val repository: MovieRepository,
) {

    suspend operator fun invoke(
        movie: MovieDetails,
        watchedAt: LocalDate,
        firstProfileRating: Rating,
        secondProfileRating: Rating,
    ): Result<Long> {
        return repository.markAsWatched(
            movie = movie,
            watchedAt = watchedAt,
            firstProfileRating = firstProfileRating,
            secondProfileRating = secondProfileRating,
        )
    }
}