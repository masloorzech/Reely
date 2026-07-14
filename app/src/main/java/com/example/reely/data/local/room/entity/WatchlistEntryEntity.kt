package com.example.reely.data.local.room.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.Instant

@Entity(
    tableName = "watchlist_entries",
    foreignKeys = [
        ForeignKey(
            entity = _root_ide_package_.com.example.reely.data.local.room.entity.MovieEntity::class,
            parentColumns = ["id"],
            childColumns = ["movieId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [
        Index("addedAt"),
    ],
)
data class WatchlistEntryEntity(
    @PrimaryKey
    val movieId: Int,
    val addedAt: Instant,
)