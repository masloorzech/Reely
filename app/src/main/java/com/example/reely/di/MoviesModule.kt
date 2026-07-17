package com.example.reely.di

import android.content.Context
import android.content.SharedPreferences
import com.example.reely.data.local.LocalMediaDataSource
import com.example.reely.data.local.MovieLocalDataSource
import com.example.reely.data.local.PopularMovieCacheLocalDataSource
import com.example.reely.data.local.RecentSearchesLocalDataSource
import com.example.reely.data.local.WatchProviderLocalDataSource
import com.example.reely.data.local.recentSearches.SharedPrefsRecentSearchesLocalDataSource
import com.example.reely.data.local.room.RoomMovieLocalDataSource
import com.example.reely.data.local.room.RoomPopularMovieCacheLocalDataSource
import com.example.reely.data.local.room.RoomWatchProviderLocalDataSource
import com.example.reely.data.local.storage.AndroidLocalMediaDataSource
import com.example.reely.data.remote.MoviesRemoteDataSource
import com.example.reely.data.remote.tmdb.TmdbMoviesRemoteDataSource
import com.example.reely.data.repository.MovieRepositoryImpl
import com.example.reely.domain.repository.MovieRepository
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
    abstract fun bindMoviesRepository(
        impl: MovieRepositoryImpl
    ): MovieRepository

    @Binds
    @Singleton
    abstract fun bindMovieLocalDataSource(
        implementation: RoomMovieLocalDataSource,
    ): MovieLocalDataSource

    @Binds
    @Singleton
    abstract fun bindPopularMovieCacheLocalDataSource(
        implementation: RoomPopularMovieCacheLocalDataSource,
    ): PopularMovieCacheLocalDataSource

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

    @Binds
    @Singleton
    abstract fun bindWatchProviderLocalDataSource(
        implementation: RoomWatchProviderLocalDataSource,
    ): WatchProviderLocalDataSource

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