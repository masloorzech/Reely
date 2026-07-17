package com.example.reely.data.local.room


import com.example.reely.data.local.StatisticsLocalDataSource
import com.example.reely.data.local.model.StatisticsLocalData
import com.example.reely.data.local.room.dao.StatisticsDao
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

@Singleton
class RoomStatisticsLocalDataSource @Inject constructor(
    private val statisticsDao: StatisticsDao,
) : StatisticsLocalDataSource {

    override fun observeData(): Flow<StatisticsLocalData> {
        val watchedData = combine(
            statisticsDao.observeMovies(),
            statisticsDao.observeViewings(),
            statisticsDao.observeRatings(),
        ) { movies, viewings, ratings ->
            Triple(
                movies,
                viewings,
                ratings,
            )
        }

        val libraryData = combine(
            statisticsDao.observeGenres(),
            statisticsDao.observeMovieGenres(),
            statisticsDao.observeWatchlistEntries(),
        ) { genres, movieGenres, watchlistEntries ->
            Triple(
                genres,
                movieGenres,
                watchlistEntries,
            )
        }

        return combine(
            watchedData,
            libraryData,
        ) { watched, library ->
            StatisticsLocalData(
                movies = watched.first,
                viewings = watched.second,
                ratings = watched.third,
                genres = library.first,
                movieGenres = library.second,
                watchlistEntries = library.third,
            )
        }
    }
}