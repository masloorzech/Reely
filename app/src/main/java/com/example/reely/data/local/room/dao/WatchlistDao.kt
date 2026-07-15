package com.example.reely.data.local.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.reely.data.local.room.entity.WatchlistEntryEntity
import com.example.reely.data.local.room.model.WatchlistEntryRow
import kotlinx.coroutines.flow.Flow

@Dao
interface WatchlistDao {

    @Query(
        """
        SELECT *
        FROM watchlist_entries
        ORDER BY addedAt DESC
        """
    )
    fun observeAll(): Flow<List<WatchlistEntryEntity>>

    @Transaction
    @Query(
        """
    SELECT
        movies.*,
        watchlist_entries.addedAt AS addedAt
    FROM watchlist_entries
    INNER JOIN movies
        ON movies.id = watchlist_entries.movieId
    ORDER BY watchlist_entries.addedAt DESC
    """
    )
    fun observeWatchlist(): Flow<List<WatchlistEntryRow>>

    @Query(
        """
        SELECT *
        FROM watchlist_entries
        WHERE movieId = :movieId
        LIMIT 1
        """
    )
    suspend fun getByMovieId(
        movieId: Int,
    ): WatchlistEntryEntity?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(
        entry: WatchlistEntryEntity,
    ): Long

    @Query(
        """
        DELETE FROM watchlist_entries
        WHERE movieId = :movieId
        """
    )
    suspend fun deleteByMovieId(movieId: Int): Int

    @Query(
        """
        SELECT EXISTS(
            SELECT 1
            FROM watchlist_entries
            WHERE movieId = :movieId
        )
        """
    )
    suspend fun contains(movieId: Int): Boolean

    @Query("SELECT COUNT(*) FROM watchlist_entries")
    suspend fun count(): Int

    @Query(
        """
        SELECT movieId
        FROM watchlist_entries
        ORDER BY RANDOM()
        LIMIT 1
        """
    )
    suspend fun getRandomMovieId(): Int?
}