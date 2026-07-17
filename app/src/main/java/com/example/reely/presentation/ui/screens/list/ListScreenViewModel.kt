package com.example.reely.presentation.ui.screens.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.reely.domain.model.CachedMovieWatchProviders
import com.example.reely.domain.model.Genre
import com.example.reely.domain.model.MovieProviderFilter
import com.example.reely.domain.model.ProviderFilterScope
import com.example.reely.domain.model.WatchMonetizationType
import com.example.reely.domain.model.WatchProvider
import com.example.reely.domain.usecase.movie.ObserveCachedMovieProvidersUseCase
import com.example.reely.domain.usecase.movie.ObserveSelectedWatchProviderIdsUseCase
import com.example.reely.domain.usecase.movie.ObserveSelectedWatchProviderTypesUseCase
import com.example.reely.domain.usecase.movie.ObserveWatchProviderCatalogUseCase
import com.example.reely.domain.usecase.movie.ObserveWatchedMoviesUseCase
import com.example.reely.domain.usecase.movie.ObserveWatchlistUseCase
import com.example.reely.domain.usecase.movie.RefreshLibraryWatchProvidersUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.ZoneOffset
import javax.inject.Inject

data class ListScreenUiState(
    val selectedSort: MovieSortOption =
        MovieSortOption.RATING_DESC,

    val selectedTab: MovieListTab =
        MovieListTab.WATCHLIST,

    val displayStyle: MovieDisplayStyle =
        MovieDisplayStyle.LIST,

    val selectedGenreIds: Set<Int> =
        emptySet(),

    val genres: List<Genre> =
        emptyList(),

    val searchQuery: String =
        "",

    val moviesList: List<ListMovieItem> =
        emptyList(),

    val availableProviders: List<WatchProvider> =
        emptyList(),

    val myProviderIds: Set<Int> =
        emptySet(),

    val providerFilter: MovieProviderFilter =
        MovieProviderFilter(),

    val isProviderFilterDialogVisible: Boolean =
        false,

    val unsyncedMoviesCount: Int =
        0,

    val isRefreshingProviders: Boolean =
        false,

    val providerMessage: String? =
        null,

    val isLoading: Boolean =
        true,

    val error: String? =
        null,
)

