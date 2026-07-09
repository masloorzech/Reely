package com.example.reely.di

import com.example.reely.data.local.LocalMediaDataSource
import com.example.reely.data.local.LocalStorage.AndroidLocalMediaDataSource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class LocalMediaModule {

    @Binds
    @Singleton
    abstract fun bindLocalMediaDataSource(
        impl: AndroidLocalMediaDataSource
    ): LocalMediaDataSource
}