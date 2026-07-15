package com.example.reely.di

import com.example.reely.data.local.ProfileLocalDataSource
import com.example.reely.data.local.room.RoomProfileLocalDataSource
import com.example.reely.data.repository.ProfileRepositoryImpl
import com.example.reely.domain.repository.ProfileRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class UserModule {

    @Binds
    abstract fun bindProfileRepository(
        impl: ProfileRepositoryImpl
    ): ProfileRepository

    @Binds
    abstract fun bindProfileLocalDataSource(
        impl : RoomProfileLocalDataSource
    ): ProfileLocalDataSource

}