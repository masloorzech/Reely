package com.example.reely.data.local.room.mapper

import com.example.reely.data.local.room.entity.GenreEntity
import com.example.reely.data.local.room.entity.MovieEntity
import com.example.reely.data.local.room.entity.MovieRatingEntity
import com.example.reely.data.local.room.entity.MovieViewingEntity
import com.example.reely.data.local.room.entity.PopularMovieCacheEntity
import com.example.reely.data.local.room.entity.ProfileEntity
import com.example.reely.data.local.room.entity.WatchlistEntryEntity
import com.example.reely.data.local.room.model.RecentWatchedMovieRow
import com.example.reely.data.local.room.model.WatchlistEntryRow
import com.example.reely.domain.model.Genre
import com.example.reely.domain.model.MovieDetails
import com.example.reely.domain.model.MoviePreview
import com.example.reely.domain.model.MovieRating
import com.example.reely.domain.model.MovieViewing
import com.example.reely.domain.model.Profile
import com.example.reely.domain.model.ProfileId
import com.example.reely.domain.model.Rating
import com.example.reely.domain.model.RecentWatchedMovie
import com.example.reely.domain.model.WatchlistEntry
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

fun ProfileEntity.toDomain(): Profile {
    return Profile(
        id = ProfileId(id),
        position = position,
        name = name,
        avatarPath = avatarPath,
    )
}

fun PopularMovieCacheEntity.toDomain(): MoviePreview {
    return MoviePreview(
        id = movieId,
        title = title,
        posterPath = posterPath,
        releaseDate = null
    )
}

fun MoviePreview.toCacheEntity(
    position: Int,
    fetchedAt: Long,
): PopularMovieCacheEntity {
    return PopularMovieCacheEntity(
        movieId = id,
        title = title,
        posterPath = posterPath,
        position = position,
        fetchedAt = fetchedAt,
    )
}

fun WatchlistEntryRow.toDomain(): WatchlistEntry {
    return WatchlistEntry(
        movie = MovieDetails(
            id = movie.id,
            title = movie.title,
            originalTitle = movie.originalTitle,
            overview = movie.overview,
            posterPath = movie.posterPath,
            backdropPath = movie.backdropPath,
            releaseDate = movie.releaseDate,
            runtimeMinutes = movie.runtimeMinutes,
            originalLanguage = movie.originalLanguage,
            publicRating = movie.publicRating,
            publicVoteCount = movie.publicVoteCount,
            genres = genres.map { genre ->
                Genre(
                    id = genre.id,
                    name = genre.name,
                )
            },
        ),
        addedAt = addedAt,
    )
}

fun MovieViewingEntity.toDomain(): MovieViewing {
    return MovieViewing(
        id = id,
        movieId = movieId,
        watchedAt = watchedAt,
        createdAt = createdAt,
        updatedAt = updatedAt,
    )
}

fun MovieRatingEntity.toDomain(): MovieRating {
    return MovieRating(
        movieId = movieId,
        profileId = ProfileId(profileId),
        rating = Rating(score),
        createdAt = createdAt,
        updatedAt = updatedAt,
    )
}

fun RecentWatchedMovieRow.toDomain(): RecentWatchedMovie {
    return RecentWatchedMovie(
        viewingId = viewingId,
        movieId = movie.id,
        title = movie.title,
        posterPath = movie.posterPath,
        watchedAt = watchedAt,
        averageUserRating = averageUserRating,
        releaseDate = movie.releaseDate,
        runtime = movie.runtimeMinutes,
        publicRating = movie.publicRating,
        overview = movie.overview,
        genres = genres.map { Genre(it.id, it.name) }
    )
}