@HiltViewModel
class ListScreenViewModel @Inject constructor(
    private val observeWatchlistUseCase:
    ObserveWatchlistUseCase,

    private val observeWatchedMoviesUseCase:
    ObserveWatchedMoviesUseCase,

    private val observeWatchProviderCatalogUseCase:
    ObserveWatchProviderCatalogUseCase,

    private val observeSelectedWatchProviderIdsUseCase:
    ObserveSelectedWatchProviderIdsUseCase,

    private val observeSelectedWatchProviderTypesUseCase:
    ObserveSelectedWatchProviderTypesUseCase,

    private val observeCachedMovieProvidersUseCase:
    ObserveCachedMovieProvidersUseCase,

    private val refreshLibraryWatchProvidersUseCase:
    RefreshLibraryWatchProvidersUseCase,
) : ViewModel() {

    private var watchlistMovies:
            List<ListMovieItem> = emptyList()

    private var watchedMovies:
            List<ListMovieItem> = emptyList()

    private var defaultProviderTypes:
            Set<WatchMonetizationType> =
        setOf(WatchMonetizationType.FLATRATE)

    private var filterTypesCustomized = false

    private val _state =
        MutableStateFlow(ListScreenUiState())

    val state = _state.asStateFlow()

    init {
        observeMovies()
    }

    private fun observeMovies() {
        val providerSources = combine(
            observeWatchProviderCatalogUseCase(),
            observeSelectedWatchProviderIdsUseCase(),
            observeSelectedWatchProviderTypesUseCase(),
            observeCachedMovieProvidersUseCase(),
        ) {
                catalog,
                myProviderIds,
                selectedTypes,
                cachedProviders,
            ->

            ProviderSources(
                catalog = catalog,
                myProviderIds = myProviderIds,
                defaultTypes = selectedTypes.ifEmpty {
                    setOf(
                        WatchMonetizationType.FLATRATE
                    )
                },
                cachedProviders = cachedProviders,
            )
        }

        viewModelScope.launch {
            combine(
                observeWatchlistUseCase(),
                observeWatchedMoviesUseCase(),
                providerSources,
            ) {
                    watchlist,
                    watched,
                    providers,
                ->

                val providersByMovieId =
                    providers.cachedProviders.associateBy {
                            cached ->
                        cached.movieId
                    }

                MovieSources(
                    watchlist = watchlist.map { entry ->
                        val item =
                            entry.toListMovieItem()

                        item.withProviderCache(
                            providersByMovieId[item.id]
                        )
                    },
                    watched = watched.map { movie ->
                        val item =
                            movie.toListMovieItem()

                        item.withProviderCache(
                            providersByMovieId[item.id]
                        )
                    },
                    providers = providers,
                )
            }
                .catch { error ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = error.message
                                ?: "Nie udało się wczytać list filmów",
                        )
                    }
                }
                .collect { sources ->
                    watchlistMovies =
                        sources.watchlist

                    watchedMovies =
                        sources.watched

                    defaultProviderTypes =
                        sources.providers.defaultTypes

                    _state.update { currentState ->
                        val providerFilter = if (
                            filterTypesCustomized
                        ) {
                            currentState.providerFilter
                        } else {
                            currentState.providerFilter.copy(
                                monetizationTypes =
                                    defaultProviderTypes,
                            )
                        }

                        recalculateState(
                            currentState.copy(
                                availableProviders =
                                    sources.providers.catalog,

                                myProviderIds =
                                    sources.providers.myProviderIds,

                                providerFilter =
                                    providerFilter,

                                isLoading = false,
                                error = null,
                            )
                        )
                    }
                }
        }
    }


    private fun recalculateState(
        state: ListScreenUiState,
    ): ListScreenUiState {
        val sourceMovies =
            getMoviesForTab(state.selectedTab)

        val availableGenres = sourceMovies
            .flatMap { movie ->
                movie.genres
            }
            .distinctBy { genre ->
                genre.id
            }
            .sortedBy { genre ->
                genre.name.lowercase()
            }

        val availableGenreIds =
            availableGenres
                .mapTo(mutableSetOf()) { genre ->
                    genre.id
                }


        val validSelectedGenreIds =
            state.selectedGenreIds
                .intersect(availableGenreIds)

        val updatedState = state.copy(
            genres = availableGenres,
            selectedGenreIds =
                validSelectedGenreIds,
        )

        return updatedState.copy(
            moviesList =
                getVisibleMovies(updatedState),

            unsyncedMoviesCount =
                getUnsyncedMoviesCount(
                    tab = updatedState.selectedTab,
                ),
        )
    }

    private fun updateVisibleState(
        transform: (
            ListScreenUiState
        ) -> ListScreenUiState,
    ) {
        _state.update { currentState ->
            recalculateState(
                transform(currentState)
            )
        }
    }

    private fun getMoviesForTab(
        tab: MovieListTab,
    ): List<ListMovieItem> {
        return when (tab) {
            MovieListTab.WATCHLIST ->
                watchlistMovies

            MovieListTab.WATCHED ->
                watchedMovies
        }
    }

    fun clearFilters() {
        filterTypesCustomized = false

        updateVisibleState { currentState ->
            currentState.copy(
                selectedGenreIds =
                    emptySet(),

                providerFilter =
                    MovieProviderFilter(
                        monetizationTypes =
                            defaultProviderTypes,
                    ),
            )
        }
    }

    fun toggleGenre(
        genre: Genre,
    ) {
        updateVisibleState { currentState ->
            val updatedSelectedGenreIds =
                currentState
                    .selectedGenreIds
                    .toggle(genre.id)

            currentState.copy(
                selectedGenreIds =
                    updatedSelectedGenreIds,
            )
        }
    }

    fun toggleDisplayStyle() {
        _state.update { currentState ->
            currentState.copy(
                displayStyle = when (
                    currentState.displayStyle
                ) {
                    MovieDisplayStyle.GRID ->
                        MovieDisplayStyle.LIST

                    MovieDisplayStyle.LIST ->
                        MovieDisplayStyle.GRID
                },
            )
        }
    }

    fun switchTab(
        tab: MovieListTab,
    ) {
        updateVisibleState { currentState ->
            currentState.copy(
                selectedTab = tab,
            )
        }
    }

    fun switchSort(
        sortOption: MovieSortOption,
    ) {
        updateVisibleState { currentState ->
            currentState.copy(
                selectedSort = sortOption,
            )
        }
    }

    fun changeSearchQuery(
        query: String,
    ) {
        updateVisibleState { currentState ->
            currentState.copy(
                searchQuery = query,
            )
        }
    }


    fun setProviderFilterEnabled(
        enabled: Boolean,
    ) {
        updateVisibleState { currentState ->
            currentState.copy(
                providerFilter =
                    currentState.providerFilter.copy(
                        isEnabled = enabled,
                    ),
            )
        }
    }

    fun selectProviderScope(
        scope: ProviderFilterScope,
    ) {
        updateVisibleState { currentState ->
            currentState.copy(
                providerFilter =
                    currentState.providerFilter.copy(
                        scope = scope,
                    ),
            )
        }
    }

    fun toggleFilterProvider(
        providerId: Int,
    ) {
        updateVisibleState { currentState ->
            val selectedProviderIds =
                currentState
                    .providerFilter
                    .selectedProviderIds
                    .toggle(providerId)

            currentState.copy(
                providerFilter =
                    currentState.providerFilter.copy(
                        selectedProviderIds =
                            selectedProviderIds,
                    ),
            )
        }
    }

    fun toggleFilterMonetizationType(
        type: WatchMonetizationType,
    ) {
        filterTypesCustomized = true

        updateVisibleState { currentState ->
            val selectedTypes =
                currentState
                    .providerFilter
                    .monetizationTypes
                    .toggle(type)

            currentState.copy(
                providerFilter =
                    currentState.providerFilter.copy(
                        monetizationTypes =
                            selectedTypes,
                    ),
            )
        }
    }

    fun refreshProviders() {
        if (_state.value.isRefreshingProviders) {
            return
        }

        viewModelScope.launch {
            _state.update { currentState ->
                currentState.copy(
                    isRefreshingProviders = true,
                    providerMessage = null,
                )
            }

            refreshLibraryWatchProvidersUseCase(
                forceRefresh = true,
            )
                .onSuccess { result ->
                    val message = when {
                        result.totalMovies == 0 -> {
                            "Brak lokalnych filmów do odświeżenia."
                        }

                        result.failedMovieIds.isEmpty() -> {
                            "Zaktualizowano dostępność dla " +
                                    "${result.refreshedMovies} filmów."
                        }

                        else -> {
                            "Zaktualizowano " +
                                    "${result.refreshedMovies} z " +
                                    "${result.totalMovies} filmów."
                        }
                    }

                    _state.update { currentState ->
                        currentState.copy(
                            isRefreshingProviders =
                                false,

                            providerMessage =
                                message,
                        )
                    }
                }
                .onFailure { error ->
                    _state.update { currentState ->
                        currentState.copy(
                            isRefreshingProviders =
                                false,

                            providerMessage =
                                error.message
                                    ?: "Nie udało się odświeżyć dostępności.",
                        )
                    }
                }
        }
    }

    private fun getVisibleMovies(
        state: ListScreenUiState,
    ): List<ListMovieItem> {
        return getMoviesForTab(
            state.selectedTab
        )
            .filterByGenres(
                state.selectedGenreIds
            )
            .filterBySearch(
                state.searchQuery
            )
            .filterByProviders(
                providerFilter =
                    state.providerFilter,

                myProviderIds =
                    state.myProviderIds,
            )
            .sortByOption(
                state.selectedSort
            )
    }

    private fun getUnsyncedMoviesCount(
        tab: MovieListTab,
    ): Int {
        return getMoviesForTab(tab)
            .count { movie ->
                movie.providersSyncedAt == null
            }
    }
}

