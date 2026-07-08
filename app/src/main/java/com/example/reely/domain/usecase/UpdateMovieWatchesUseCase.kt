package com.example.reely.domain.usecase

import com.example.reely.domain.model.UserId
import com.example.reely.domain.model.UserMovieWatch
import com.example.reely.domain.repository.MoviesRepository
import jakarta.inject.Inject

class UpdateMovieWatchesUseCase @Inject constructor(
    private val repository: MoviesRepository
) {
    suspend operator fun invoke(
        movieId: Int,
        userWatches: Map<UserId, UserMovieWatch>
    ): Result<Unit> {
        return repository.updateMovieWatches(
            movieId = movieId,
            userWatches = userWatches
        )
    }
}