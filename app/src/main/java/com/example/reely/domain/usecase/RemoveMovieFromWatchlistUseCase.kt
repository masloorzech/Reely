package com.example.reely.domain.usecase

import com.example.reely.domain.model.Movie
import com.example.reely.domain.repository.MoviesRepository
import javax.inject.Inject

class RemoveMovieFromWatchlistUseCase @Inject constructor(
    private val repository: MoviesRepository
){
    suspend operator fun invoke(movieId: Int): Result<Unit> {
        return repository.removeMovieFromWatchlist(movieId = movieId)
    }
}