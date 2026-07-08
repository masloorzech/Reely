package com.example.reely.data.local.room

import com.example.reely.data.local.MoviesLocalDataSource
import com.example.reely.data.local.model.LocalMovie
import com.example.reely.data.local.model.LocalMovieUserState
import com.example.reely.data.local.room.dao.MovieDao
import com.example.reely.data.local.room.mapper.toEntity
import com.example.reely.data.local.room.mapper.toLocalMovie
import javax.inject.Inject

class RoomMoviesLocalDataSource @Inject constructor(
    private val movieDao: MovieDao
) : MoviesLocalDataSource {

    override suspend fun saveMovieData(movie: LocalMovie) {
        movieDao.upsertMovie(movie.toEntity())

        movie.userStates.forEach { entry ->
            movieDao.upsertUserState(
                entry.value.toEntity(
                    movieId = movie.id,
                    userId = entry.key
                )
            )
        }
    }

    override suspend fun getMovieData(movieId: Int): LocalMovie? {
        val movieEntity = movieDao.getMovie(movieId) ?: return null

        val userStates = movieDao.getUserStatesForMovies(
            movieIds = listOf(movieId)
        )

        return movieEntity.toLocalMovie(
            userStates = userStates
        )
    }

    override suspend fun getUserMovies(): List<LocalMovie> {
        val movieEntities = movieDao.getUserMovieEntities()

        if (movieEntities.isEmpty()) {
            return emptyList()
        }

        val movieIds = movieEntities.map { movieEntity ->
            movieEntity.id
        }

        val userStatesByMovieId = movieDao.getUserStatesForMovies(movieIds)
            .groupBy { stateEntity ->
                stateEntity.movieId
            }

        return movieEntities.map { movieEntity ->
            movieEntity.toLocalMovie(
                userStates = userStatesByMovieId[movieEntity.id].orEmpty()
            )
        }
    }

    override suspend fun changeMovieUserState(
        movieId: Int,
        userId: Int,
        movieUserState: LocalMovieUserState
    ) {
        movieDao.upsertUserState(
            movieUserState.toEntity(
                movieId = movieId,
                userId = userId
            )
        )
    }
}