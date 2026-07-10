package com.example.reely.data.local.room.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.reely.data.local.room.entity.MovieEntity
import com.example.reely.data.local.room.entity.MovieUserWatchEntity
import com.example.reely.data.local.room.entity.MovieWatchlistEntity
import kotlinx.coroutines.flow.Flow


@Dao
interface MovieDao {

    @Upsert
    suspend fun upsertMovie(movie: MovieEntity)

    @Query("SELECT * FROM movie_watchlist WHERE movieId IN (:movieIds)")
    suspend fun getWatchlistItemsForMovies(movieIds: List<Int>): List<MovieWatchlistEntity>

    @Query("SELECT * FROM movies WHERE id = :movieId")
    suspend fun getMovie(movieId: Int): MovieEntity?

    // WATCHLIST
    @Query(
        """
    SELECT DISTINCT movies.*
    FROM movies
    INNER JOIN movie_user_watches 
        ON movies.id = movie_user_watches.movieId
    """
    )
    fun observeWatchedMovieEntities(): Flow<List<MovieEntity>>

    @Query("SELECT * FROM movie_user_watches WHERE movieId IN (:movieIds)")
    fun observeUserWatchesForMovies(movieIds: List<Int>): Flow<List<MovieUserWatchEntity>>

    @Query("SELECT * FROM movie_watchlist WHERE movieId IN (:movieIds)")
    fun observeWatchlistItemsForMovies(movieIds: List<Int>): Flow<List<MovieWatchlistEntity>>

    @Upsert
    suspend fun upsertWatchlistItem(item: MovieWatchlistEntity)

    @Query("SELECT * FROM movie_watchlist WHERE movieId = :movieId")
    suspend fun getWatchlistItem(movieId: Int): MovieWatchlistEntity?

    @Query("DELETE FROM movie_watchlist WHERE movieId = :movieId")
    suspend fun deleteWatchlistItem(movieId: Int)

    @Query("""
        SELECT movies.*
        FROM movies
        INNER JOIN movie_user_watches 
            ON movies.id = movie_user_watches.movieId
        WHERE movie_user_watches.userId = :mainUserId
        ORDER BY movie_user_watches.watchedAt DESC
        LIMIT 8
""")
    suspend fun getRecentWatchedMovies(mainUserId: Int) : List<MovieEntity>

    @Query(
        """
        SELECT movies.*
        FROM movies
        INNER JOIN movie_watchlist 
            ON movies.id = movie_watchlist.movieId
        """
    )
    suspend fun getWatchlistMovieEntities(): List<MovieEntity>

    @Upsert
    suspend fun upsertUserWatch(watch: MovieUserWatchEntity)

    @Query("SELECT * FROM movie_user_watches WHERE movieId = :movieId")
    suspend fun getUserWatchesForMovie(movieId: Int): List<MovieUserWatchEntity>

    @Query("SELECT * FROM movie_user_watches WHERE movieId IN (:movieIds)")
    suspend fun getUserWatchesForMovies(movieIds: List<Int>): List<MovieUserWatchEntity>

    @Query("DELETE FROM movie_user_watches WHERE movieId = :movieId AND userId = :userId")
    suspend fun deleteUserWatch(movieId: Int, userId: Int)

    @Query(
        """
        SELECT DISTINCT movies.*
        FROM movies
        INNER JOIN movie_user_watches 
            ON movies.id = movie_user_watches.movieId
        """
    )
    suspend fun getWatchedMovieEntities(): List<MovieEntity>

    @Query(
        """
        SELECT DISTINCT movies.*
        FROM movies
        LEFT JOIN movie_watchlist 
            ON movies.id = movie_watchlist.movieId
        LEFT JOIN movie_user_watches 
            ON movies.id = movie_user_watches.movieId
        WHERE movie_watchlist.movieId IS NOT NULL
           OR movie_user_watches.movieId IS NOT NULL
        """
    )
    suspend fun getUserMovieEntities(): List<MovieEntity>

    @Query("DELETE FROM movie_user_watches WHERE movieId = :movieId")
    suspend fun deleteUserWatchesForMovie(movieId: Int)
    
}