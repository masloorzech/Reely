package com.example.reely.domain.usecase.movie

import com.example.reely.domain.repository.MoviesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveRecentSearchesUseCase @Inject constructor(
    private val repository: MoviesRepository
) {

    operator fun invoke() : Flow<List<String>> {
        return repository.observeRecentSearches()
    }

}