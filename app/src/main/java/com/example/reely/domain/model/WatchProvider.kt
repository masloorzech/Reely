package com.example.reely.domain.model

import java.time.Instant

enum class WatchMonetizationType(
    val displayName: String,
) {
    FLATRATE("W abonamencie"),
    FREE("Bezpłatnie"),
    ADS("Z reklamami"),
    RENT("Wypożyczenie"),
    BUY("Kupno"),
}

data class MovieProviderOffer(
    val providerId: Int,
    val monetizationType: WatchMonetizationType,
)

data class CachedMovieWatchProviders(
    val movieId: Int,
    val countryCode: String,
    val justWatchLink: String?,
    val offers: Set<MovieProviderOffer>,
    val syncedAt: Instant,
)

data class ProviderRefreshResult(
    val totalMovies: Int,
    val refreshedMovies: Int,
    val failedMovieIds: List<Int>,
)


data class MovieWatchProviders(
    val movieId: Int,
    val countryCode: String,
    val justWatchLink: String?,
    val flatrate: List<WatchProvider>,
    val rent: List<WatchProvider>,
    val buy: List<WatchProvider>,
    val ads: List<WatchProvider>,
    val free: List<WatchProvider>,
) {

    fun offers(): Set<MovieProviderOffer> {
        return buildSet {
            flatrate.forEach { provider ->
                add(
                    provider.toOffer(
                        WatchMonetizationType.FLATRATE
                    )
                )
            }

            free.forEach { provider ->
                add(
                    provider.toOffer(
                        WatchMonetizationType.FREE
                    )
                )
            }

            ads.forEach { provider ->
                add(
                    provider.toOffer(
                        WatchMonetizationType.ADS
                    )
                )
            }

            rent.forEach { provider ->
                add(
                    provider.toOffer(
                        WatchMonetizationType.RENT
                    )
                )
            }

            buy.forEach { provider ->
                add(
                    provider.toOffer(
                        WatchMonetizationType.BUY
                    )
                )
            }
        }
    }

    fun allProviders(): List<WatchProvider> {
        return (
                flatrate +
                        free +
                        ads +
                        rent +
                        buy
                )
            .distinctBy { provider ->
                provider.providerId
            }
    }
}

data class WatchProvider(
    val providerId: Int,
    val providerName: String,
    val logoPath: String?,
    val displayPriority: Int?,
)

private fun WatchProvider.toOffer(
    type: WatchMonetizationType,
): MovieProviderOffer {
    return MovieProviderOffer(
        providerId = providerId,
        monetizationType = type,
    )
}