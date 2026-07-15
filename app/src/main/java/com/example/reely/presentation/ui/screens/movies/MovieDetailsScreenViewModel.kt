package com.example.reely.presentation.ui.screens.movies

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.reely.domain.model.MovieCredits
import com.example.reely.domain.model.MovieDetails
import com.example.reely.domain.model.MovieRating
import com.example.reely.domain.model.MovieViewing
import com.example.reely.domain.model.MovieWatchProviders
import com.example.reely.domain.model.Profile
import com.example.reely.domain.model.ProfilePosition
import com.example.reely.domain.model.Rating
import com.example.reely.domain.model.WatchProvider
import com.example.reely.domain.usecase.movie.AddMovieToWatchlistUseCase
import com.example.reely.domain.usecase.movie.GetMovieCreditsUseCase
import com.example.reely.domain.usecase.movie.GetMovieDetailsUseCase
import com.example.reely.domain.usecase.movie.GetMovieProvidersUseCase
import com.example.reely.domain.usecase.movie.IsMovieOnWatchlistUseCase
import com.example.reely.domain.usecase.movie.MarkMovieAsWatchedUseCase
import com.example.reely.domain.usecase.movie.ObserveMovieRatingsUseCase
import com.example.reely.domain.usecase.movie.ObserveMovieViewingsUseCase
import com.example.reely.domain.usecase.movie.RemoveMovieFromWatchedUseCase
import com.example.reely.domain.usecase.movie.RemoveMovieFromWatchlistUseCase
import com.example.reely.domain.usecase.movie.UpdateMovieRatingsUseCase
import com.example.reely.domain.usecase.user.ObserveProfilesUseCase
import com.example.reely.presentation.navigation.MovieDetailsRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDate
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class ProviderSectionType(
    val text: String,
) {
    FLATRATE("na streamingu"),
    BUY("do kupienia"),
    RENT("do wypożyczenia"),
    FREE("za darmo"),
    ADS("z reklamami");

    fun providersFrom(
        providers: MovieWatchProviders,
    ): List<WatchProvider> {
        return when (this) {
            FLATRATE -> providers.flatrate
            BUY -> providers.buy
            RENT -> providers.rent
            FREE -> providers.free
            ADS -> providers.ads
        }
    }

    companion object {

        fun firstAvailable(
            providers: MovieWatchProviders,
        ): ProviderSectionType {
            return entries.firstOrNull { type ->
                type.providersFrom(providers).isNotEmpty()
            } ?: FLATRATE
        }
    }
}

enum class RatingDialogMode {
    RECORD_VIEWING,
    EDIT_RATINGS,
}

data class MovieDetailsScreenState(
    val movie: MovieDetails? = null,
    val movieCredits: MovieCredits? = null,
    val movieProviders: MovieWatchProviders? = null,

    val profiles: List<Profile> = emptyList(),


    val viewings: List<MovieViewing> = emptyList(),

    val ratings: List<MovieRating> = emptyList(),

    val isOnWatchlist: Boolean = false,

    val selectedProviderSection: ProviderSectionType = ProviderSectionType.FLATRATE,

    val ratingDialogMode: RatingDialogMode? = null,

    val isMovieLoading: Boolean = true,
    val isMovieCreditsLoading: Boolean = false,
    val isMovieProvidersLoading: Boolean = false,

    val isAddingToWatchlist: Boolean = false,
    val isRemovingFromWatchlist: Boolean = false,
    val isRemovingFromWatched: Boolean = false,
    val isSavingRatings: Boolean = false,

    val error: String? = null,
) {

    val isWatched: Boolean
        get() {
            if (profiles.size != 2) {
                return false
            }

            return profiles.all { profile ->
                ratings.any { rating ->
                    rating.profileId == profile.id
                }
            }
        }

    val isRatingDialogVisible: Boolean
        get() = ratingDialogMode != null

    val latestWatchedAt: LocalDate?
        get() = viewings
            .maxWithOrNull(
                compareBy<MovieViewing> { viewing ->
                    viewing.watchedAt
                }.thenBy { viewing ->
                    viewing.id
                }
            )
            ?.watchedAt

    val averageUserRating: Double?
        get() = ratings
            .map { movieRating ->
                movieRating.rating.value
            }
            .takeIf { values ->
                values.isNotEmpty()
            }
            ?.average()

    fun ratingFor(
        position: ProfilePosition,
    ): Rating? {
        val profileId = profiles
            .firstOrNull { profile ->
                profile.position == position
            }
            ?.id
            ?: return null

        return ratings
            .firstOrNull { movieRating ->
                movieRating.profileId == profileId
            }
            ?.rating
    }

    val availableProviderSections: List<ProviderSectionType>
        get() = movieProviders
            ?.let { providers ->
                ProviderSectionType.entries.filter { type ->
                    type.providersFrom(providers).isNotEmpty()
                }
            }
            .orEmpty()

    val selectedProviders: List<WatchProvider>
        get() = movieProviders
            ?.let(selectedProviderSection::providersFrom)
            .orEmpty()
}

