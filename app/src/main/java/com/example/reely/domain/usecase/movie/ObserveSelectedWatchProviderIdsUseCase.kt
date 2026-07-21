package com.example.reely.domain.usecase.movie

import com.example.reely.domain.repository.MovieRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveSelectedWatchProviderIdsUseCase @Inject constructor(
    private val repository: MovieRepository,
) {

    operator fun invoke(): Flow<Set<Int>> {
        return repository.observeSelectedWatchProviderIds()
    }
}