package com.example.reely.domain.usecase.movie

import com.example.reely.domain.model.MovieRating
import com.example.reely.domain.repository.MovieRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class ObserveMovieRatingsUseCase @Inject constructor(
    private val repository: MovieRepository,
) {
    operator fun invoke(
        movieId: Int,
    ): Flow<List<MovieRating>> {
        return repository.observeRatingsForMovie(movieId)
    }
}