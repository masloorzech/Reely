package com.example.reely.data.local.room.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "movie_genres",
    primaryKeys = ["movieId", "genreId"],
    foreignKeys = [
        ForeignKey(
            entity = _root_ide_package_.com.example.reely.data.local.room.entity.MovieEntity::class,
            parentColumns = ["id"],
            childColumns = ["movieId"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = _root_ide_package_.com.example.reely.data.local.room.entity.GenreEntity::class,
            parentColumns = ["id"],
            childColumns = ["genreId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [
        Index("movieId"),
        Index("genreId"),
    ],
)
data class MovieGenreCrossRef(
    val movieId: Int,
    val genreId: Int,
)