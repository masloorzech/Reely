package com.example.reely.data.local

import com.example.reely.data.local.room.entity.MovieWatchProviderEntity
import com.example.reely.data.local.room.entity.MovieWatchProviderSyncEntity
import com.example.reely.data.local.room.entity.WatchProviderEntity
import com.example.reely.domain.model.MovieWatchProviders
import com.example.reely.domain.model.WatchMonetizationType
import kotlinx.coroutines.flow.Flow
import java.time.Instant

interface WatchProviderLocalDataSource {

    fun observeProviderCatalog(
        countryCode: String,
    ): Flow<List<WatchProviderEntity>>

    fun observeSelectedProviderIds(
        countryCode: String,
    ): Flow<List<Int>>

    fun observeSelectedMonetizationTypes():
            Flow<List<WatchMonetizationType>>

    fun observeMovieOffers(
        countryCode: String,
    ): Flow<List<MovieWatchProviderEntity>>

    fun observeMovieSyncStates(
        countryCode: String,
    ): Flow<List<MovieWatchProviderSyncEntity>>

    suspend fun getLatestCatalogFetchedAt(
        countryCode: String,
    ): Instant?

    suspend fun getMovieSyncState(
        movieId: Int,
        countryCode: String,
    ): MovieWatchProviderSyncEntity?

    suspend fun replaceProviderCatalog(
        countryCode: String,
        providers: List<WatchProviderEntity>,
    )

    suspend fun replaceMovieProviders(
        providers: MovieWatchProviders,
        syncedAt: Instant,
    )

    suspend fun replacePreferences(
        countryCode: String,
        providerIds: Set<Int>,
        types: Set<WatchMonetizationType>,
        selectedAt: Instant,
    )
}