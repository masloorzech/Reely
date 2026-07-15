package com.example.reely.domain.usecase.movie

import com.example.reely.domain.repository.MovieRepository
import javax.inject.Inject

class RemoveMovieFromWatchedUseCase @Inject constructor(
    private val repository: MovieRepository
) {

    suspend operator fun invoke(
        movieId : Int
    ) : Result<Boolean>{
        return repository.removeFromWatched(movieId)
    }

}