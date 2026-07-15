package com.example.reely.domain.usecase.movie

import com.example.reely.domain.repository.MovieRepository
import jakarta.inject.Inject


class DeleteViewingUseCase @Inject constructor(
    private val repository: MovieRepository,
) {

    suspend operator fun invoke(
        viewingId: Long,
    ): Result<Boolean> {
        require(viewingId > 0) {
            "Viewing ID must be positive"
        }

        return repository.deleteViewing(viewingId)
    }
}