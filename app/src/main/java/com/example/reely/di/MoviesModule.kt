package com.example.reely.di

import com.example.reely.data.remote.MoviesRemoteDataSource
import com.example.reely.data.remote.tmdb.TmdbMoviesRemoteDataSource
import com.example.reely.data.repository.MoviesRepositoryImpl
import com.example.reely.domain.repository.MoviesRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
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
        impl: MoviesRepositoryImpl
    ): MoviesRepository
}