package com.example.reely.domain.usecase.movie

import com.example.reely.domain.model.WatchlistEntry
import com.example.reely.domain.repository.MovieRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveWatchlistUseCase @Inject constructor(
    private val repository: MovieRepository,
) {

    operator fun invoke(): Flow<List<WatchlistEntry>> {
        return repository.observeWatchlist()
    }
}