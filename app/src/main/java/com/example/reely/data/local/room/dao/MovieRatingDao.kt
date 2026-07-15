package com.example.reely.data.local.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.reely.data.local.room.entity.MovieRatingEntity
import kotlinx.coroutines.flow.Flow
import java.time.Instant

@Dao
interface MovieRatingDao {

    @Query(
        """
        SELECT *
        FROM movie_ratings
        WHERE movieId = :movieId
        ORDER BY profileId ASC
        """
    )
    fun observeForMovie(
        movieId: Int,
    ): Flow<List<MovieRatingEntity>>

    @Query(
        """
        SELECT *
        FROM movie_ratings
        WHERE movieId = :movieId
        ORDER BY profileId ASC
        """
    )
    suspend fun getForMovie(
        movieId: Int,
    ): List<MovieRatingEntity>

    @Query(
        """
        SELECT *
        FROM movie_ratings
        WHERE movieId = :movieId
          AND profileId = :profileId
        LIMIT 1
        """
    )
    suspend fun get(
        movieId: Int,
        profileId: Int,
    ): MovieRatingEntity?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertIgnore(
        rating: MovieRatingEntity,
    ): Long

    @Query(
        """
        UPDATE movie_ratings
        SET score = :score,
            updatedAt = :updatedAt
        WHERE movieId = :movieId
          AND profileId = :profileId
        """
    )
    suspend fun updateScore(
        movieId: Int,
        profileId: Int,
        score: Int,
        updatedAt: Instant,
    ): Int

    @Transaction
    suspend fun save(rating: MovieRatingEntity) {
        val insertedRowId = insertIgnore(rating)

        if (insertedRowId == -1L) {
            updateScore(
                movieId = rating.movieId,
                profileId = rating.profileId,
                score = rating.score,
                updatedAt = rating.updatedAt,
            )
        }
    }

    @Transaction
    suspend fun saveAll(
        ratings: List<MovieRatingEntity>,
    ) {
        ratings.forEach { rating ->
            val insertedRowId = insertIgnore(rating)

            if (insertedRowId == -1L) {
                updateScore(
                    movieId = rating.movieId,
                    profileId = rating.profileId,
                    score = rating.score,
                    updatedAt = rating.updatedAt,
                )
            }
        }
    }

    @Query(
        """
        DELETE FROM movie_ratings
        WHERE movieId = :movieId
        """
    )
    suspend fun deleteForMovie(movieId: Int): Int
}