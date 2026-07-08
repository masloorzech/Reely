package com.example.reely.data.local.room

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.reely.data.local.room.dao.MovieDao
import com.example.reely.data.local.room.dao.UserDao
import com.example.reely.data.local.room.entity.MovieEntity
import com.example.reely.data.local.room.entity.MovieUserWatchEntity
import com.example.reely.data.local.room.entity.MovieWatchlistEntity
import com.example.reely.data.local.room.entity.UserEntity

@Database(
    entities = [
        MovieEntity::class,
        MovieUserWatchEntity::class,
        MovieWatchlistEntity::class,
        UserEntity::class
    ],
    version = 3,
    exportSchema = false,
)
abstract class ReelyDatabase : RoomDatabase() {
    abstract fun movieDao(): MovieDao
    abstract fun userDao(): UserDao

}