package com.example.reely.data.local

import com.example.reely.data.local.room.entity.PopularMovieCacheEntity
import kotlinx.coroutines.flow.Flow

interface PopularMovieCacheLocalDataSource {

    fun observeMovies(): Flow<List<PopularMovieCacheEntity>>

    suspend fun getMovies(): List<PopularMovieCacheEntity>

    suspend fun getLatestFetchedAt(): Long?

    suspend fun replaceMovies(
        movies: List<PopularMovieCacheEntity>,
    )

    suspend fun deleteOlderThan(
        cutoffTimestamp: Long,
    )

    suspend fun clear()
}