package com.example.reely.presentation.ui.screens.movies

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.reely.domain.model.Movie
import com.example.reely.domain.model.MovieCredits
import com.example.reely.domain.model.MovieWatchProviders
import com.example.reely.domain.model.WatchProvider
import com.example.reely.domain.usecase.AddMovieToWatchlist
import com.example.reely.domain.usecase.GetMovieCreditsUseCase
import com.example.reely.domain.usecase.GetMovieProvidersUseCase
import com.example.reely.domain.usecase.LoadMovieDataUseCase
import com.example.reely.presentation.navigation.MovieDetailsRoute
import dagger.hilt.android.lifecycle.HiltViewModel
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
    private val addMovieToWatchlist: AddMovieToWatchlist
) : ViewModel() {

    private val route = savedStateHandle.toRoute<MovieDetailsRoute>()

    private val _state = MutableStateFlow(MovieDetailsScreenState())
    val state = _state.asStateFlow()

    init {
        val movieId = route.movieId

        loadMovieDetails(movieId)
        loadMovieProviders(movieId)
        loadMovieCredits(movieId)
    }

    fun loadMovieDetails(movieId: Int) {
        viewModelScope.launch {
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
            _state.update {
                it.copy(
                    isAddingToWatchlist = true
                )
            }

            val movie = _state.value.movie ?: return@launch

            addMovieToWatchlist(movie)
            loadMovieDetails(movie.id)


            _state.update {
                it.copy(
                    isAddingToWatchlist = false
                )
            }

        }
    }

    fun markAsWatched(){
        //TODO not implemented yet
    }

    fun removeFromWatchlist(){
        //TODO not impemented yet
    }
}