package com.example.reely.data.local.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.reely.data.local.room.entity.WatchlistEntryEntity
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