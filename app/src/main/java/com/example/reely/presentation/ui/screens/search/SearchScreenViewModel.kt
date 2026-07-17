package com.example.reely.presentation.ui.screens.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.reely.domain.model.MoviePreview
import com.example.reely.domain.usecase.movie.ObservePopularMoviesUseCase
import com.example.reely.domain.usecase.movie.ObserveRecentSearchesUseCase
import com.example.reely.domain.usecase.movie.RefreshPopularMoviesUseCase
import com.example.reely.domain.usecase.movie.SearchMovieUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class SearchSection(
    val title: String,
) {
    POPULAR("Popularne teraz"),
    RESULTS("Wyniki wyszukiwania"),
}

data class SearchUiState(
    val movies: List<MoviePreview> = emptyList(),
    val searchQuery: String = "",
    val section: SearchSection = SearchSection.POPULAR,
    val isLoading: Boolean = true,
    val error: String? = null,
)

sealed interface SearchEvent {

    data class NavigateToMovieDetails(
        val movieId: Int,
    ) : SearchEvent
}

@HiltViewModel
class SearchScreenViewModel @Inject constructor(
    private val observePopularMoviesUseCase:
    ObservePopularMoviesUseCase,
    private val refreshPopularMoviesUseCase: RefreshPopularMoviesUseCase,
    private val searchMovieUseCase: SearchMovieUseCase,
    observeRecentSearchesUseCase: ObserveRecentSearchesUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(SearchUiState())
    val state = _state.asStateFlow()

    private val _events = Channel<SearchEvent>(
        capacity = Channel.BUFFERED,
    )
    val events = _events.receiveAsFlow()

    val recentSearches = observeRecentSearchesUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList(),
        )

    private var cachedPopularMovies: List<MoviePreview> =
        emptyList()

    init {
        observePopularMovies()
        refreshPopularMovies()
    }

    private fun observePopularMovies() {
        viewModelScope.launch {
            observePopularMoviesUseCase()
                .collect { movies ->
                    cachedPopularMovies = movies

                    if (_state.value.section == SearchSection.POPULAR) {
                        _state.update {
                            it.copy(
                                movies = movies,
                                isLoading = false,
                            )
                        }
                    }
                }
        }
    }

    fun refreshPopularMovies(
        forceRefresh: Boolean = false,
    ) {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    isLoading = cachedPopularMovies.isEmpty(),
                    error = null,
                    section = SearchSection.POPULAR,
                    movies = cachedPopularMovies,
                )
            }

            refreshPopularMoviesUseCase(
                forceRefresh = forceRefresh,
            ).onSuccess {
                _state.update {
                    it.copy(isLoading = false)
                }
            }.onFailure { error ->
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = error.message
                            ?: "Nie udało się pobrać popularnych filmów",
                    )
                }
            }
        }
    }

    fun onQueryChange(
        value: String,
    ) {
        _state.update {
            it.copy(searchQuery = value)
        }

        if (value.isBlank()) {
            showPopularMovies()
        }
    }

    fun onRecentSearchClick(
        query: String,
    ) {
        _state.update {
            it.copy(searchQuery = query)
        }

        search()
    }

    fun search() {
        val query = _state.value.searchQuery.trim()

        if (query.isBlank()) {
            showPopularMovies()
            return
        }

        viewModelScope.launch {
            _state.update {
                it.copy(
                    isLoading = true,
                    error = null,
                    section = SearchSection.RESULTS,
                )
            }

            searchMovieUseCase(query)
                .onSuccess { movies ->
                    _state.update {
                        it.copy(
                            movies = movies,
                            isLoading = false,
                        )
                    }
                }
                .onFailure { error ->
                    _state.update {
                        it.copy(
                            movies = emptyList(),
                            isLoading = false,
                            error = error.message
                                ?: "Nie udało się wyszukać filmów",
                        )
                    }
                }
        }
    }

    private fun showPopularMovies() {
        _state.update {
            it.copy(
                movies = cachedPopularMovies,
                section = SearchSection.POPULAR,
                isLoading = cachedPopularMovies.isEmpty(),
                error = null,
            )
        }
    }

    fun onMovieSelected(
        movie: MoviePreview,
    ) {
        _events.trySend(
            SearchEvent.NavigateToMovieDetails(
                movieId = movie.id,
            )
        )
    }
}