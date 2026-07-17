package com.example.reely.data.local.room.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.Instant
import java.time.LocalDate

@Entity(
    tableName = "movie_viewings",
    foreignKeys = [
        ForeignKey(
            entity = MovieEntity::class,
            parentColumns = ["id"],
            childColumns = ["movieId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [
        Index("movieId"),
        Index("watchedAt"),
    ],
)
data class MovieViewingEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val movieId: Int,
    val watchedAt: LocalDate,
    val createdAt: Instant,
    val updatedAt: Instant,
)