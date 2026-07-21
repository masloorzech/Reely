package com.example.reely.domain.usecase.movie

import com.example.reely.domain.model.CachedMovieWatchProviders
import com.example.reely.domain.repository.MovieRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveCachedMovieProvidersUseCase @Inject constructor(
    private val repository: MovieRepository,
) {

    operator fun invoke():
            Flow<List<CachedMovieWatchProviders>> {
        return repository.observeCachedMovieProviders()
    }
}