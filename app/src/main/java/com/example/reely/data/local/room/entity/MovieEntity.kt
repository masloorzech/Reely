package com.example.reely.data.local.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "movies")
data class MovieEntity(
    @PrimaryKey
    val id: Int,
    val title: String,
    val overview: String,
    val localPosterPath: String?,
    val remotePosterPath: String?,
    val backdropPath: String?,
    val releaseDate: String?,
    val rating: Double?,
    val voteCount: Int?,
    val popularity: Double?,
    val runtime: Int?
)