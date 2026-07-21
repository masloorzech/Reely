package com.example.reely.domain.usecase.movie

import com.example.reely.domain.model.MovieRating
import com.example.reely.domain.repository.MovieRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveMovieRatingsUseCase @Inject constructor(
    private val repository: MovieRepository,
) {
    operator fun invoke(
        movieId: Int,
    ): Flow<List<MovieRating>> {
        return repository.observeRatingsForMovie(movieId)
    }
}