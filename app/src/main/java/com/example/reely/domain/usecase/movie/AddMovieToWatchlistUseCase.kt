package com.example.reely.domain.usecase.movie

import com.example.reely.domain.model.MovieDetails
import com.example.reely.domain.repository.MovieRepository
import javax.inject.Inject

class AddMovieToWatchlistUseCase @Inject constructor(
    private val repository: MovieRepository
) {

    suspend operator fun invoke(movie: MovieDetails) : Result<Boolean>{
        return repository.addToWatchlist(movie = movie)
    }

}