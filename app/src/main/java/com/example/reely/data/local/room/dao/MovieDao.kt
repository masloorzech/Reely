package com.example.reely.data.local.room.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.reely.data.local.room.entity.MovieEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MovieDao {

    @Upsert
    suspend fun upsert(movie: com.example.reely.data.local.room.entity.MovieEntity)

    @Query(
        """
        SELECT *
        FROM movies
        WHERE id = :movieId
        LIMIT 1
        """
    )
    suspend fun getById(movieId: Int): com.example.reely.data.local.room.entity.MovieEntity?

    @Query(
        """
        SELECT *
        FROM movies
        WHERE id = :movieId
        LIMIT 1
        """
    )
    fun observeById(movieId: Int): Flow<com.example.reely.data.local.room.entity.MovieEntity?>

    @Query(
        """
        SELECT EXISTS(
            SELECT 1
            FROM movies
            WHERE id = :movieId
        )
        """
    )
    suspend fun exists(movieId: Int): Boolean

    @Query("SELECT COUNT(*) FROM movies")
    suspend fun count(): Int

    @Query(
        """
        DELETE FROM movies
        WHERE id = :movieId
          AND NOT EXISTS(
              SELECT 1
              FROM watchlist_entries
              WHERE movieId = :movieId
          )
          AND NOT EXISTS(
              SELECT 1
              FROM movie_viewings
              WHERE movieId = :movieId
          )
        """
    )
    suspend fun deleteIfOrphan(movieId: Int): Int
}