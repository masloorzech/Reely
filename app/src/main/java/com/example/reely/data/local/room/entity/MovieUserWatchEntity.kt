package com.example.reely.data.local.room.entity

import androidx.room.Entity

@Entity(
    tableName = "movie_user_watches",
    primaryKeys = ["movieId", "userId"]
)
data class MovieUserWatchEntity(
    val movieId: Int,
    val userId: Int,
    val watchedAt: String? = null,
    val rating: Double? = null
)