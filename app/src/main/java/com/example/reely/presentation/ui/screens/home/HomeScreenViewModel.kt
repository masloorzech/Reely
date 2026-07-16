package com.example.reely.presentation.ui.screens.home
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.reely.domain.model.Profile
import com.example.reely.domain.model.RecentWatchedMovie
import com.example.reely.domain.model.WatchlistEntry
import com.example.reely.domain.usecase.movie.ObserveRecentWatchedMoviesUseCase
import com.example.reely.domain.usecase.movie.ObserveWatchlistUseCase
import com.example.reely.domain.usecase.user.ObserveProfilesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDate
import java.time.YearMonth

data class HomeScreenUiState(
    val profiles: List<Profile> = emptyList(),

    val recentSeenMovies: List<RecentWatchedMovie> = emptyList(),

    val watchlist: List<WatchlistEntry> = emptyList(),

    val calendarMonth: YearMonth = YearMonth.now(),

    val watchedMoviesByDate:
    Map<LocalDate, List<RecentWatchedMovie>> = emptyMap(),

    val isLoading: Boolean = true,
    val error: String? = null,
)

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    observeProfilesUseCase: ObserveProfilesUseCase,
    observeRecentWatchedMoviesUseCase: ObserveRecentWatchedMoviesUseCase,
    observeWatchlistUseCase: ObserveWatchlistUseCase,
) : ViewModel() {

    val state: StateFlow<HomeScreenUiState> =
        combine(
            observeProfilesUseCase(),
            observeRecentWatchedMoviesUseCase(limit = 10),
            observeRecentWatchedMoviesUseCase(limit = Int.MAX_VALUE),
            observeWatchlistUseCase()
        ) { profiles, recentMovies, viewingHistory, watchlist   ->

            val calendarMonth = YearMonth.now()

            HomeScreenUiState(
                profiles = profiles,
                recentSeenMovies = recentMovies,
                calendarMonth = calendarMonth,
                watchlist = watchlist,
                watchedMoviesByDate = viewingHistory
                    .filter { movie ->
                        YearMonth.from(movie.watchedAt) ==
                                calendarMonth
                    }
                    .groupBy { movie ->
                        movie.watchedAt
                    },
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
