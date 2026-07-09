package com.example.reely.domain.usecase.movie

import com.example.reely.domain.repository.MoviesRepository
import javax.inject.Inject

class RemoveMovieFromWatchlistUseCase @Inject constructor(
    private val repository: MoviesRepository
){
    suspend operator fun invoke(movieId: Int): Result<Unit> {
        return repository.removeMovieFromWatchlist(movieId = movieId)
    }
}