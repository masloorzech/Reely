package com.example.reely.di

import com.example.reely.data.repository.UsersRepositoryImpl
import com.example.reely.domain.repository.UsersRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class UserModule {

    @Binds
    abstract fun bindUsersRepository(
        impl: UsersRepositoryImpl
    ): UsersRepository

    @Binds
    abstract fun bindUserLocalDataSource(
        impl : RoomUsersLocalDataSource
    ): UsersLocalDataSource


}