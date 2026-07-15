package com.example.reely.domain.usecase.movie

import com.example.reely.domain.model.RecentWatchedMovie
import com.example.reely.domain.repository.MovieRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveWatchedMoviesUseCase @Inject constructor(
    private val repository: MovieRepository
) {

    operator fun invoke(): Flow<List<RecentWatchedMovie>> {
        return repository.observeWatchedMovies()
    }

}