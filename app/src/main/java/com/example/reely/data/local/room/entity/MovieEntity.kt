package com.example.reely.data.local.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.Instant
import java.time.LocalDate

@Entity(tableName = "movies")
data class MovieEntity(
    @PrimaryKey
    val id: Int,
    val title: String,
    val originalTitle: String?,
    val overview: String,
    val posterPath: String?,
    val backdropPath: String?,
    val releaseDate: LocalDate?,
    val runtimeMinutes: Int?,
    val originalLanguage: String?,
    val publicRating: Double?,
    val publicVoteCount: Int?,
    val lastSyncedAt: Instant
)
