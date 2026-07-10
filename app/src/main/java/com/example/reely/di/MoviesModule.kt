package com.example.reely.di

import android.content.Context
import android.content.SharedPreferences
import com.example.reely.data.local.LocalMediaDataSource
import com.example.reely.data.local.MoviesLocalDataSource
import com.example.reely.data.local.RecentSearchesLocalDataSource
import com.example.reely.data.local.recentSearches.SharedPrefsRecentSearchesLocalDataSource
import com.example.reely.data.local.room.RoomMoviesLocalDataSource
import com.example.reely.data.local.storage.AndroidLocalMediaDataSource
import com.example.reely.data.remote.MoviesRemoteDataSource
import com.example.reely.data.remote.tmdb.TmdbMoviesRemoteDataSource
import com.example.reely.data.repository.MoviesRepositoryImpl
import com.example.reely.domain.repository.MoviesRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class MoviesModule {

    @Binds
    @Singleton
    abstract fun bindMoviesRemoteDataSource(
        impl: TmdbMoviesRemoteDataSource
    ): MoviesRemoteDataSource

    @Binds
    @Singleton
    abstract fun bindMoviesLocalDataSource(
        impl: RoomMoviesLocalDataSource
    ): MoviesLocalDataSource

    @Binds
    @Singleton
    abstract fun bindMoviesRepository(
        impl: MoviesRepositoryImpl
    ): MoviesRepository

    @Binds
    @Singleton
    abstract fun bindLocalMediaDataSource(
        impl: AndroidLocalMediaDataSource
    ): LocalMediaDataSource

    @Binds
    @Singleton
    abstract fun bindRecentSearchesDataSource(
        impl: SharedPrefsRecentSearchesLocalDataSource
    ): RecentSearchesLocalDataSource

    companion object {

        @Provides
        @Singleton
        fun provideSharedPreferences(
            @ApplicationContext context: Context
        ): SharedPreferences =
            context.getSharedPreferences(
                "reely_preferences",
                Context.MODE_PRIVATE
            )
    }
}