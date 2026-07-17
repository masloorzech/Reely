package com.example.reely.data.local.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.example.reely.data.local.room.entity.MovieWatchProviderEntity
import com.example.reely.data.local.room.entity.MovieWatchProviderSyncEntity
import com.example.reely.data.local.room.entity.SelectedWatchProviderEntity
import com.example.reely.data.local.room.entity.SelectedWatchProviderTypeEntity
import com.example.reely.data.local.room.entity.WatchProviderEntity
import com.example.reely.domain.model.WatchMonetizationType
import java.time.Instant
import kotlinx.coroutines.flow.Flow

@Dao
interface WatchProviderDao {

    @Query(
        """
        SELECT *
        FROM watch_providers
        WHERE countryCode = :countryCode
        ORDER BY
            displayPriority IS NULL,
            displayPriority,
            providerName
        """
    )
    fun observeCatalog(
        countryCode: String,
    ): Flow<List<WatchProviderEntity>>

    @Query(
        """
        SELECT MAX(fetchedAt)
        FROM watch_providers
        WHERE countryCode = :countryCode
        """
    )
    suspend fun getLatestCatalogFetchedAt(
        countryCode: String,
    ): Instant?

    @Query(
        """
        SELECT providerId
        FROM selected_watch_providers
        WHERE countryCode = :countryCode
        ORDER BY selectedAt
        """
    )
    fun observeSelectedProviderIds(
        countryCode: String,
    ): Flow<List<Int>>

    @Query(
        """
        SELECT monetizationType
        FROM selected_watch_provider_types
        """
    )
    fun observeSelectedMonetizationTypes():
            Flow<List<WatchMonetizationType>>

    @Query(
        """
        SELECT *
        FROM movie_watch_providers
        WHERE countryCode = :countryCode
        """
    )
    fun observeMovieOffers(
        countryCode: String,
    ): Flow<List<MovieWatchProviderEntity>>

    @Query(
        """
        SELECT *
        FROM movie_watch_provider_sync
        WHERE countryCode = :countryCode
        """
    )
    fun observeMovieSyncStates(
        countryCode: String,
    ): Flow<List<MovieWatchProviderSyncEntity>>

    @Query(
        """
        SELECT *
        FROM movie_watch_provider_sync
        WHERE movieId = :movieId
          AND countryCode = :countryCode
        LIMIT 1
        """
    )
    suspend fun getMovieSyncState(
        movieId: Int,
        countryCode: String,
    ): MovieWatchProviderSyncEntity?

    @Upsert
    suspend fun upsertProviders(
        providers: List<WatchProviderEntity>,
    )

    @Query(
        """
        DELETE FROM watch_providers
        WHERE countryCode = :countryCode
          AND providerId NOT IN (
              SELECT providerId
              FROM selected_watch_providers
              WHERE countryCode = :countryCode
          )
        """
    )
    suspend fun deleteCatalog(
        countryCode: String,
    )

    @Insert(
        onConflict = OnConflictStrategy.REPLACE,
    )
    suspend fun insertMovieOffers(
        offers: List<MovieWatchProviderEntity>,
    )

    @Query(
        """
        DELETE FROM movie_watch_providers
        WHERE movieId = :movieId
          AND countryCode = :countryCode
        """
    )
    suspend fun deleteMovieOffers(
        movieId: Int,
        countryCode: String,
    )

    @Upsert
    suspend fun upsertMovieSyncState(
        syncState: MovieWatchProviderSyncEntity,
    )

    @Query(
        """
        DELETE FROM selected_watch_providers
        WHERE countryCode = :countryCode
        """
    )
    suspend fun deleteSelectedProviders(
        countryCode: String,
    )

    @Insert(
        onConflict = OnConflictStrategy.REPLACE,
    )
    suspend fun insertSelectedProviders(
        providers: List<SelectedWatchProviderEntity>,
    )

    @Query(
        """
        DELETE FROM selected_watch_provider_types
        """
    )
    suspend fun deleteSelectedMonetizationTypes()

    @Insert(
        onConflict = OnConflictStrategy.REPLACE,
    )
    suspend fun insertSelectedMonetizationTypes(
        types: List<SelectedWatchProviderTypeEntity>,
    )

    @Transaction
    suspend fun replaceCatalog(
        countryCode: String,
        providers: List<WatchProviderEntity>,
    ) {
        deleteCatalog(countryCode)

        if (providers.isNotEmpty()) {
            upsertProviders(providers)
        }
    }

    @Transaction
    suspend fun replaceMovieProviders(
        movieId: Int,
        countryCode: String,
        providers: List<WatchProviderEntity>,
        offers: List<MovieWatchProviderEntity>,
        justWatchLink: String?,
        syncedAt: Instant,
    ) {
        if (providers.isNotEmpty()) {
            upsertProviders(providers)
        }

        deleteMovieOffers(
            movieId = movieId,
            countryCode = countryCode,
        )

        if (offers.isNotEmpty()) {
            insertMovieOffers(offers)
        }

        upsertMovieSyncState(
            MovieWatchProviderSyncEntity(
                movieId = movieId,
                countryCode = countryCode,
                justWatchLink = justWatchLink,
                syncedAt = syncedAt,
            )
        )
    }

    @Transaction
    suspend fun replacePreferences(
        countryCode: String,
        providerIds: Set<Int>,
        types: Set<WatchMonetizationType>,
        selectedAt: Instant,
    ) {
        deleteSelectedProviders(countryCode)

        if (providerIds.isNotEmpty()) {
            insertSelectedProviders(
                providerIds.map { providerId ->
                    SelectedWatchProviderEntity(
                        providerId = providerId,
                        countryCode = countryCode,
                        selectedAt = selectedAt,
                    )
                }
            )
        }

        deleteSelectedMonetizationTypes()

        if (types.isNotEmpty()) {
            insertSelectedMonetizationTypes(
                types.map { type ->
                    SelectedWatchProviderTypeEntity(
                        monetizationType = type,
                    )
                }
            )
        }
    }
}