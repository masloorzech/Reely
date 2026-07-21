package com.example.reely.data.local.room.dao

import androidx.room.Dao
import androidx.room.Query
import com.example.reely.data.local.room.entity.GenreEntity
import com.example.reely.data.local.room.entity.MovieEntity
import com.example.reely.data.local.room.entity.MovieGenreCrossRef
import com.example.reely.data.local.room.entity.MovieRatingEntity
import com.example.reely.data.local.room.entity.MovieViewingEntity
import com.example.reely.data.local.room.entity.WatchlistEntryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface StatisticsDao {

    @Query(
        """
        SELECT *
        FROM movies
        ORDER BY id
        """
    )
    fun observeMovies(): Flow<List<MovieEntity>>

    @Query(
        """
        SELECT *
        FROM movie_viewings
        ORDER BY watchedAt ASC, id ASC
        """
    )
    fun observeViewings(): Flow<List<MovieViewingEntity>>

    @Query(
        """
        SELECT *
        FROM movie_ratings
        ORDER BY movieId ASC, profileId ASC
        """
    )
    fun observeRatings(): Flow<List<MovieRatingEntity>>

    @Query(
        """
        SELECT *
        FROM genres
        ORDER BY name ASC
        """
    )
    fun observeGenres(): Flow<List<GenreEntity>>

    @Query(
        """
        SELECT *
        FROM movie_genres
        ORDER BY movieId ASC, genreId ASC
        """
    )
    fun observeMovieGenres(): Flow<List<MovieGenreCrossRef>>

    @Query(
        """
        SELECT *
        FROM watchlist_entries
        ORDER BY addedAt DESC
        """
    )
    fun observeWatchlistEntries(): Flow<List<WatchlistEntryEntity>>
}