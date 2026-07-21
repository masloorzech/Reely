package com.example.reely.data.local.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.reely.data.local.room.converter.RoomConverters
import com.example.reely.data.local.room.dao.GenreDao
import com.example.reely.data.local.room.dao.MovieDao
import com.example.reely.data.local.room.dao.MovieRatingDao
import com.example.reely.data.local.room.dao.MovieViewingDao
import com.example.reely.data.local.room.dao.PopularMovieCacheDao
import com.example.reely.data.local.room.dao.ProfileDao
import com.example.reely.data.local.room.dao.StatisticsDao
import com.example.reely.data.local.room.dao.WatchProviderDao
import com.example.reely.data.local.room.dao.WatchlistDao
import com.example.reely.data.local.room.entity.GenreEntity
import com.example.reely.data.local.room.entity.MovieEntity
import com.example.reely.data.local.room.entity.MovieGenreCrossRef
import com.example.reely.data.local.room.entity.MovieRatingEntity
import com.example.reely.data.local.room.entity.MovieViewingEntity
import com.example.reely.data.local.room.entity.MovieWatchProviderEntity
import com.example.reely.data.local.room.entity.MovieWatchProviderSyncEntity
import com.example.reely.data.local.room.entity.PopularMovieCacheEntity
import com.example.reely.data.local.room.entity.ProfileEntity
import com.example.reely.data.local.room.entity.SelectedWatchProviderEntity
import com.example.reely.data.local.room.entity.SelectedWatchProviderTypeEntity
import com.example.reely.data.local.room.entity.WatchProviderEntity
import com.example.reely.data.local.room.entity.WatchlistEntryEntity

@Database(
    entities = [
       ProfileEntity::class,
        MovieEntity::class,
        GenreEntity::class,
        MovieGenreCrossRef::class,
        WatchlistEntryEntity::class,
        MovieViewingEntity::class,
        MovieRatingEntity::class,
        PopularMovieCacheEntity::class,
        WatchProviderEntity::class,
        MovieWatchProviderEntity::class,
        MovieWatchProviderSyncEntity::class,
        SelectedWatchProviderEntity::class,
        SelectedWatchProviderTypeEntity::class,
    ],
    version = 2,
    exportSchema = true,
)
@TypeConverters(RoomConverters::class)
abstract class ReelyDatabase : RoomDatabase() {

    abstract fun profileDao(): ProfileDao

    abstract fun movieDao():MovieDao

    abstract fun genreDao(): GenreDao

    abstract fun watchlistDao(): WatchlistDao

    abstract fun movieViewingDao(): MovieViewingDao

    abstract fun movieRatingDao(): MovieRatingDao

    abstract fun popularMovieCacheDao(): PopularMovieCacheDao

    abstract fun watchProviderDao(): WatchProviderDao

    abstract fun statisticsDao(): StatisticsDao
}