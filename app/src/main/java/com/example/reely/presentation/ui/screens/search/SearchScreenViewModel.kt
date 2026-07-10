package com.example.reely.presentation.ui.screens.search

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.reely.domain.model.Movie
import com.example.reely.domain.usecase.movie.GetPopularMoviesUseCase
import com.example.reely.domain.usecase.movie.ObserveRecentSearchesUseCase
import com.example.reely.domain.usecase.movie.SaveMovieDataUseCase
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


private enum class SearchSectionNameDefaults(
    val sectionName: String
){
    POPULAR_NOW("Popularne teraz"),
    SEARCH_RESULT("Wyniki wyszukiwania");

}
data class SearchUiState(
    val movies: List<Movie> = emptyList(),
    val searchQuery: String = "",
    val searchSectionName : String = SearchSectionNameDefaults.POPULAR_NOW.sectionName,
    val isLoading: Boolean = false,
    val error: String? = null
)

sealed interface SearchEvent {
    data class NavigateToMovieDetails(val movieId: Int) : SearchEvent
}

@HiltViewModel
class SearchScreenViewModel @Inject constructor(
    private val getPopularMoviesUseCase: GetPopularMoviesUseCase,
    private val searchMovieUseCase: SearchMovieUseCase,
    private val saveMovieData: SaveMovieDataUseCase,
    private val observeRecentSearchesUseCase: ObserveRecentSearchesUseCase
)  : ViewModel(){

    val recentSearches = observeRecentSearchesUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    private var _state = MutableStateFlow(SearchUiState())
    val state = _state.asStateFlow()

    private val _events = Channel<SearchEvent>()
    val events = _events.receiveAsFlow()


    init {
        loadPopularMovies()
    }


    fun search() {
        val query = _state.value.searchQuery.trim()

        if (query.isBlank()) {
            loadPopularMovies()
            return
        }

        viewModelScope.launch {
            _state.update {
                it.copy(
                    isLoading = true,
                    error = null,
                    searchSectionName = SearchSectionNameDefaults.SEARCH_RESULT.sectionName
                )
            }

            searchMovieUseCase(query)
                .onSuccess { movies ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            movies = movies
                        )
                    }
                }
                .onFailure { error ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = error.message
                        )
                    }
                }
        }
    }

    fun onQueryChange(value: String) {
        _state.update {
            it.copy(
                searchQuery = value
            )
        }

        if (value.isBlank()) {
            loadPopularMovies()
        }
    }

    fun loadPopularMovies() {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    isLoading = true,
                    error = null,
                    searchSectionName = SearchSectionNameDefaults.POPULAR_NOW.sectionName
                )
            }

            getPopularMoviesUseCase()
                .onSuccess { movies ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            movies = movies
                        )
                    }
                }
                .onFailure { error ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = error.message
                        )
                    }
                    error.message?.let { Log.d("Error", it) }
                }
        }
    }

    fun onMovieSelected(movie: Movie) {
        viewModelScope.launch {
            saveMovieData(movie)
                .onSuccess {
                    _events.send(
                        SearchEvent.NavigateToMovieDetails(movie.id)
                    )
                }
                .onFailure { error ->
                    _state.update {
                        it.copy(error = error.message)
                    }
                }
        }
    }



}