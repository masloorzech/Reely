package com.example.reely.domain.model

enum class ProviderFilterScope {
    ANY_PROVIDER,
    MY_PROVIDERS,
    SELECTED_PROVIDERS,
}

data class MovieProviderFilter(
    val isEnabled: Boolean = false,
    val scope: ProviderFilterScope =
        ProviderFilterScope.MY_PROVIDERS,
    val selectedProviderIds: Set<Int> = emptySet(),
    val monetizationTypes: Set<WatchMonetizationType> =
        setOf(WatchMonetizationType.FLATRATE),
) {

    fun matches(
        offers: Set<MovieProviderOffer>,
        myProviderIds: Set<Int>,
    ): Boolean {
        if (!isEnabled) {
            return true
        }

        if (monetizationTypes.isEmpty()) {
            return false
        }

        val allowedProviderIds = when (scope) {
            ProviderFilterScope.ANY_PROVIDER -> {
                null
            }

            ProviderFilterScope.MY_PROVIDERS -> {
                myProviderIds
            }

            ProviderFilterScope.SELECTED_PROVIDERS -> {
                selectedProviderIds
            }
        }

        if (
            allowedProviderIds != null &&
            allowedProviderIds.isEmpty()
        ) {
            return false
        }

        return offers.any { offer ->
            val matchesType =
                offer.monetizationType in monetizationTypes

            val matchesProvider =
                allowedProviderIds == null ||
                        offer.providerId in allowedProviderIds

            matchesType && matchesProvider
        }
    }
}