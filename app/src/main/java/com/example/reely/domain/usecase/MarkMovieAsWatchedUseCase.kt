package com.example.reely.domain.usecase

import com.example.reely.domain.model.Movie
import com.example.reely.domain.model.UserId
import com.example.reely.domain.model.UserMovieWatch
import com.example.reely.domain.repository.MoviesRepository
import jakarta.inject.Inject


class MarkMovieAsWatchedUseCase @Inject constructor(
    private val repository: MoviesRepository
) {
    suspend operator fun invoke(
        movie: Movie,
        userWatches: Map<UserId, UserMovieWatch>
    ): Result<Unit> {
        return repository.markMovieAsWatched(
            movie = movie,
            userWatches = userWatches
        )
    }
}