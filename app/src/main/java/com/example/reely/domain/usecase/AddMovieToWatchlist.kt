package com.example.reely.domain.usecase

import com.example.reely.domain.model.Movie
import com.example.reely.domain.repository.MoviesRepository
import javax.inject.Inject

class AddMovieToWatchlist @Inject constructor(
    private val repository: MoviesRepository
) {

    suspend operator fun invoke(movie: Movie) : Result<Unit>{
        return repository.addMovieToWatchlist(movie = movie)
    }

}