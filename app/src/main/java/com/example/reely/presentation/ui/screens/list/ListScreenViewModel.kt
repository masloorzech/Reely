package com.example.reely.presentation.ui.screens.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.reely.domain.model.Movie
import com.example.reely.domain.model.isWatched
import com.example.reely.domain.model.latestWatchedAt
import com.example.reely.domain.model.ourAverageRatingRounded
import com.example.reely.domain.usecase.movie.GetUserMoviesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ListScreenUiState(
    val selectedSort: MovieSortOption = MovieSortOption.RATING_DESC,
    val selectedTab: MovieListTab = MovieListTab.WATCHLIST,
    val displayStyle: MovieDisplayStyle = MovieDisplayStyle.LIST,
    val searchQuery: String = "",
    val moviesList: List<Movie> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class ListScreenViewModel @Inject constructor(
    private val getUserMoviesUseCase: GetUserMoviesUseCase
) : ViewModel() {

    private var allMovies: List<Movie> = emptyList()

    private val _state = MutableStateFlow(ListScreenUiState())
    val state = _state.asStateFlow()

    init {
        loadMovies()
    }

    fun loadMovies() {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    isLoading = true,
                    error = null
                )
            }

            getUserMoviesUseCase()
                .onSuccess { movies ->
                    allMovies = movies

                    _state.update { currentState ->
                        currentState.copy(
                            moviesList = movies
                                .filterByTab(currentState.selectedTab)
                                .filterBySearch(currentState.searchQuery)
                                .sortByOption(currentState.selectedSort),
                            isLoading = false,
                            error = null
                        )
                    }
                }
                .onFailure { error ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = "Błąd podczas ładowania listy filmów\n${error.message}\n${error.cause}"
                        )
                    }
                }
        }
    }

    fun refresh() {
        loadMovies()
        refreshVisibleMovies()
    }

    fun toggleDisplayStyle() {
        _state.update {
            it.copy(
                displayStyle = when (it.displayStyle) {
                    MovieDisplayStyle.GRID -> MovieDisplayStyle.LIST
                    MovieDisplayStyle.LIST -> MovieDisplayStyle.GRID
                }
            )
        }
    }

    fun switchTab(tab: MovieListTab) {
        _state.update {
            it.copy(selectedTab = tab)
        }

        refresh()
    }

    fun switchSort(sortType: MovieSortOption) {
        _state.update {
            it.copy(selectedSort = sortType)
        }

        refresh()
    }

    fun changeSearchQuery(query: String) {
        _state.update {
            it.copy(searchQuery = query)
        }

        refresh()
    }

    private fun refreshVisibleMovies() {
        _state.update { currentState ->
            currentState.copy(
                moviesList = allMovies
                    .filterByTab(currentState.selectedTab)
                    .filterBySearch(currentState.searchQuery)
                    .sortByOption(currentState.selectedSort)
            )
        }
    }
}

private fun List<Movie>.filterByTab(tab: MovieListTab): List<Movie> {
    return when (tab) {
        MovieListTab.WATCHED -> filter { movie ->
            movie.isWatched()
        }

        MovieListTab.WATCHLIST -> filter { movie ->
            movie.isInWatchlist && !movie.isWatched()
        }

    }
}

private fun List<Movie>.filterBySearch(query: String): List<Movie> {
    val normalizedQuery = query.trim()

    if (normalizedQuery.isBlank()) return this

    return filter { movie ->
        movie.title.contains(normalizedQuery, ignoreCase = true)
    }
}

private fun List<Movie>.sortByOption(sort: MovieSortOption): List<Movie> {
    return when (sort) {
        MovieSortOption.RATING_DESC -> sortedWith(
            compareByDescending<Movie> { movie ->
                movie.ourAverageRatingRounded() ?: movie.publicRating?.average ?: 0.0
            }.thenBy { movie ->
                movie.title.lowercase()
            }
        )

        MovieSortOption.NEWEST -> sortedWith(
            compareByDescending<Movie> { movie ->
                movie.latestWatchedAt() ?: movie.releaseDate.orEmpty()
            }.thenBy { movie ->
                movie.title.lowercase()
            }
        )

        MovieSortOption.OLDEST -> sortedWith(
            compareBy<Movie> { movie ->
                movie.latestWatchedAt() ?: movie.releaseDate.orEmpty()
            }.thenBy { movie ->
                movie.title.lowercase()
            }
        )

        MovieSortOption.TITLE_ASC -> sortedBy { movie ->
            movie.title.lowercase()
        }

        MovieSortOption.ADDING_TIME -> this
    }
}