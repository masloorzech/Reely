package com.example.reely.data.local.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.example.reely.data.local.room.entity.GenreEntity
import com.example.reely.data.local.room.entity.MovieGenreCrossRef

@Dao
interface GenreDao {

    @Upsert
    suspend fun upsertAll(genres: List<com.example.reely.data.local.room.entity.GenreEntity>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCrossReferences(
        references: List<com.example.reely.data.local.room.entity.MovieGenreCrossRef>,
    )

    @Query(
        """
        DELETE FROM movie_genres
        WHERE movieId = :movieId
        """
    )
    suspend fun deleteCrossReferences(movieId: Int)

    @Query(
        """
        SELECT genres.*
        FROM genres
        INNER JOIN movie_genres
            ON genres.id = movie_genres.genreId
        WHERE movie_genres.movieId = :movieId
        ORDER BY genres.name ASC
        """
    )
    suspend fun getForMovie(movieId: Int): List<com.example.reely.data.local.room.entity.GenreEntity>

    @Query(
        """
        DELETE FROM genres
        WHERE NOT EXISTS(
            SELECT 1
            FROM movie_genres
            WHERE movie_genres.genreId = genres.id
        )
        """
    )
    suspend fun deleteOrphanGenres(): Int

    @Transaction
    suspend fun replaceForMovie(
        movieId: Int,
        genres: List<com.example.reely.data.local.room.entity.GenreEntity>,
    ) {
        upsertAll(genres)
        deleteCrossReferences(movieId)

        if (genres.isNotEmpty()) {
            insertCrossReferences(
                genres.map { genre ->
                    _root_ide_package_.com.example.reely.data.local.room.entity.MovieGenreCrossRef(
                        movieId = movieId,
                        genreId = genre.id,
                    )
                }
            )
        }

        deleteOrphanGenres()
    }
}