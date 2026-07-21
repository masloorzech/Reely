package com.example.reely.domain.usecase.movie

import com.example.reely.domain.repository.MovieRepository
import javax.inject.Inject

class RefreshWatchProviderCatalogUseCase @Inject constructor(
    private val repository: MovieRepository,
) {

    suspend operator fun invoke(
        forceRefresh: Boolean = false,
    ): Result<Unit> {
        return repository.refreshWatchProviderCatalog(
            forceRefresh = forceRefresh,
        )
    }
}