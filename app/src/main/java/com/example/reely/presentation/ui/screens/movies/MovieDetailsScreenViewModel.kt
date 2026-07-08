package com.example.reely.presentation.ui.screens.movies

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.reely.domain.model.Movie
import com.example.reely.domain.model.MovieCredits
import com.example.reely.domain.model.MovieWatchProviders
import com.example.reely.domain.model.User
import com.example.reely.domain.model.UserId
import com.example.reely.domain.model.UserMovieWatch
import com.example.reely.domain.model.WatchProvider
import com.example.reely.domain.usecase.AddMovieToWatchlistUseCase
import com.example.reely.domain.usecase.GetMovieCreditsUseCase
import com.example.reely.domain.usecase.GetMovieProvidersUseCase
import com.example.reely.domain.usecase.LoadMovieDataUseCase
import com.example.reely.domain.usecase.MarkMovieAsWatchedUseCase
import com.example.reely.domain.usecase.ObserveUsersUseCase
import com.example.reely.domain.usecase.RemoveMovieFromWatchlistUseCase
import com.example.reely.presentation.navigation.MovieDetailsRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class ProviderSectionType(
    val text: String
) {
    FLATRATE("na streamingu"),
    BUY("do kupienia"),
    RENT("do wypożyczenia"),
    FREE("za darmo"),
    ADS("z reklamami");

    fun providersFrom(movieProviders: MovieWatchProviders): List<WatchProvider> {
        return when (this) {
            FLATRATE -> movieProviders.flatrate
            BUY -> movieProviders.buy
            RENT -> movieProviders.rent
            FREE -> movieProviders.free
            ADS -> movieProviders.ads
        }
    }

    companion object {
        fun firstAvailable(movieProviders: MovieWatchProviders): ProviderSectionType {
            return entries.firstOrNull { type ->
                type.providersFrom(movieProviders).isNotEmpty()
            } ?: FLATRATE
        }
    }
}

data class MovieDetailsScreenState(
    val movie: Movie? = null,
    val movieCredits: MovieCredits? = null,
    val movieProviders: MovieWatchProviders? = null,

    val selectedProviderSection: ProviderSectionType = ProviderSectionType.FLATRATE,

    val isMovieCreditsLoading: Boolean = false,
    val isMovieProvidersLoading: Boolean = false,
    val isMovieLoading: Boolean = false,
    val isAddingToWatchlist: Boolean = false,

    val users: List<User> = emptyList(),
    val isRatingDialogVisible: Boolean = false,
    val isMarkingAsWatched: Boolean = false,

    val error: String? = null
) {
    val availableProviderSections: List<ProviderSectionType>
        get() = movieProviders?.let { providers ->
            ProviderSectionType.entries.filter { type ->
                type.providersFrom(providers).isNotEmpty()
            }
        }.orEmpty()

    val selectedProviders: List<WatchProvider>
        get() = movieProviders
            ?.let { selectedProviderSection.providersFrom(it) }
            .orEmpty()
}

