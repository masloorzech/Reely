package com.example.reely.data.local.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "movie_watchlist")
data class MovieWatchlistEntity(
    @PrimaryKey val movieId: Int,
    val addedAt: String? = null,
    val addedByUserId: Int? = null
)