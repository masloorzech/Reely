package com.example.reely.presentation.ui.screens.list

import androidx.compose.runtime.collectAsState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.reely.domain.model.Genre
import com.example.reely.domain.usecase.movie.ObserveWatchedMoviesUseCase
import com.example.reely.domain.usecase.movie.ObserveWatchlistUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.ZoneOffset
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.collections.plus

data class ListScreenUiState(
    val selectedSort: MovieSortOption = MovieSortOption.RATING_DESC,
    val selectedTab: MovieListTab = MovieListTab.WATCHLIST,
    val displayStyle: MovieDisplayStyle = MovieDisplayStyle.LIST,
    val selectedGenreIds: Set<Int> = emptySet(),
    val genres: List<Genre> = emptyList(),
    val searchQuery: String = "",
    val moviesList: List<ListMovieItem> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null,
)

@HiltViewModel
class ListScreenViewModel @Inject constructor(
    private val observeWatchlistUseCase:
    ObserveWatchlistUseCase,
    private val observeWatchedMoviesUseCase: ObserveWatchedMoviesUseCase,
) : ViewModel() {

    private var watchlistMovies: List<ListMovieItem> = emptyList()

    private var watchedMovies: List<ListMovieItem> = emptyList()

    private val _state = MutableStateFlow(ListScreenUiState())

    val state = _state.asStateFlow()

    init {
        observeMovies()
    }


    private fun observeMovies() {
        viewModelScope.launch {
            combine(
                observeWatchlistUseCase(),
                observeWatchedMoviesUseCase(),
            ) { watchlist, watched ->
                MovieSources(
                    watchlist = watchlist.map { entry ->
                        entry.toListMovieItem()
                    },
                    watched = watched.map { movie ->
                        movie.toListMovieItem()
                    },
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
                    watchlistMovies = sources.watchlist
                    watchedMovies = sources.watched

                    _state.update { currentState ->
                        val sourceMovies = getMoviesForTab(
                            tab = currentState.selectedTab,
                        )

                        val availableGenres = sourceMovies
                            .flatMap { movie -> movie.genres }
                            .distinctBy { genre -> genre.id }
                            .sortedBy { genre -> genre.name }

                        val stateWithGenres = currentState.copy(
                            genres = availableGenres,
                        )

                        stateWithGenres.copy(
                            moviesList = getVisibleMovies(
                                state = stateWithGenres,
                            ),
                            isLoading = false,
                            error = null,
                        )
                    }
                }
        }
    }

    private fun getMoviesForTab(
        tab: MovieListTab,
    ): List<ListMovieItem> {
        return when (tab) {
            MovieListTab.WATCHLIST -> watchlistMovies
            MovieListTab.WATCHED -> watchedMovies
        }
    }

    fun clearFilters() {
        _state.update { currentState ->
            val updatedState = currentState.copy(
                selectedGenreIds = emptySet(),
            )

            updatedState.copy(
                moviesList = getVisibleMovies(updatedState),
            )
        }
    }

    fun toggleGenre(genre: Genre) {
        _state.update { currentState ->
            val updatedSelectedGenreIds =
                if (genre.id in currentState.selectedGenreIds) {
                    currentState.selectedGenreIds - genre.id
                } else {
                    currentState.selectedGenreIds + genre.id
                }

            val updatedState = currentState.copy(
                selectedGenreIds = updatedSelectedGenreIds,
            )

            updatedState.copy(
                moviesList = getVisibleMovies(updatedState),
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
        _state.update { currentState ->
            val updatedState = currentState.copy(
                selectedTab = tab,
            )

            updatedState.copy(
                moviesList = getVisibleMovies(
                    state = updatedState,
                ),
            )
        }
    }

    fun switchSort(
        sortOption: MovieSortOption,
    ) {
        _state.update { currentState ->
            val updatedState = currentState.copy(
                selectedSort = sortOption,
            )

            updatedState.copy(
                moviesList = getVisibleMovies(
                    state = updatedState,
                ),
            )
        }
    }

    fun changeSearchQuery(
        query: String,
    ) {
        _state.update { currentState ->
            val updatedState = currentState.copy(
                searchQuery = query,
            )

            updatedState.copy(
                moviesList = getVisibleMovies(
                    state = updatedState,
                ),
            )
        }
    }

    private fun getVisibleMovies(
        state: ListScreenUiState,
    ): List<ListMovieItem> {
        return getMoviesForTab(state.selectedTab)
            .filterByGenres(state.selectedGenreIds)
            .filterBySearch(state.searchQuery)
            .sortByOption(state.selectedSort)
    }
}

private data class MovieSources(
    val watchlist: List<ListMovieItem>,
    val watched: List<ListMovieItem>,
)

private fun List<ListMovieItem>.filterBySearch(
    query: String,
): List<ListMovieItem> {
    val normalizedQuery = query.trim()

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