private data class MovieSources(
    val watchlist: List<ListMovieItem>,
    val watched: List<ListMovieItem>,
    val providers: ProviderSources,
)

private data class ProviderSources(
    val catalog: List<WatchProvider>,
    val myProviderIds: Set<Int>,
    val defaultTypes:
    Set<WatchMonetizationType>,
    val cachedProviders:
    List<CachedMovieWatchProviders>,
)

private fun ListMovieItem.withProviderCache(
    cached: CachedMovieWatchProviders?,
): ListMovieItem {
    return copy(
        providerOffers =
            cached?.offers.orEmpty(),

        providersSyncedAt =
            cached?.syncedAt,
    )
}

private fun List<ListMovieItem>.filterBySearch(
    query: String,
): List<ListMovieItem> {
    val normalizedQuery =
        query.trim()

    if (normalizedQuery.isBlank()) {
        return this
    }

    return filter { movie ->
        movie.title.contains(
            other = normalizedQuery,
            ignoreCase = true,
        ) || movie.genres.any { genre ->
            genre.name.contains(
                other = normalizedQuery,
                ignoreCase = true,
            )
        }
    }
}

private fun List<ListMovieItem>.filterByProviders(
    providerFilter: MovieProviderFilter,
    myProviderIds: Set<Int>,
): List<ListMovieItem> {
    if (!providerFilter.isEnabled) {
        return this
    }

    return filter { movie ->
        providerFilter.matches(
            offers = movie.providerOffers,
            myProviderIds = myProviderIds,
        )
    }
}

