package com.example.reely.data.local.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.example.reely.data.local.room.entity.MovieViewingEntity
import com.example.reely.data.local.room.model.RecentWatchedMovieRow
import kotlinx.coroutines.flow.Flow

@Dao
interface MovieViewingDao {

    @Insert
    suspend fun insert(
        viewing: MovieViewingEntity,
    ): Long

    @Transaction
    @Query(
        """
        SELECT
            movies.*,

            (
                SELECT movie_viewings.id
                FROM movie_viewings
                WHERE movie_viewings.movieId = movies.id
                ORDER BY
                    movie_viewings.watchedAt DESC,
                    movie_viewings.id DESC
                LIMIT 1
            ) AS viewingId,

            (
                SELECT movie_viewings.watchedAt
                FROM movie_viewings
                WHERE movie_viewings.movieId = movies.id
                ORDER BY
                    movie_viewings.watchedAt DESC,
                    movie_viewings.id DESC
                LIMIT 1
            ) AS watchedAt,

            (
                SELECT AVG(movie_ratings.score)
                FROM movie_ratings
                WHERE movie_ratings.movieId = movies.id
            ) AS averageUserRating

        FROM movies

        WHERE EXISTS (
            SELECT 1
            FROM movie_viewings
            WHERE movie_viewings.movieId = movies.id
        )

        AND (
            SELECT COUNT(DISTINCT movie_ratings.profileId)
            FROM movie_ratings
            WHERE movie_ratings.movieId = movies.id
        ) = 2

        ORDER BY watchedAt DESC
        """
    )
    fun observeWatchedMovies():
            Flow<List<RecentWatchedMovieRow>>

    @Query(
        """
        SELECT *
        FROM movie_viewings
        ORDER BY watchedAt DESC, id DESC
        """
    )
    fun observeAll(): Flow<List<MovieViewingEntity>>

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
    ): Flow<List<MovieViewingEntity>>

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
    ): List<MovieViewingEntity>

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
    ): MovieViewingEntity?

    @Query(
        """
        DELETE FROM movie_viewings
        WHERE id = :viewingId
        """
    )
    suspend fun deleteById(viewingId: Long): Int

    @Transaction
    @Query(
        """
    SELECT
        movies.*,
        movie_viewings.id AS viewingId,
        movie_viewings.watchedAt AS watchedAt,
        (
            SELECT AVG(rating.score)
            FROM movie_ratings AS rating
            WHERE rating.movieId = movies.id
        ) AS averageUserRating
    FROM movie_viewings
    INNER JOIN movies
        ON movies.id = movie_viewings.movieId
    ORDER BY
        movie_viewings.watchedAt DESC,
        movie_viewings.id DESC
    LIMIT :limit
    """
    )
    fun observeRecentWatchedMovies(
        limit: Int,
    ): Flow<List<RecentWatchedMovieRow>>

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