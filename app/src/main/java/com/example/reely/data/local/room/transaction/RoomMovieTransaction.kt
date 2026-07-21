package com.example.reely.data.local.room.transaction

import androidx.room.withTransaction
import com.example.reely.data.local.room.ReelyDatabase
import com.example.reely.data.local.room.dao.GenreDao
import com.example.reely.data.local.room.dao.MovieDao
import com.example.reely.data.local.room.dao.MovieRatingDao
import com.example.reely.data.local.room.dao.MovieViewingDao
import com.example.reely.data.local.room.dao.ProfileDao
import com.example.reely.data.local.room.dao.WatchlistDao
import com.example.reely.data.local.room.entity.MovieGenreCrossRef
import com.example.reely.data.local.room.entity.MovieRatingEntity
import com.example.reely.data.local.room.entity.MovieViewingEntity
import com.example.reely.data.local.room.entity.WatchlistEntryEntity
import com.example.reely.data.local.room.mapper.toEntity
import com.example.reely.data.local.room.mapper.toGenreEntities
import com.example.reely.domain.model.MovieDetails
import com.example.reely.domain.model.ProfilePosition
import com.example.reely.domain.model.Rating
import java.time.Clock
import java.time.Instant
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RoomMovieTransactions @Inject constructor(
    private val database: ReelyDatabase,
    private val movieDao: MovieDao,
    private val genreDao: GenreDao,
    private val watchlistDao: WatchlistDao,
    private val viewingDao: MovieViewingDao,
    private val ratingDao: MovieRatingDao,
    private val profileDao: ProfileDao,
    private val clock: Clock,
) {

    suspend fun addToWatchlist(
        movie: MovieDetails,
    ): Boolean = database.withTransaction {
        val now = clock.instant()

        saveMovieGraph(
            movie = movie,
            syncedAt = now,
        )

        val insertedRowId = watchlistDao.insert(
            WatchlistEntryEntity(
                movieId = movie.id,
                addedAt = now,
            )
        )

        insertedRowId != -1L
    }

    suspend fun markAsWatched(
        movie: MovieDetails,
        watchedAt: LocalDate,
        firstProfileRating: Rating,
        secondProfileRating: Rating,
    ): Long = database.withTransaction {
        val now = clock.instant()

        saveMovieGraph(
            movie = movie,
            syncedAt = now,
        )

        recordViewingInOpenTransaction(
            movieId = movie.id,
            watchedAt = watchedAt,
            firstProfileRating = firstProfileRating,
            secondProfileRating = secondProfileRating,
            createdAt = now,
        ).also {
            watchlistDao.deleteByMovieId(movie.id)
        }
    }

    suspend fun rewatchMovie(
        movie: MovieDetails,
        watchedAt: LocalDate,
        firstProfileRating: Rating,
        secondProfileRating: Rating,
    ): Long = database.withTransaction {
        val now = clock.instant()

        saveMovieGraph(
            movie = movie,
            syncedAt = now,
        )

        recordViewingInOpenTransaction(
            movieId = movie.id,
            watchedAt = watchedAt,
            firstProfileRating = firstProfileRating,
            secondProfileRating = secondProfileRating,
            createdAt = now,
        )
    }

    suspend fun updateRatings(
        movieId: Int,
        firstProfileRating: Rating,
        secondProfileRating: Rating,
    ) {
        database.withTransaction {
            check(viewingDao.hasViewings(movieId)) {
                "Cannot rate a movie without a viewing: $movieId"
            }

            saveRatingsInOpenTransaction(
                movieId = movieId,
                firstProfileRating = firstProfileRating,
                secondProfileRating = secondProfileRating,
                updatedAt = clock.instant(),
            )
        }
    }

    suspend fun removeFromWatchlist(
        movieId: Int,
    ): Boolean = database.withTransaction {
        val deletedRows = watchlistDao.deleteByMovieId(movieId)

        movieDao.deleteIfOrphan(movieId)
        genreDao.deleteOrphanGenres()

        deletedRows == 1
    }

    suspend fun deleteViewing(
        viewingId: Long,
    ): Boolean = database.withTransaction {
        val viewing = viewingDao.getById(viewingId)
            ?: return@withTransaction false

        val deletedRows = viewingDao.deleteById(viewingId)

        check(deletedRows == 1) {
            "Viewing could not be deleted: $viewingId"
        }

        val hasRemainingViewings =
            viewingDao.hasViewings(viewing.movieId)

        if (!hasRemainingViewings) {
            ratingDao.deleteForMovie(viewing.movieId)
        }

        movieDao.deleteIfOrphan(viewing.movieId)
        genreDao.deleteOrphanGenres()

        true
    }

    private suspend fun saveMovieGraph(
        movie: MovieDetails,
        syncedAt: Instant,
    ) {
        movieDao.upsert(
            movie.toEntity(syncedAt = syncedAt)
        )

        val genres = movie.toGenreEntities()

        if (genres.isNotEmpty()) {
            genreDao.upsertAll(genres)
        }

        genreDao.deleteCrossReferences(movie.id)

        if (genres.isNotEmpty()) {
            genreDao.insertCrossReferences(
                genres.map { genre ->
                    MovieGenreCrossRef(
                        movieId = movie.id,
                        genreId = genre.id,
                    )
                }
            )
        }

        genreDao.deleteOrphanGenres()
    }

    private suspend fun recordViewingInOpenTransaction(
        movieId: Int,
        watchedAt: LocalDate,
        firstProfileRating: Rating,
        secondProfileRating: Rating,
        createdAt: Instant,
    ): Long {
        val viewingId = viewingDao.insert(
            MovieViewingEntity(
                movieId = movieId,
                watchedAt = watchedAt,
                createdAt = createdAt,
                updatedAt = createdAt,
            )
        )

        check(viewingId > 0) {
            "Viewing could not be created for movie: $movieId"
        }

        saveRatingsInOpenTransaction(
            movieId = movieId,
            firstProfileRating = firstProfileRating,
            secondProfileRating = secondProfileRating,
            updatedAt = createdAt,
        )

        return viewingId
    }

    private suspend fun saveRatingsInOpenTransaction(
        movieId: Int,
        firstProfileRating: Rating,
        secondProfileRating: Rating,
        updatedAt: Instant,
    ) {
        val firstProfile = profileDao.getByPosition(
            ProfilePosition.FIRST
        ) ?: error("FIRST profile does not exist")

        val secondProfile = profileDao.getByPosition(
            ProfilePosition.SECOND
        ) ?: error("SECOND profile does not exist")

        ratingDao.saveAll(
            listOf(
                MovieRatingEntity(
                    movieId = movieId,
                    profileId = firstProfile.id,
                    score = firstProfileRating.value,
                    createdAt = updatedAt,
                    updatedAt = updatedAt,
                ),
                MovieRatingEntity(
                    movieId = movieId,
                    profileId = secondProfile.id,
                    score = secondProfileRating.value,
                    createdAt = updatedAt,
                    updatedAt = updatedAt,
                ),
            )
        )
    }
}