package com.example.reely.domain.usecase.movie

import com.example.reely.domain.model.MoviePreview
import com.example.reely.domain.repository.MovieRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObservePopularMoviesUseCase @Inject constructor(
    private val repository: MovieRepository,
) {

    operator fun invoke(): Flow<List<MoviePreview>> {
        return repository.observePopularMovies()
    }
}