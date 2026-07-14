package com.example.reely.data.local

import com.example.reely.data.local.room.entity.MovieEntity
import com.example.reely.data.local.room.entity.MovieRatingEntity
import com.example.reely.data.local.room.entity.MovieViewingEntity
import com.example.reely.data.local.room.entity.WatchlistEntryEntity
import com.example.reely.domain.model.MovieDetails
import com.example.reely.domain.model.Rating
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface MovieLocalDataSource {

    suspend fun getMovie(
        movieId: Int,
    ): MovieEntity?

    fun observeMovie(
        movieId: Int,
    ): Flow<MovieEntity?>

    fun observeWatchlistEntries():
            Flow<List<WatchlistEntryEntity>>

    fun observeViewings():
            Flow<List<MovieViewingEntity>>

    fun observeViewingsForMovie(
        movieId: Int,
    ): Flow<List<MovieViewingEntity>>

    fun observeRatingsForMovie(
        movieId: Int,
    ): Flow<List<MovieRatingEntity>>

    suspend fun isOnWatchlist(
        movieId: Int,
    ): Boolean

    suspend fun addToWatchlist(
        movie: MovieDetails,
    ): Boolean

    suspend fun markAsWatched(
        movie: MovieDetails,
        watchedAt: LocalDate,
        firstProfileRating: Rating,
        secondProfileRating: Rating,
    ): Long

    suspend fun rewatchMovie(
        movie: MovieDetails,
        watchedAt: LocalDate,
        firstProfileRating: Rating,
        secondProfileRating: Rating,
    ): Long

    suspend fun updateRatings(
        movieId: Int,
        firstProfileRating: Rating,
        secondProfileRating: Rating,
    )

    suspend fun removeFromWatchlist(
        movieId: Int,
    ): Boolean

    suspend fun deleteViewing(
        viewingId: Long,
    ): Boolean
}