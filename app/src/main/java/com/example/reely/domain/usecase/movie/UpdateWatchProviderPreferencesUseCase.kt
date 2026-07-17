package com.example.reely.domain.usecase.movie

import com.example.reely.domain.model.WatchMonetizationType
import com.example.reely.domain.repository.MovieRepository
import javax.inject.Inject

class UpdateWatchProviderPreferencesUseCase @Inject constructor(
    private val repository: MovieRepository,
) {

    suspend operator fun invoke(
        providerIds: Set<Int>,
        monetizationTypes: Set<WatchMonetizationType>,
    ): Result<Unit> {
        return repository.updateWatchProviderPreferences(
            providerIds = providerIds,
            monetizationTypes = monetizationTypes,
        )
    }
}