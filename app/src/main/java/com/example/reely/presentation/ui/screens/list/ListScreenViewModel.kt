package com.example.reely.presentation.ui.screens.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.reely.domain.model.Movie
import com.example.reely.domain.model.MovieUserState
import com.example.reely.domain.model.isInWatchlist
import com.example.reely.domain.model.ourAverageRatingRounded
import com.example.reely.domain.usecase.GetUserMoviesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ListScreenUiState(
    val selectedSort: MovieSortOption = MovieSortOption.RATING_DESC,
    val selectedTab: MovieListTab = MovieListTab.WATCHED,
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

        refreshVisibleMovies()
    }

    fun switchSort(sortType: MovieSortOption) {
        _state.update {
            it.copy(selectedSort = sortType)
        }

        refreshVisibleMovies()
    }

    fun changeSearchQuery(query: String) {
        _state.update {
            it.copy(searchQuery = query)
        }

        refreshVisibleMovies()
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
            movie.userStates.values.any { it is MovieUserState.Watched }
        }

        MovieListTab.WATCHLIST -> filter { movie ->
            movie.isInWatchlist()
        }

        MovieListTab.FAVOURITE -> filter {
            // Na razie nie masz Favourite w MovieUserState,
            // więc zostawiamy pustą listę.
            false
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
        MovieSortOption.RATING_DESC -> sortedByDescending { movie ->
            movie.ourAverageRatingRounded() ?: 0.0
        }

        MovieSortOption.NEWEST -> sortedByDescending { movie ->
            movie.latestWatchedAt() ?: movie.releaseDate.orEmpty()
        }

        MovieSortOption.OLDEST -> sortedBy { movie ->
            movie.latestWatchedAt() ?: movie.releaseDate.orEmpty()
        }

        MovieSortOption.TITLE_ASC -> sortedBy { movie ->
            movie.title.lowercase()
        }
    }
}

private fun Movie.latestWatchedAt(): String? {
    return userStates.values
        .filterIsInstance<MovieUserState.Watched>()
        .mapNotNull { it.watchedAt }
        .maxOrNull()
}