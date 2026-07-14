package com.example.reely.data.local.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.reely.data.local.room.entity.PopularMovieCacheEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PopularMovieCacheDao {

    @Query(
        """
            SELECT * 
            FROM popular_movie_cache
            ORDER BY position ASC
        """
    )
    fun observeAll(): Flow<List<PopularMovieCacheDao>>

    @Query(
        """
        SELECT MAX(fetchedAt)
        FROM popular_movie_cache
        """
    )
    suspend fun getLatestFetchedAt(): Long?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(
        entries: List<com.example.reely.data.local.room.entity.PopularMovieCacheEntity>,
    )


    @Query("DELETE FROM popular_movie_cache")
    suspend fun clear()

    @Query(
        """
        DELETE FROM popular_movie_cache
        WHERE fetchedAt < :cutoff
        """
    )
    suspend fun deleteOlderThan(cutoff: Long)

    @Transaction
    suspend fun replaceAll(
        entries: List<com.example.reely.data.local.room.entity.PopularMovieCacheEntity>,
    ) {
        clear()
        insertAll(entries)
    }
}