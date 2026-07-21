package com.example.reely.data.local.room

import com.example.reely.data.local.WatchProviderLocalDataSource
import com.example.reely.data.local.room.dao.WatchProviderDao
import com.example.reely.data.local.room.entity.MovieWatchProviderEntity
import com.example.reely.data.local.room.entity.MovieWatchProviderSyncEntity
import com.example.reely.data.local.room.entity.WatchProviderEntity
import com.example.reely.domain.model.MovieWatchProviders
import com.example.reely.domain.model.WatchMonetizationType
import kotlinx.coroutines.flow.Flow
import java.time.Instant
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RoomWatchProviderLocalDataSource @Inject constructor(
    private val dao: WatchProviderDao,
) : WatchProviderLocalDataSource {

    override fun observeProviderCatalog(
        countryCode: String,
    ): Flow<List<WatchProviderEntity>> {
        return dao.observeCatalog(countryCode)
    }

    override fun observeSelectedProviderIds(
        countryCode: String,
    ): Flow<List<Int>> {
        return dao.observeSelectedProviderIds(countryCode)
    }

    override fun observeSelectedMonetizationTypes():
            Flow<List<WatchMonetizationType>> {
        return dao.observeSelectedMonetizationTypes()
    }

    override fun observeMovieOffers(
        countryCode: String,
    ): Flow<List<MovieWatchProviderEntity>> {
        return dao.observeMovieOffers(countryCode)
    }

    override fun observeMovieSyncStates(
        countryCode: String,
    ): Flow<List<MovieWatchProviderSyncEntity>> {
        return dao.observeMovieSyncStates(countryCode)
    }

    override suspend fun getLatestCatalogFetchedAt(
        countryCode: String,
    ): Instant? {
        return dao.getLatestCatalogFetchedAt(countryCode)
    }

    override suspend fun getMovieSyncState(
        movieId: Int,
        countryCode: String,
    ): MovieWatchProviderSyncEntity? {
        return dao.getMovieSyncState(
            movieId = movieId,
            countryCode = countryCode,
        )
    }

    override suspend fun replaceProviderCatalog(
        countryCode: String,
        providers: List<WatchProviderEntity>,
    ) {
        dao.replaceCatalog(
            countryCode = countryCode,
            providers = providers,
        )
    }

    override suspend fun replaceMovieProviders(
        providers: MovieWatchProviders,
        syncedAt: Instant,
    ) {
        val providerEntities = providers
            .allProviders()
            .map { provider ->
                WatchProviderEntity(
                    providerId = provider.providerId,
                    providerName = provider.providerName,
                    logoPath = provider.logoPath,
                    displayPriority = provider.displayPriority,
                    countryCode = providers.countryCode,
                    fetchedAt = syncedAt,
                )
            }

        val offerEntities = providers
            .offers()
            .map { offer ->
                MovieWatchProviderEntity(
                    movieId = providers.movieId,
                    providerId = offer.providerId,
                    monetizationType =
                        offer.monetizationType,
                    countryCode = providers.countryCode,
                )
            }

        dao.replaceMovieProviders(
            movieId = providers.movieId,
            countryCode = providers.countryCode,
            providers = providerEntities,
            offers = offerEntities,
            justWatchLink = providers.justWatchLink,
            syncedAt = syncedAt,
        )
    }

    override suspend fun replacePreferences(
        countryCode: String,
        providerIds: Set<Int>,
        types: Set<WatchMonetizationType>,
        selectedAt: Instant,
    ) {
        dao.replacePreferences(
            countryCode = countryCode,
            providerIds = providerIds,
            types = types,
            selectedAt = selectedAt,
        )
    }
}