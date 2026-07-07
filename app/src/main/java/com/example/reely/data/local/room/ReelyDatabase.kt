package com.example.reely.data.local.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.reely.data.local.room.dao.MovieDao
import com.example.reely.data.local.room.entity.MovieEntity
import com.example.reely.data.local.room.entity.MovieUserStateEntity

@Database(
    entities = [
        MovieEntity::class,
        MovieUserStateEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class ReelyDatabase : RoomDatabase() {
    abstract fun movieDao(): MovieDao
}