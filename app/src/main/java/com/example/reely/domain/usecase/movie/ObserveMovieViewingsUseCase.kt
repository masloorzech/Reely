package com.example.reely.domain.usecase.movie

import com.example.reely.domain.model.MovieViewing
import com.example.reely.domain.repository.MovieRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveMovieViewingsUseCase @Inject constructor(
    private val repository: MovieRepository,
) {
    operator fun invoke(
        movieId: Int,
    ): Flow<List<MovieViewing>> {
        return repository.observeViewingsForMovie(movieId)
    }
}