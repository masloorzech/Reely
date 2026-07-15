package com.example.reely.presentation.ui.screens.home
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.reely.domain.model.Profile
import com.example.reely.domain.model.RecentWatchedMovie
import com.example.reely.domain.usecase.movie.ObserveRecentWatchedMoviesUseCase
import com.example.reely.domain.usecase.user.ObserveProfilesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class HomeScreenUiState(
    val profiles: List<Profile> = emptyList(),
    val recentSeenMovies: List<RecentWatchedMovie> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null,
)

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    observeProfilesUseCase: ObserveProfilesUseCase,
    observeRecentWatchedMoviesUseCase: ObserveRecentWatchedMoviesUseCase,
) : ViewModel() {

    val state: StateFlow<HomeScreenUiState> =
        combine(
            observeProfilesUseCase(),
            observeRecentWatchedMoviesUseCase(limit = 10),
        ) { profiles, recentMovies ->
            HomeScreenUiState(
                profiles = profiles,
                recentSeenMovies = recentMovies,
                isLoading = false,
            )
        }
            .catch { exception ->
                emit(
                    HomeScreenUiState(
                        isLoading = false,
                        error = exception.message
                            ?: "Nie udało się wczytać ekranu głównego",
                    )
                )
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = HomeScreenUiState(),
            )
}
