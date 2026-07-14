package com.example.reely.data.local.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.reely.data.local.room.entity.MovieViewingEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MovieViewingDao {

    @Insert
    suspend fun insert(
        viewing: com.example.reely.data.local.room.entity.MovieViewingEntity,
    ): Long

    @Query(
        """
        SELECT *
        FROM movie_viewings
        ORDER BY watchedAt DESC, id DESC
        """
    )
    fun observeAll(): Flow<List<com.example.reely.data.local.room.entity.MovieViewingEntity>>

    @Query(
        """
        SELECT *
        FROM movie_viewings
        WHERE movieId = :movieId
        ORDER BY watchedAt DESC, id DESC
        """
    )
    fun observeForMovie(
        movieId: Int,
    ): Flow<List<com.example.reely.data.local.room.entity.MovieViewingEntity>>

    @Query(
        """
        SELECT *
        FROM movie_viewings
        WHERE movieId = :movieId
        ORDER BY watchedAt DESC, id DESC
        """
    )
    suspend fun getForMovie(
        movieId: Int,
    ): List<com.example.reely.data.local.room.entity.MovieViewingEntity>

    @Query(
        """
        SELECT *
        FROM movie_viewings
        WHERE id = :viewingId
        LIMIT 1
        """
    )
    suspend fun getById(
        viewingId: Long,
    ): com.example.reely.data.local.room.entity.MovieViewingEntity?

    @Query(
        """
        DELETE FROM movie_viewings
        WHERE id = :viewingId
        """
    )
    suspend fun deleteById(viewingId: Long): Int

    @Query(
        """
        SELECT COUNT(*)
        FROM movie_viewings
        WHERE movieId = :movieId
        """
    )
    suspend fun countForMovie(movieId: Int): Int

    @Query(
        """
        SELECT EXISTS(
            SELECT 1
            FROM movie_viewings
            WHERE movieId = :movieId
        )
        """
    )
    suspend fun hasViewings(movieId: Int): Boolean
}