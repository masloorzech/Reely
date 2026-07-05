package com.example.reely.presentation.ui.screens.search

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.reely.domain.model.Movie
import com.example.reely.domain.usecase.GetPopularMoviesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SearchUiState(
    val movies: List<Movie> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class SearchScreenViewModel @Inject constructor(
    val getPopularMoviesUseCase: GetPopularMoviesUseCase
)  : ViewModel(){

    private var _state = MutableStateFlow(SearchUiState())
    val state = _state.asStateFlow()

    init {
        loadPopularMovies()
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



}