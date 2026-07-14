package com.example.reely.data.local.room.mapper

import com.example.reely.data.local.room.entity.GenreEntity
import com.example.reely.data.local.room.entity.MovieEntity
import com.example.reely.domain.model.MovieDetails
import java.time.Instant

fun MovieDetails.toEntity(
    syncedAt: Instant,
): MovieEntity {
    return MovieEntity(
        id = id,
        title = title,
        originalTitle = originalTitle,
        overview = overview,
        posterPath = posterPath,
        backdropPath = backdropPath,
        releaseDate = releaseDate,
        runtimeMinutes = runtimeMinutes,
        originalLanguage = originalLanguage,
        publicRating = publicRating,
        publicVoteCount = publicVoteCount,
        lastSyncedAt = syncedAt,
    )
}

fun MovieDetails.toGenreEntities(): List<GenreEntity> =
    genres.map { genre ->
        GenreEntity(
            id = genre.id,
            name = genre.name,
        )
    }