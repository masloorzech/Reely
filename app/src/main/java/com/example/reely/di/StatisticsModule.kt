package com.example.reely.di

import com.example.reely.data.local.StatisticsLocalDataSource
import com.example.reely.data.local.room.RoomStatisticsLocalDataSource
import com.example.reely.data.repository.StatisticsRepositoryImpl
import com.example.reely.domain.repository.StatisticsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class StatisticsModule {

    @Binds
    @Singleton
    abstract fun bindStatisticsLocalDataSource(
        implementation: RoomStatisticsLocalDataSource,
    ): StatisticsLocalDataSource

    @Binds
    @Singleton
    abstract fun bindStatisticsRepository(
        implementation: StatisticsRepositoryImpl,
    ): StatisticsRepository
}