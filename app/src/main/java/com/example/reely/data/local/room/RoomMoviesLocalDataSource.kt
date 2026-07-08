package com.example.reely.data.local.room

import com.example.reely.data.local.MoviesLocalDataSource
import com.example.reely.data.local.model.LocalMovie
import com.example.reely.data.local.model.LocalMovieUserWatch
import com.example.reely.data.local.model.LocalMovieWithUserData
import com.example.reely.data.local.room.dao.MovieDao
import com.example.reely.data.local.room.entity.MovieWatchlistEntity
import com.example.reely.data.local.room.mapper.toEntity
import com.example.reely.data.local.room.mapper.toLocalMovie
import com.example.reely.data.local.room.mapper.toLocalUserWatch
import javax.inject.Inject

class RoomMoviesLocalDataSource @Inject constructor(
    private val movieDao: MovieDao
) : MoviesLocalDataSource {

    override suspend fun saveMovieData(movie: LocalMovie) {
        movieDao.upsertMovie(movie.toEntity())
    }

    override suspend fun getMovieData(movieId: Int): LocalMovie? {
        return movieDao.getMovie(movieId)
            ?.toLocalMovie()
    }

    override suspend fun getMovieWithUserData(movieId: Int): LocalMovieWithUserData? {
        val movieEntity = movieDao.getMovie(movieId) ?: return null

        val watchlistItem = movieDao.getWatchlistItem(movieId)

        val userWatches = movieDao.getUserWatchesForMovie(movieId)
            .associate { entity ->
                entity.userId to entity.toLocalUserWatch()
            }

        return LocalMovieWithUserData(
            movie = movieEntity.toLocalMovie(),
            isInWatchlist = watchlistItem != null,
            userWatches = userWatches
        )
    }

    override suspend fun getUserMovies(): List<LocalMovieWithUserData> {
        val movieEntities = movieDao.getUserMovieEntities()

        if (movieEntities.isEmpty()) {
            return emptyList()
        }

        val movieIds = movieEntities.map { movieEntity ->
            movieEntity.id
        }

        val watchlistMovieIds = movieDao.getWatchlistItemsForMovies(movieIds)
            .map { watchlistEntity ->
                watchlistEntity.movieId
            }
            .toSet()

        val userWatchesByMovieId = movieDao.getUserWatchesForMovies(movieIds)
            .groupBy { watchEntity ->
                watchEntity.movieId
            }

        return movieEntities.map { movieEntity ->
            val userWatches = userWatchesByMovieId[movieEntity.id]
                .orEmpty()
                .associate { watchEntity ->
                    watchEntity.userId to watchEntity.toLocalUserWatch()
                }

            LocalMovieWithUserData(
                movie = movieEntity.toLocalMovie(),
                isInWatchlist = movieEntity.id in watchlistMovieIds,
                userWatches = userWatches
            )
        }
    }

    override suspend fun addToWatchlist(
        movieId: Int,
        addedAt: String?,
        addedByUserId: Int?
    ) {
        movieDao.upsertWatchlistItem(
            MovieWatchlistEntity(
                movieId = movieId,
                addedAt = addedAt,
                addedByUserId = addedByUserId
            )
        )
    }

    override suspend fun removeFromWatchlist(movieId: Int) {
        movieDao.deleteWatchlistItem(movieId)
    }

    override suspend fun upsertUserWatch(
        movieId: Int,
        userId: Int,
        userWatch: LocalMovieUserWatch
    ) {
        movieDao.upsertUserWatch(
            userWatch.toEntity(
                movieId = movieId,
                userId = userId
            )
        )
    }

    override suspend fun deleteUserWatch(
        movieId: Int,
        userId: Int
    ) {
        movieDao.deleteUserWatch(
            movieId = movieId,
            userId = userId
        )
    }

    override suspend fun deleteUserWatchesForMovie(movieId: Int) {
        movieDao.deleteUserWatchesForMovie(movieId)
    }


}