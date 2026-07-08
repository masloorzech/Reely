package com.example.reely.domain.usecase

import com.example.reely.domain.model.Movie
import com.example.reely.domain.repository.MoviesRepository
import jakarta.inject.Inject

class GetUserMoviesUseCase @Inject constructor(
    private val repository: MoviesRepository
) {
    suspend operator fun invoke(): Result<List<Movie>> {
        return repository.getUserMovies()
    }
}