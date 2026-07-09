package com.example.reely.domain.usecase.movie

import com.example.reely.domain.model.Movie
import com.example.reely.domain.model.UserId
import com.example.reely.domain.repository.MoviesRepository
import com.example.reely.domain.repository.UsersRepository
import javax.inject.Inject

class GetRecentWatchedMoviesUseCase @Inject constructor(
    private val repository: MoviesRepository
) {

    suspend operator fun invoke(userId: UserId) : Result<List<Movie>>{
        return repository.getRecentWatchedMovies(userId)
    }

}