package com.example.reely.domain.usecase.statistics

import com.example.reely.domain.model.Movie
import com.example.reely.domain.model.UserId
import com.example.reely.domain.repository.MoviesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.YearMonth
import java.time.ZoneId
import javax.inject.Inject

class ObserveMonthlyWatchedMoviesUseCase @Inject constructor(
    private val moviesRepository: MoviesRepository
) {

    operator fun invoke(
        userId: UserId,
        month: YearMonth
    ): Flow<Map<LocalDate, List<Movie>>> {
        return moviesRepository
            .observeWatchedMovies()
            .map { movies ->
                movies
                    .mapNotNull { movie ->
                        val watchedAt = movie.userWatches[userId]
                            ?.watchedAt
                            ?: return@mapNotNull null

                        val watchedDate = LocalDate.parse(watchedAt)
                            ?: return@mapNotNull null

                        if (YearMonth.from(watchedDate) != month) {
                            return@mapNotNull null
                        }

                        watchedDate to movie
                    }
                    .groupBy(
                        keySelector = { pair -> pair.first },
                        valueTransform = { pair -> pair.second }
                    )
            }
    }
}