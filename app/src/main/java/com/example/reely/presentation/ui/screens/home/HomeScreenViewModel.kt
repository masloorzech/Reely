package com.example.reely.presentation.ui.screens.home.components

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.reely.domain.model.Movie
import com.example.reely.domain.model.StatsOverview
import com.example.reely.domain.model.User
import com.example.reely.domain.usecase.movie.GetRecentWatchedMoviesUseCase
import com.example.reely.domain.usecase.statistics.ObserveStatsUseCase
import com.example.reely.domain.usecase.user.GetAllUsersUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.internal.userAgent

data class HomeScreenUIState(

    val users: List<User> = emptyList(),
    val recentSeenMovies: List<Movie> = emptyList(),
    val error: String? = null

)

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val getAllUsersUseCase: GetAllUsersUseCase,
    private val getRecentWatchedMoviesUseCase: GetRecentWatchedMoviesUseCase,
    observeStatsUseCase: ObserveStatsUseCase

) : ViewModel(){

    val stats = observeStatsUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = StatsOverview()
        )

    private val _state = MutableStateFlow(HomeScreenUIState())

    val state = _state.asStateFlow()

    init {
        loadInitialData()
    }

    fun loadInitialData(){
        viewModelScope.launch {
            getAllUsersUseCase().onSuccess { users ->
                _state.update {
                    it.copy(
                        users = users
                    )
                }

                val mainUser = users.firstOrNull() ?: return@launch

                getRecentWatchedMoviesUseCase(userId = mainUser.id).onSuccess {movies ->

                    _state.update {
                        it.copy(
                            recentSeenMovies = movies
                        )
                    }

                }
                    .onFailure { error ->
                        _state.update {
                            it.copy(
                                error = error.message
                            )
                        }
                        return@launch
                    }


            }.onFailure { error ->
                _state.update {
                    it.copy(
                        error = error.message
                    )
                }
                return@launch
            }


        }
    }
}
