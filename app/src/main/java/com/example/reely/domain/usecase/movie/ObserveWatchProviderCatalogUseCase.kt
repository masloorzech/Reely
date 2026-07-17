package com.example.reely.domain.usecase.movie

import com.example.reely.domain.model.WatchProvider
import com.example.reely.domain.repository.MovieRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class ObserveWatchProviderCatalogUseCase @Inject constructor(
    private val repository: MovieRepository,
) {

    operator fun invoke(): Flow<List<WatchProvider>> {
        return repository.observeWatchProviderCatalog()
    }
}