package com.example.reely.data.repository

import com.example.reely.data.remote.MoviesRemoteDataSource
import com.example.reely.data.remote.model.toDomain
import com.example.reely.domain.model.Movie
import com.example.reely.domain.repository.MoviesRepository
import javax.inject.Inject

class MoviesRepositoryImpl @Inject constructor(
    private val remoteDataSource: MoviesRemoteDataSource,
) : MoviesRepository{


    override suspend fun getPopularMovies(): Result<List<Movie>> {
        return runCatching {
            remoteDataSource.getPopularMovies()
                .map { it.toDomain() }
        }
    }

    override suspend fun search(query: String): Result<List<Movie>> {
        return runCatching {
            remoteDataSource.search(query)
                .map { it.toDomain() }
        }
    }

    override suspend fun getMovieDetails(id: Int): Result<Movie> {
        return runCatching {
            remoteDataSource.getMovieDetails(id)
                .toDomain()
        }
    }
}