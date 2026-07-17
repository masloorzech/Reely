package com.example.reely.domain.usecase.movie

import com.example.reely.domain.model.WatchMonetizationType
import com.example.reely.domain.repository.MovieRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class ObserveSelectedWatchProviderTypesUseCase @Inject constructor(
    private val repository: MovieRepository,
) {

    operator fun invoke():
            Flow<Set<WatchMonetizationType>> {
        return repository.observeSelectedWatchProviderTypes()
    }
}