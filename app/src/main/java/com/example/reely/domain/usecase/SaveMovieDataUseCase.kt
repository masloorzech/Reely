package com.example.reely.domain.usecase

import com.example.reely.domain.model.Movie
import com.example.reely.domain.repository.MoviesRepository
import javax.inject.Inject

class SaveMovieDataUseCase @Inject constructor(
    private val moviesRepository: MoviesRepository
) {
    suspend operator fun invoke(movie: Movie): Result<Unit> {
        return moviesRepository.saveMovieData(movie)
    }
}