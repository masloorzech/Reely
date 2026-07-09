package com.example.reely.di

import android.content.Context
import androidx.room.Room
import com.example.reely.data.local.room.ReelyDatabase
import com.example.reely.data.local.room.dao.MovieDao
import com.example.reely.data.local.room.dao.UserDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideReelyDatabase(
        @ApplicationContext context: Context
    ): ReelyDatabase {
        return Room.databaseBuilder(
            context,
            ReelyDatabase::class.java,
            "reely_database"
        )
            .build()
    }

    @Provides
    @Singleton
    fun provideMovieDao(
        database: ReelyDatabase
    ): MovieDao {
        return database.movieDao()
    }

    @Provides
    @Singleton
    fun provideUserDao(
        database: ReelyDatabase
    ): UserDao {
        return database.userDao()
    }
}