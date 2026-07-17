package com.example.reely.data.local.room.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.reely.domain.model.WatchMonetizationType
import java.time.Instant

@Entity(
    tableName = "watch_providers",
    primaryKeys = [
        "providerId",
        "countryCode",
    ],
    indices = [
        Index(
            value = [
                "countryCode",
                "displayPriority",
            ],
        ),
    ],
)
data class WatchProviderEntity(
    val providerId: Int,
    val providerName: String,
    val logoPath: String?,
    val displayPriority: Int?,
    val countryCode: String,
    val fetchedAt: Instant,
)

@Entity(
    tableName = "movie_watch_providers",
    primaryKeys = [
        "movieId",
        "providerId",
        "monetizationType",
        "countryCode",
    ],
    foreignKeys = [
        ForeignKey(
            entity = MovieEntity::class,
            parentColumns = ["id"],
            childColumns = ["movieId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [
        Index(value = ["movieId"]),
        Index(value = ["providerId"]),
        Index(value = ["countryCode"]),
    ],
)
data class MovieWatchProviderEntity(
    val movieId: Int,
    val providerId: Int,
    val monetizationType: WatchMonetizationType,
    val countryCode: String,
)

@Entity(
    tableName = "movie_watch_provider_sync",
    primaryKeys = [
        "movieId",
        "countryCode",
    ],
    foreignKeys = [
        ForeignKey(
            entity = MovieEntity::class,
            parentColumns = ["id"],
            childColumns = ["movieId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [
        Index(value = ["movieId"]),
    ],
)
data class MovieWatchProviderSyncEntity(
    val movieId: Int,
    val countryCode: String,
    val justWatchLink: String?,
    val syncedAt: Instant,
)

@Entity(
    tableName = "selected_watch_providers",
    primaryKeys = [
        "providerId",
        "countryCode",
    ],
)
data class SelectedWatchProviderEntity(
    val providerId: Int,
    val countryCode: String,
    val selectedAt: Instant,
)

@Entity(
    tableName = "selected_watch_provider_types",
)
data class SelectedWatchProviderTypeEntity(
    @PrimaryKey
    val monetizationType: WatchMonetizationType,
)