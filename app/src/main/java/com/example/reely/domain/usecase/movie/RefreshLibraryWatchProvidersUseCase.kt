package com.example.reely.domain.usecase.movie

import com.example.reely.domain.model.ProviderRefreshResult
import com.example.reely.domain.repository.MovieRepository
import javax.inject.Inject

class RefreshLibraryWatchProvidersUseCase @Inject constructor(
    private val repository: MovieRepository,
) {

    suspend operator fun invoke(
        forceRefresh: Boolean = true,
    ): Result<ProviderRefreshResult> {
        return repository.refreshLibraryProviders(
            forceRefresh = forceRefresh,
        )
    }
}