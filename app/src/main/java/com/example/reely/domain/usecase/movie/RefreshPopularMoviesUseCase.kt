package com.example.reely.domain.usecase.movie

import com.example.reely.domain.repository.MovieRepository
import javax.inject.Inject

class RefreshPopularMoviesUseCase @Inject constructor(
    private val repository: MovieRepository,
) {

    suspend operator fun invoke(
        forceRefresh: Boolean = false,
    ): Result<Unit> {
        return repository.refreshPopularMovies(
            forceRefresh = forceRefresh,
        )
    }
}