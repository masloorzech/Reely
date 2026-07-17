package com.example.reely.data.local.room

import com.example.reely.data.local.MovieLocalDataSource
import com.example.reely.data.local.room.dao.MovieDao
import com.example.reely.data.local.room.dao.MovieRatingDao
import com.example.reely.data.local.room.dao.MovieViewingDao
import com.example.reely.data.local.room.dao.WatchlistDao
import com.example.reely.data.local.room.entity.MovieEntity
import com.example.reely.data.local.room.entity.MovieRatingEntity
import com.example.reely.data.local.room.entity.MovieViewingEntity
import com.example.reely.data.local.room.mapper.toDomain
import com.example.reely.data.local.room.model.RecentWatchedMovieRow
import com.example.reely.data.local.room.model.WatchlistEntryRow
import com.example.reely.data.local.room.transaction.RoomMovieTransactions
import com.example.reely.domain.model.MovieDetails
import com.example.reely.domain.model.Rating
import com.example.reely.domain.model.RecentWatchedMovie
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RoomMovieLocalDataSource @Inject constructor(
    private val movieDao: MovieDao,
    private val watchlistDao: WatchlistDao,
    private val viewingDao: MovieViewingDao,
    private val ratingDao: MovieRatingDao,
    private val movieTransactions: RoomMovieTransactions,
) : MovieLocalDataSource {

    override suspend fun getAllMovieIds(): List<Int> {
        return movieDao.getAllIds()
    }

    override suspend fun getMovie(
        movieId: Int,
    ): MovieEntity? {
        return movieDao.getById(movieId)
    }

    override fun observeMovie(
        movieId: Int,
    ): Flow<MovieEntity?> {
        return movieDao.observeById(movieId)
    }

    override fun observeWatchlist(): Flow<List<WatchlistEntryRow>> {
        return watchlistDao.observeWatchlist()
    }

    override fun observeViewings():
            Flow<List<MovieViewingEntity>> {
        return viewingDao.observeAll()
    }

    override fun observeWatchedMovies():
            Flow<List<RecentWatchedMovie>> {
        return viewingDao
            .observeWatchedMovies()
            .map { rows ->
                rows.map { row ->
                    row.toDomain()
                }
            }
    }

    override fun observeViewingsForMovie(
        movieId: Int,
    ): Flow<List<MovieViewingEntity>> {
        return viewingDao.observeForMovie(movieId)
    }

    override fun observeRatingsForMovie(
        movieId: Int,
    ): Flow<List<MovieRatingEntity>> {
        return ratingDao.observeForMovie(movieId)
    }

    override suspend fun isOnWatchlist(
        movieId: Int,
    ): Boolean {
        return watchlistDao.contains(movieId)
    }

    override suspend fun addToWatchlist(
        movie: MovieDetails,
    ): Boolean {
        return movieTransactions.addToWatchlist(movie)
    }

    override suspend fun markAsWatched(
        movie: MovieDetails,
        watchedAt: LocalDate,
        firstProfileRating: Rating,
        secondProfileRating: Rating,
    ): Long {
        return movieTransactions.markAsWatched(
            movie = movie,
            watchedAt = watchedAt,
            firstProfileRating = firstProfileRating,
            secondProfileRating = secondProfileRating,
        )
    }

    override suspend fun rewatchMovie(
        movie: MovieDetails,
        watchedAt: LocalDate,
        firstProfileRating: Rating,
        secondProfileRating: Rating,
    ): Long {
        return movieTransactions.rewatchMovie(
            movie = movie,
            watchedAt = watchedAt,
            firstProfileRating = firstProfileRating,
            secondProfileRating = secondProfileRating,
        )
    }

    override suspend fun updateRatings(
        movieId: Int,
        firstProfileRating: Rating,
        secondProfileRating: Rating,
    ) {
        movieTransactions.updateRatings(
            movieId = movieId,
            firstProfileRating = firstProfileRating,
            secondProfileRating = secondProfileRating,
        )
    }

    override suspend fun removeFromWatchlist(
        movieId: Int,
    ): Boolean {
        return movieTransactions.removeFromWatchlist(movieId)
    }

    override suspend fun deleteViewing(
        viewingId: Long,
    ): Boolean {
        return movieTransactions.deleteViewing(viewingId)
    }

    override fun observeRecentWatchedMovies(
        limit: Int,
    ): Flow<List<RecentWatchedMovieRow>> {
        return viewingDao.observeRecentWatchedMovies(limit)
    }

    override suspend fun removeFromWatched(
        movieId: Int,
    ): Boolean {
        return ratingDao.deleteByMovieId(movieId) > 0
    }

}