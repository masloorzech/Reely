package com.example.reely.domain.usecase.movie

import com.example.reely.domain.model.MovieWatchProviders
import com.example.reely.domain.repository.MovieRepository
import javax.inject.Inject

class GetMovieProvidersUseCase @Inject constructor(
    private val repository: MovieRepository
) {

    suspend operator fun invoke(movieId: Int) : Result<MovieWatchProviders>{
        return repository.getMovieProviders(movieId)
    }

}