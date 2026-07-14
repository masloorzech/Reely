package com.example.reely.data.local.room.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import java.time.Instant

@Entity(
    tableName = "movie_ratings",
    primaryKeys = ["movieId", "profileId"],
    foreignKeys = [
        ForeignKey(
            entity = _root_ide_package_.com.example.reely.data.local.room.entity.MovieEntity::class,
            parentColumns = ["id"],
            childColumns = ["movieId"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = _root_ide_package_.com.example.reely.data.local.room.entity.ProfileEntity::class,
            parentColumns = ["id"],
            childColumns = ["profileId"],
            onDelete = ForeignKey.RESTRICT,
        ),
    ],
    indices = [
        Index("movieId"),
        Index("profileId"),
    ],
)
data class MovieRatingEntity(
    val movieId: Int,
    val profileId: Int,
    val score: Int,
    val createdAt: Instant,
    val updatedAt: Instant,
) {
    init {
        require(score in 1..10) {
            "Movie rating must be between 1 and 10"
        }
    }
}