package com.example.reely.presentation.ui.screens.search

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.reely.domain.model.Movie
import com.example.reely.domain.usecase.GetPopularMoviesUseCase
import com.example.reely.domain.usecase.SaveMovieDataUseCase
import com.example.reely.domain.usecase.SearchMovieUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SearchUiState(
    val movies: List<Movie> = emptyList(),
    val searchQuery: String = "",
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
    private val saveMovieData: SaveMovieDataUseCase
)  : ViewModel(){

    private var _state = MutableStateFlow(SearchUiState())
    val state = _state.asStateFlow()

    private val _events = Channel<SearchEvent>()
    val events = _events.receiveAsFlow()


    init {
        loadPopularMovies()
    }


    fun search(){

        viewModelScope.launch {

            val lastSavedQuery = _state.value.searchQuery

            _state.update {
                it.copy(
                    isLoading = true
                )
            }

            searchMovieUseCase(lastSavedQuery)
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

    fun onQueryChange(value: String){
        _state.update {
            it.copy(
                searchQuery = value
            )
        }
    }

    fun loadPopularMovies(){

        viewModelScope.launch {
            _state.update {
                it.copy(
                    isLoading = true
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