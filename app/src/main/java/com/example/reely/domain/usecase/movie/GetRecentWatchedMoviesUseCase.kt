package com.example.reely.domain.usecase.movie

import com.example.reely.domain.model.RecentWatchedMovie
import com.example.reely.domain.repository.MovieRepository
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow

class ObserveRecentWatchedMoviesUseCase @Inject constructor(
    private val repository: MovieRepository,
) {
    operator fun invoke(
        limit: Int = 10,
    ): Flow<List<RecentWatchedMovie>> {
        require(limit > 0) {
            "Limit must be greater than zero"
        }

        return repository.observeRecentWatchedMovies(limit)
    }
}