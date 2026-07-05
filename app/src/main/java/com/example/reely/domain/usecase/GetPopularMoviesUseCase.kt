package com.example.reely.domain.usecase

import com.example.reely.domain.model.Movie
import com.example.reely.domain.repository.MoviesRepository
import javax.inject.Inject

class GetPopularMoviesUseCase @Inject constructor(
    val repository: MoviesRepository
) {

    suspend operator fun invoke(): Result<List<Movie>> {
        return repository.getPopularMovies()
    }

}