@HiltViewModel
class MovieDetailsScreenViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getMovieDetailsUseCase: GetMovieDetailsUseCase,
    private val getMovieCreditsUseCase: GetMovieCreditsUseCase,
    private val getMovieProvidersUseCase: GetMovieProvidersUseCase,
    private val observeProfilesUseCase: ObserveProfilesUseCase,
    private val observeMovieViewingsUseCase: ObserveMovieViewingsUseCase,
    private val observeMovieRatingsUseCase: ObserveMovieRatingsUseCase,
    private val isMovieOnWatchlistUseCase: IsMovieOnWatchlistUseCase,
    private val addMovieToWatchlistUseCase: AddMovieToWatchlistUseCase,
    private val removeMovieFromWatchlistUseCase: RemoveMovieFromWatchlistUseCase,
    private val removeMovieFromWatchedUseCase: RemoveMovieFromWatchedUseCase,
    private val markMovieAsWatchedUseCase:
    MarkMovieAsWatchedUseCase,
    private val updateMovieRatingsUseCase:
    UpdateMovieRatingsUseCase,
) : ViewModel() {

    private val route =
        savedStateHandle.toRoute<MovieDetailsRoute>()

    private val movieId = route.movieId

    private val _state =
        MutableStateFlow(MovieDetailsScreenState())

    val state = _state.asStateFlow()

    init {
        observeProfiles()
        observeViewings()
        observeRatings()

        loadMovieDetails()
        loadMovieProviders()
        loadMovieCredits()
        loadWatchlistStatus()
    }

    private fun observeProfiles() {
        viewModelScope.launch {
            observeProfilesUseCase()
                .collect { profiles ->
                    _state.update {
                        it.copy(profiles = profiles)
                    }
                }
        }
    }

    private fun observeViewings() {
        viewModelScope.launch {
            observeMovieViewingsUseCase(movieId)
                .collect { viewings ->
                    _state.update {
                        it.copy(viewings = viewings)
                    }
                }
        }
    }

    private fun observeRatings() {
        viewModelScope.launch {
            observeMovieRatingsUseCase(movieId)
                .collect { ratings ->
                    _state.update {
                        it.copy(ratings = ratings)
                    }
                }
        }
    }

    fun loadMovieDetails() {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    isMovieLoading = true,
                    error = null,
                )
            }

            getMovieDetailsUseCase(movieId)
                .onSuccess { movie ->
                    _state.update {
                        it.copy(
                            movie = movie,
                            isMovieLoading = false,
                        )
                    }
                }
                .onFailure { error ->
                    _state.update {
                        it.copy(
                            isMovieLoading = false,
                            error = error.message
                                ?: "Nie udało się pobrać filmu",
                        )
                    }
                }
        }
    }

    private fun loadWatchlistStatus() {
        viewModelScope.launch {
            runCatching {
                isMovieOnWatchlistUseCase(movieId)
            }
                .onSuccess { isOnWatchlist ->
                    _state.update {
                        it.copy(
                            isOnWatchlist = isOnWatchlist,
                        )
                    }
                }
                .onFailure { error ->
                    _state.update {
                        it.copy(
                            error = error.message
                                ?: "Nie udało się sprawdzić watchlisty",
                        )
                    }
                }
        }
    }

    private fun loadMovieProviders() {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    isMovieProvidersLoading = true,
                )
            }

            getMovieProvidersUseCase(movieId)
                .onSuccess { providers ->
                    _state.update {
                        it.copy(
                            movieProviders = providers,
                            selectedProviderSection =
                                ProviderSectionType
                                    .firstAvailable(providers),
                            isMovieProvidersLoading = false,
                        )
                    }
                }
                .onFailure {
                    _state.update {
                        it.copy(
                            isMovieProvidersLoading = false,
                        )
                    }
                }
        }
    }

    private fun loadMovieCredits() {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    isMovieCreditsLoading = true,
                )
            }

            getMovieCreditsUseCase(movieId)
                .onSuccess { credits ->
                    _state.update {
                        it.copy(
                            movieCredits = credits,
                            isMovieCreditsLoading = false,
                        )
                    }
                }
                .onFailure {
                    _state.update {
                        it.copy(
                            isMovieCreditsLoading = false,
                        )
                    }
                }
        }
    }

    fun changeProvidersType(
        type: ProviderSectionType,
    ) {
        _state.update {
            it.copy(
                selectedProviderSection = type,
            )
        }
    }

    fun addToWatchlist() {
        val movie = _state.value.movie ?: return

        viewModelScope.launch {
            _state.update {
                it.copy(
                    isAddingToWatchlist = true,
                    error = null,
                )
            }

            addMovieToWatchlistUseCase(movie)
                .onSuccess {
                    _state.update {
                        it.copy(
                            isOnWatchlist = true,
                            isAddingToWatchlist = false,
                        )
                    }
                }
                .onFailure { error ->
                    _state.update {
                        it.copy(
                            isAddingToWatchlist = false,
                            error = error.message
                                ?: "Nie udało się dodać filmu do listy",
                        )
                    }
                }
        }
    }

    fun removeFromWatchlist() {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    isRemovingFromWatchlist = true,
                    error = null,
                )
            }

            removeMovieFromWatchlistUseCase(movieId)
                .onSuccess {
                    _state.update {
                        it.copy(
                            isOnWatchlist = false,
                            isRemovingFromWatchlist = false,
                        )
                    }
                }
                .onFailure { error ->
                    _state.update {
                        it.copy(
                            isRemovingFromWatchlist = false,
                            error = error.message
                                ?: "Nie udało się usunąć filmu z listy",
                        )
                    }
                }
        }
    }

    fun showRecordViewingDialog() {
        val currentState = _state.value

        when {
            currentState.isWatched -> {
                _state.update {
                    it.copy(
                        error = "Film jest już oznaczony jako obejrzany.",
                    )
                }
            }

            !currentState.isOnWatchlist -> {
                _state.update {
                    it.copy(
                        error = "Najpierw dodaj film do watchlisty.",
                    )
                }
            }

            currentState.profiles.size != 2 -> {
                _state.update {
                    it.copy(
                        error = "Brakuje dwóch skonfigurowanych profili.",
                    )
                }
            }

            else -> {
                _state.update {
                    it.copy(
                        ratingDialogMode =
                            RatingDialogMode.RECORD_VIEWING,
                        error = null,
                    )
                }
            }
        }
    }

    fun showEditRatingsDialog() {
        if (!_state.value.isWatched) {
            return
        }

        _state.update {
            it.copy(
                ratingDialogMode =
                    RatingDialogMode.EDIT_RATINGS,
                error = null,
            )
        }
    }

    fun hideRatingDialog() {
        if (_state.value.isSavingRatings) {
            return
        }

        _state.update {
            it.copy(
                ratingDialogMode = null,
            )
        }
    }

    fun confirmRatingDialog(
        watchedAt: LocalDate,
        firstProfileRating: Rating,
        secondProfileRating: Rating,
    ) {
        val currentState = _state.value
        val movie = currentState.movie ?: return
        val mode = currentState.ratingDialogMode ?: return

        if (currentState.isSavingRatings) {
            return
        }

        viewModelScope.launch {
            _state.update {
                it.copy(
                    isSavingRatings = true,
                    error = null,
                )
            }

            val result: Result<Unit> = when (mode) {
                RatingDialogMode.RECORD_VIEWING -> {
                    when {
                        currentState.isWatched -> {
                            Result.failure(
                                IllegalStateException(
                                    "Film jest już oznaczony jako obejrzany.",
                                )
                            )
                        }

                        !currentState.isOnWatchlist -> {
                            Result.failure(
                                IllegalStateException(
                                    "Film nie znajduje się na watchliście.",
                                )
                            )
                        }

                        else -> {
                            markMovieAsWatchedUseCase(
                                movie = movie,
                                watchedAt = watchedAt,
                                firstProfileRating =
                                    firstProfileRating,
                                secondProfileRating =
                                    secondProfileRating,
                            ).map {
                                Unit
                            }
                        }
                    }
                }

                RatingDialogMode.EDIT_RATINGS -> {
                    if (!currentState.isWatched) {
                        Result.failure(
                            IllegalStateException(
                                "Film nie jest oznaczony jako obejrzany.",
                            )
                        )
                    } else {
                        updateMovieRatingsUseCase(
                            movieId = movie.id,
                            firstProfileRating =
                                firstProfileRating,
                            secondProfileRating =
                                secondProfileRating,
                        )
                    }
                }
            }

            result
                .onSuccess {
                    _state.update {
                        it.copy(
                            ratingDialogMode = null,
                            isSavingRatings = false,
                            isOnWatchlist = when (mode) {
                                RatingDialogMode.RECORD_VIEWING ->
                                    false

                                RatingDialogMode.EDIT_RATINGS ->
                                    it.isOnWatchlist
                            },
                        )
                    }
                }
                .onFailure { error ->
                    _state.update {
                        it.copy(
                            isSavingRatings = false,
                            error = error.message
                                ?: "Nie udało się zapisać filmu",
                        )
                    }
                }
        }
    }

    fun removeFromWatched() {
        val currentState = _state.value

        if (!currentState.isWatched) {
            return
        }

        viewModelScope.launch {
            _state.update {
                it.copy(
                    isRemovingFromWatched = true,
                    error = null,
                )
            }

            removeMovieFromWatchedUseCase(movieId)
                .onSuccess { removed ->
                    _state.update {
                        it.copy(
                            isRemovingFromWatched = false,
                            ratingDialogMode = null,
                            error = if (removed) {
                                null
                            } else {
                                "Film nie był oznaczony jako obejrzany."
                            },
                        )
                    }
                }
                .onFailure { error ->
                    _state.update {
                        it.copy(
                            isRemovingFromWatched = false,
                            error = error.message
                                ?: "Nie udało się usunąć filmu z obejrzanych",
                        )
                    }
                }
        }
    }
}