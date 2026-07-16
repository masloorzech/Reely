package com.example.reely.data.local.room.model

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.example.reely.data.local.room.entity.GenreEntity
import com.example.reely.data.local.room.entity.MovieEntity
import com.example.reely.data.local.room.entity.MovieGenreCrossRef
import java.time.LocalDate

data class RecentWatchedMovieRow(
    @Embedded
    val movie: MovieEntity,

    val viewingId: Long,
    val watchedAt: LocalDate,
    val averageUserRating: Double?,

    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = MovieGenreCrossRef::class,
            parentColumn = "movieId",
            entityColumn = "genreId",
        ),
    )
    val genres: List<GenreEntity>,
)