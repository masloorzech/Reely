package com.example.reely.domain.usecase.movie

import com.example.reely.domain.model.Movie
import com.example.reely.domain.repository.MoviesRepository
import javax.inject.Inject

class AddMovieToWatchlistUseCase @Inject constructor(
    private val repository: MoviesRepository
) {

    suspend operator fun invoke(movie: Movie) : Result<Unit>{
        return repository.addMovieToWatchlist(movie = movie)
    }

}