package com.example.reely.data.local.room.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "popular_movie_cache",
    indices = [
        Index(
            value = ["position"],
            unique = true,
        ),
        Index("fetchedAt"),
    ],
)
data class PopularMovieCacheEntity(
    @PrimaryKey
    val movieId: Int,
    val title: String,
    val posterPath: String?,
    val position: Int,
    val fetchedAt: Long,
)