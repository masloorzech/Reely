package com.example.reely.domain.usecase.movie

import com.example.reely.domain.repository.MoviesRepository
import jakarta.inject.Inject

class RemoveMovieFromWatchedUseCase @Inject constructor(
    private val repository: MoviesRepository
) {
    suspend operator fun invoke(movieId: Int): Result<Unit> {
        return repository.removeMovieFromWatched(movieId)
    }
}