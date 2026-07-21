package com.example.reely.data.local.room

import com.example.reely.data.local.PopularMovieCacheLocalDataSource
import com.example.reely.data.local.room.dao.PopularMovieCacheDao
import com.example.reely.data.local.room.entity.PopularMovieCacheEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RoomPopularMovieCacheLocalDataSource @Inject constructor(
    private val dao: PopularMovieCacheDao,
) : PopularMovieCacheLocalDataSource {

    override fun observeMovies():
            Flow<List<PopularMovieCacheEntity>> {
        return dao.observeAll()
    }

    override suspend fun getMovies():
            List<PopularMovieCacheEntity> {
        return dao.getAll()
    }

    override suspend fun getLatestFetchedAt(): Long? {
        return dao.getLatestFetchedAt()
    }

    override suspend fun replaceMovies(
        movies: List<PopularMovieCacheEntity>,
    ) {
        dao.replaceAll(movies)
    }

    override suspend fun deleteOlderThan(
        cutoffTimestamp: Long,
    ) {
        dao.deleteOlderThan(cutoffTimestamp)
    }

    override suspend fun clear() {
        dao.clear()
    }

}