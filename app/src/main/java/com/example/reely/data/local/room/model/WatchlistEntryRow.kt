package com.example.reely.data.local.room.model

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.example.reely.data.local.room.entity.GenreEntity
import com.example.reely.data.local.room.entity.MovieEntity
import com.example.reely.data.local.room.entity.MovieGenreCrossRef
import java.time.Instant

data class WatchlistEntryRow(
    @Embedded
    val movie: MovieEntity,

    val addedAt: Instant,

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