private fun List<ListMovieItem>.filterByGenres(
    selectedGenreIds: Set<Int>,
): List<ListMovieItem> {
    if (selectedGenreIds.isEmpty()) {
        return this
    }

    return filter { movie ->
        movie.genres.any { genre ->
            genre.id in selectedGenreIds
        }
    }
}

private fun List<ListMovieItem>.sortByOption(
    sort: MovieSortOption,
): List<ListMovieItem> {
    return when (sort) {
        MovieSortOption.RATING_DESC -> {
            sortedWith(
                compareByDescending<ListMovieItem> {
                        movie ->
                    movie.displayedRating ?: 0.0
                }.thenBy { movie ->
                    movie.title.lowercase()
                }
            )
        }

        MovieSortOption.NEWEST -> {
            sortedWith(
                compareByDescending<ListMovieItem> {
                        movie ->
                    movie.releaseDate?.toEpochDay()
                        ?: Long.MIN_VALUE
                }.thenBy { movie ->
                    movie.title.lowercase()
                }
            )
        }

        MovieSortOption.OLDEST -> {
            sortedWith(
                compareBy<ListMovieItem> { movie ->
                    movie.releaseDate?.toEpochDay()
                        ?: Long.MAX_VALUE
                }.thenBy { movie ->
                    movie.title.lowercase()
                }
            )
        }

        MovieSortOption.TITLE_ASC -> {
            sortedBy { movie ->
                movie.title.lowercase()
            }
        }

        MovieSortOption.ADDING_TIME -> {
            sortedWith(
                compareByDescending<ListMovieItem> {
                        movie ->
                    movie.activityTimeMillis()
                }.thenBy { movie ->
                    movie.title.lowercase()
                }
            )
        }
    }
}

private fun ListMovieItem.activityTimeMillis(): Long {
    return addedAt?.toEpochMilli()
        ?: watchedAt
            ?.atStartOfDay(ZoneOffset.UTC)
            ?.toInstant()
            ?.toEpochMilli()
        ?: Long.MIN_VALUE
}

private fun <T> Set<T>.toggle(
    value: T,
): Set<T> {
    return if (value in this) {
        this - value
    } else {
        this + value
    }
}