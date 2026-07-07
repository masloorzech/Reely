package com.example.reely.domain.usecase

import com.example.reely.domain.model.Movie
import com.example.reely.domain.repository.MoviesRepository
import javax.inject.Inject

class SearchMovieUseCase @Inject constructor(
    private val repository : MoviesRepository
){

    suspend operator fun invoke(query : String) : Result<List<Movie>>{
        return repository.search(query)
    }

}