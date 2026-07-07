package com.example.reely.data.local.room.entity

import androidx.room.Entity

@Entity(
    tableName = "movie_user_states",
    primaryKeys = ["movieId", "userId"]
)
data class MovieUserStateEntity(
    val movieId: Int,
    val userId: Int,
    val status: String,
    val watchedAt: String?,
    val rating: Double?
)