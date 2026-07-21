package com.example.reely.presentation.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.reely.domain.model.Profile
import com.example.reely.domain.model.RecentWatchedMovie
import com.example.reely.domain.model.WatchlistEntry
import com.example.reely.domain.usecase.movie.GetRandomWatchlistMovieUseCase
import com.example.reely.domain.usecase.movie.ObserveRecentWatchedMoviesUseCase
import com.example.reely.domain.usecase.movie.ObserveWatchlistUseCase
import com.example.reely.domain.usecase.user.ObserveProfilesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import javax.inject.Inject

data class HomeScreenUiState(
    val profiles: List<Profile> = emptyList(),

    val recentSeenMovies:
    List<RecentWatchedMovie> = emptyList(),

    val watchlist:
    List<WatchlistEntry> = emptyList(),

    val calendarMonth:
    YearMonth = YearMonth.now(),

    val watchedMoviesByDate:
    Map<LocalDate, List<RecentWatchedMovie>> =
        emptyMap(),

    val randomMovie:
    WatchlistEntry? = null,

    val isRandomizingMovie:
    Boolean = false,

    val randomMovieError:
    String? = null,

    val isLoading:
    Boolean = true,

    val error:
    String? = null,
)

private data class RandomMovieUiState(
    val movie: WatchlistEntry? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
)

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    observeProfilesUseCase:
    ObserveProfilesUseCase,

    observeRecentWatchedMoviesUseCase:
    ObserveRecentWatchedMoviesUseCase,

    observeWatchlistUseCase:
    ObserveWatchlistUseCase,

    private val getRandomWatchlistMovieUseCase:
    GetRandomWatchlistMovieUseCase,
) : ViewModel() {

    private val randomMovieState =
        MutableStateFlow(RandomMovieUiState())

    val state: StateFlow<HomeScreenUiState> =
        combine(
            observeProfilesUseCase(),
            observeRecentWatchedMoviesUseCase(
                limit = 10,
            ),
            observeRecentWatchedMoviesUseCase(
                limit = Int.MAX_VALUE,
            ),
            observeWatchlistUseCase(),
            randomMovieState,
        ) {
                profiles,
                recentMovies,
                viewingHistory,
                watchlist,
                randomState,
            ->

            val calendarMonth =
                YearMonth.now()

            HomeScreenUiState(
                profiles = profiles,
                recentSeenMovies = recentMovies,
                calendarMonth = calendarMonth,
                watchlist = watchlist,
                watchedMoviesByDate =
                    viewingHistory
                        .filter { movie ->
                            YearMonth.from(
                                movie.watchedAt
                            ) == calendarMonth
                        }
                        .groupBy { movie ->
                            movie.watchedAt
                        },

                randomMovie =
                    randomState.movie,

                isRandomizingMovie =
                    randomState.isLoading,

                randomMovieError =
                    randomState.error,

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
                started =
                    SharingStarted
                        .WhileSubscribed(5_000),
                initialValue =
                    HomeScreenUiState(),
            )

    fun randomizeMovie() {
        if (randomMovieState.value.isLoading) {
            return
        }

        val previousMovieId =
            randomMovieState.value
                .movie
                ?.movie
                ?.id

        viewModelScope.launch {
            randomMovieState.update {
                it.copy(
                    isLoading = true,
                    error = null,
                )
            }

            getRandomWatchlistMovieUseCase(
                previousMovieId =
                    previousMovieId,
            )
                .onSuccess { movie ->
                    randomMovieState.update {
                        it.copy(
                            movie = movie,
                            isLoading = false,
                            error = if (movie == null) {
                                "Brak filmów pasujących do Waszych serwisów."
                            } else {
                                null
                            },
                        )
                    }
                }
                .onFailure { error ->
                    randomMovieState.update {
                        it.copy(
                            isLoading = false,
                            error = error.message
                                ?: "Nie udało się wylosować filmu.",
                        )
                    }
                }
        }
    }
}