@HiltViewModel
class MovieDetailsScreenViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val loadMovieDataUseCase: LoadMovieDataUseCase,
    private val getMovieCreditsUseCase: GetMovieCreditsUseCase,
    private val getMovieProvidersUseCase: GetMovieProvidersUseCase,
    private val addMovieToWatchlistUseCase: AddMovieToWatchlistUseCase,
    private val markMovieAsWatchedUseCase: MarkMovieAsWatchedUseCase,
    private val removeMovieFromWatchlistUseCase: RemoveMovieFromWatchlistUseCase,
    private val observeUsersUseCase: ObserveUsersUseCase
) : ViewModel() {

    private val route = savedStateHandle.toRoute<MovieDetailsRoute>()

    private val _state = MutableStateFlow(MovieDetailsScreenState())
    val state = _state.asStateFlow()

    init {
        val movieId = route.movieId

        observeUsers()
        loadMovieDetails(movieId)
        loadMovieProviders(movieId)
        loadMovieCredits(movieId)
    }

    private fun observeUsers() {
        viewModelScope.launch {
            observeUsersUseCase()
                .collect { users ->
                    _state.update {
                        it.copy(users = users)
                    }
                }
        }
    }

    fun loadMovieDetails(movieId: Int) {
        viewModelScope.launch {
            reloadMovieDetails(movieId)
        }
    }

    private suspend fun reloadMovieDetails(movieId: Int) {
        _state.update {
            it.copy(
                isMovieLoading = true,
                error = null
            )
        }

        loadMovieDataUseCase(movieId)
            .onSuccess { movie ->
                _state.update {
                    it.copy(
                        isMovieLoading = false,
                        movie = movie
                    )
                }
            }
            .onFailure { error ->
                Log.d("ERROR", error.message.toString())

                _state.update {
                    it.copy(
                        isMovieLoading = false,
                        error = "Błąd podczas ładowania filmu\n${error.message}\n${error.cause}"
                    )
                }
            }
    }

    fun loadMovieProviders(movieId: Int) {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    isMovieProvidersLoading = true,
                    error = null
                )
            }

            getMovieProvidersUseCase(movieId = movieId)
                .onSuccess { movieProviders ->
                    _state.update {
                        it.copy(
                            movieProviders = movieProviders,
                            selectedProviderSection = ProviderSectionType.firstAvailable(movieProviders),
                            isMovieProvidersLoading = false
                        )
                    }
                }
                .onFailure { error ->
                    _state.update {
                        it.copy(
                            isMovieProvidersLoading = false,
                            error = "Błąd podczas ładowania dostawców\n${error.message}\n${error.cause}"
                        )
                    }
                }
        }
    }

    fun loadMovieCredits(movieId: Int) {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    isMovieCreditsLoading = true,
                    error = null
                )
            }

            getMovieCreditsUseCase(movieId = movieId)
                .onSuccess { credits ->
                    _state.update {
                        it.copy(
                            movieCredits = credits,
                            isMovieCreditsLoading = false
                        )
                    }
                }
                .onFailure { error ->
                    _state.update {
                        it.copy(
                            isMovieCreditsLoading = false,
                            error = "Błąd podczas ładowania obsady\n${error.message}\n${error.cause}"
                        )
                    }
                }
        }
    }

    fun changeProvidersType(type: ProviderSectionType) {
        _state.update {
            it.copy(
                selectedProviderSection = type
            )
        }
    }

    fun addToWatchlist() {
        viewModelScope.launch {
            val movie = _state.value.movie ?: return@launch

            _state.update {
                it.copy(
                    isAddingToWatchlist = true,
                    error = null
                )
            }

            addMovieToWatchlistUseCase(movie)
                .onSuccess {
                    reloadMovieDetails(movie.id)
                }
                .onFailure { error ->
                    _state.update {
                        it.copy(
                            error = "Błąd podczas dodawania do watchlisty\n${error.message}"
                        )
                    }
                }

            _state.update {
                it.copy(
                    isAddingToWatchlist = false
                )
            }
        }
    }

    fun showRatingDialog() {
        _state.update {
            it.copy(
                isRatingDialogVisible = true,
                error = null
            )
        }
    }

    fun hideRatingDialog() {
        _state.update {
            it.copy(
                isRatingDialogVisible = false
            )
        }
    }

    fun markAsWatched(
        ratings: Map<UserId, Double?>
    ) {
        viewModelScope.launch {
            val movie = _state.value.movie ?: return@launch

            if (_state.value.users.isEmpty()) {
                _state.update {
                    it.copy(
                        error = "Brak użytkowników do ocenienia filmu."
                    )
                }
                return@launch
            }

            _state.update {
                it.copy(
                    isMarkingAsWatched = true,
                    error = null
                )
            }

            val watchedAt = LocalDate.now().toString()

            val userWatches = ratings.mapValues { entry ->
                UserMovieWatch(
                    watchedAt = watchedAt,
                    rating = entry.value
                )
            }

            markMovieAsWatchedUseCase(
                movie = movie,
                userWatches = userWatches
            ).onSuccess {
                _state.update {
                    it.copy(
                        isRatingDialogVisible = false,
                        isMarkingAsWatched = false
                    )
                }

                reloadMovieDetails(movie.id)
            }.onFailure { error ->
                _state.update {
                    it.copy(
                        isMarkingAsWatched = false,
                        error = "Błąd podczas oznaczania filmu jako obejrzany\n${error.message}"
                    )
                }
            }
        }
    }

    fun removeFromWatchlist() {
        viewModelScope.launch {
            val movie = _state.value.movie ?: return@launch

            removeMovieFromWatchlistUseCase(movie.id)
                .onSuccess {
                    reloadMovieDetails(movie.id)
                }
                .onFailure { error ->
                    _state.update {
                        it.copy(
                            error = "Błąd podczas usuwania z watchlisty\n${error.message}"
                        )
                    }
                }
        }
    }
}