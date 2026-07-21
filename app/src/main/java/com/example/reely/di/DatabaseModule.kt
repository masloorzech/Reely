package com.example.reely.di

import android.content.Context
import androidx.room.Room
import com.example.reely.data.local.room.ReelyDatabase
import com.example.reely.data.local.room.dao.GenreDao
import com.example.reely.data.local.room.dao.MovieDao
import com.example.reely.data.local.room.dao.MovieRatingDao
import com.example.reely.data.local.room.dao.MovieViewingDao
import com.example.reely.data.local.room.dao.PopularMovieCacheDao
import com.example.reely.data.local.room.dao.ProfileDao
import com.example.reely.data.local.room.dao.StatisticsDao
import com.example.reely.data.local.room.dao.WatchProviderDao
import com.example.reely.data.local.room.dao.WatchlistDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.time.Clock
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideReelyDatabase(
        @ApplicationContext context: Context,
    ): ReelyDatabase {
        return Room.databaseBuilder(
            context,
            ReelyDatabase::class.java,
            "reely_database_v2",
        )
            .fallbackToDestructiveMigration(true)
            .build()
    }

    @Provides
    fun provideProfileDao(
        database: ReelyDatabase,
    ): ProfileDao = database.profileDao()

    @Provides
    fun provideMovieDao(
        database: ReelyDatabase,
    ): MovieDao = database.movieDao()

    @Provides
    fun provideGenreDao(
        database: ReelyDatabase,
    ): GenreDao = database.genreDao()

    @Provides
    fun provideWatchlistDao(
        database: ReelyDatabase,
    ): WatchlistDao = database.watchlistDao()

    @Provides
    fun provideMovieViewingDao(
        database: ReelyDatabase,
    ): MovieViewingDao = database.movieViewingDao()

    @Provides
    fun provideMovieRatingDao(
        database: ReelyDatabase,
    ): MovieRatingDao = database.movieRatingDao()

    @Provides
    fun providePopularMovieCacheDao(
        database: ReelyDatabase,
    ): PopularMovieCacheDao =
        database.popularMovieCacheDao()

    @Provides
    fun provideWatchProviderDao(
        database: ReelyDatabase,
    ): WatchProviderDao {
        return database.watchProviderDao()
    }

    @Provides
    fun provideStatisticsDao(
        database: ReelyDatabase,
    ): StatisticsDao {
        return database.statisticsDao()
    }

    @Provides
    @Singleton
    fun provideClock(): Clock =
        Clock.systemUTC()
}