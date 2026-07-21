package com.example.reely.presentation.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.reely.domain.model.Profile
import com.example.reely.domain.model.ProfileId
import com.example.reely.domain.model.WatchMonetizationType
import com.example.reely.domain.model.WatchProvider
import com.example.reely.domain.usecase.movie.ObserveSelectedWatchProviderIdsUseCase
import com.example.reely.domain.usecase.movie.ObserveSelectedWatchProviderTypesUseCase
import com.example.reely.domain.usecase.movie.ObserveWatchProviderCatalogUseCase
import com.example.reely.domain.usecase.movie.RefreshLibraryWatchProvidersUseCase
import com.example.reely.domain.usecase.movie.RefreshWatchProviderCatalogUseCase
import com.example.reely.domain.usecase.movie.UpdateWatchProviderPreferencesUseCase
import com.example.reely.domain.usecase.user.ObserveProfilesUseCase
import com.example.reely.domain.usecase.user.UpdateProfileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject

data class ProfileScreenState(
    val profiles: List<Profile> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null,
    val profileDialog: ProfileDialogState? = null,

    val availableProviders: List<WatchProvider> =
        emptyList(),

    val selectedProviderIds: Set<Int> =
        emptySet(),

    val selectedProviderTypes:
    Set<WatchMonetizationType> =
        setOf(WatchMonetizationType.FLATRATE),

    val areProviderSettingsLoading: Boolean =
        true,

    val isSavingProviderSettings: Boolean =
        false,

    val isRefreshingProviders: Boolean =
        false,

    val providerSettingsMessage: String? =
        null,

    val providerSettingsError: String? =
        null,
)

data class ProfileDialogState(
    val profileId: ProfileId,
    val name: String,
    val avatarPath: String?,
    val error: String? = null,
    val isSaving: Boolean = false,
)

@HiltViewModel
class ProfileScreenViewModel @Inject constructor(
    private val observeProfilesUseCase:
    ObserveProfilesUseCase,

    private val updateProfileUseCase:
    UpdateProfileUseCase,

    private val observeWatchProviderCatalogUseCase:
    ObserveWatchProviderCatalogUseCase,

    private val observeSelectedWatchProviderIdsUseCase:
    ObserveSelectedWatchProviderIdsUseCase,

    private val observeSelectedWatchProviderTypesUseCase:
    ObserveSelectedWatchProviderTypesUseCase,

    private val updateWatchProviderPreferencesUseCase:
    UpdateWatchProviderPreferencesUseCase,

    private val refreshWatchProviderCatalogUseCase:
    RefreshWatchProviderCatalogUseCase,

    private val refreshLibraryWatchProvidersUseCase:
    RefreshLibraryWatchProvidersUseCase,
) : ViewModel() {

    private val _state =
        MutableStateFlow(ProfileScreenState())

    val state = _state.asStateFlow()

    private val providerPreferencesMutex =
        Mutex()

    private var pendingProviderSaves = 0

    private var persistedProviderIds:
            Set<Int> = emptySet()

    private var persistedProviderTypes:
            Set<WatchMonetizationType> =
        setOf(WatchMonetizationType.FLATRATE)

    init {
        observeProfiles()
        observeProviderSettings()
        refreshProviderCatalog()
    }

    private fun observeProfiles() {
        viewModelScope.launch {
            observeProfilesUseCase()
                .catch { error ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = error.message
                                ?: "Nie udało się wczytać profili",
                        )
                    }
                }
                .collect { profiles ->
                    _state.update {
                        it.copy(
                            profiles = profiles.sortedBy {
                                    profile -> profile.position.ordinal
                            },
                            isLoading = false,
                            error = null,
                        )
                    }
                }
        }
    }

    private fun observeProviderSettings() {
        viewModelScope.launch {
            combine(
                observeWatchProviderCatalogUseCase(),
                observeSelectedWatchProviderIdsUseCase(),
                observeSelectedWatchProviderTypesUseCase(),
            ) {
                    providers,
                    selectedProviderIds,
                    selectedProviderTypes,
                ->

                ProviderSettingsSource(
                    providers = providers,
                    selectedProviderIds =
                        selectedProviderIds,
                    selectedProviderTypes =
                        selectedProviderTypes.ifEmpty {
                            setOf(
                                WatchMonetizationType.FLATRATE
                            )
                        },
                )
            }
                .catch { error ->
                    _state.update { currentState ->
                        currentState.copy(
                            areProviderSettingsLoading =
                                false,

                            providerSettingsError =
                                error.message
                                    ?: "Nie udało się wczytać serwisów.",
                        )
                    }
                }
                .collect { source ->
                    persistedProviderIds =
                        source.selectedProviderIds

                    persistedProviderTypes =
                        source.selectedProviderTypes

                    _state.update { currentState ->
                        val selectedIds =
                            if (pendingProviderSaves > 0) {
                                currentState.selectedProviderIds
                            } else {
                                source.selectedProviderIds
                            }

                        val selectedTypes =
                            if (pendingProviderSaves > 0) {
                                currentState.selectedProviderTypes
                            } else {
                                source.selectedProviderTypes
                            }

                        currentState.copy(
                            availableProviders =
                                source.providers,

                            selectedProviderIds =
                                selectedIds,

                            selectedProviderTypes =
                                selectedTypes,

                            areProviderSettingsLoading =
                                false,

                            providerSettingsError =
                                null,
                        )
                    }
                }
        }
    }

    fun toggleWatchProvider(
        providerId: Int,
    ) {
        val currentState = _state.value

        val updatedProviderIds =
            currentState
                .selectedProviderIds
                .toggle(providerId)

        _state.update {
            it.copy(
                selectedProviderIds =
                    updatedProviderIds,
                providerSettingsMessage = null,
                providerSettingsError = null,
            )
        }

        saveProviderPreferences(
            providerIds = updatedProviderIds,
            types = currentState.selectedProviderTypes,
        )
    }

    fun toggleWatchProviderType(
        type: WatchMonetizationType,
    ) {
        val currentState = _state.value

        val currentTypes =
            currentState.selectedProviderTypes

        if (
            type in currentTypes &&
            currentTypes.size == 1
        ) {
            _state.update {
                it.copy(
                    providerSettingsMessage =
                        "Wybierz co najmniej jeden rodzaj dostępu.",
                )
            }

            return
        }

        val updatedTypes =
            currentTypes.toggle(type)

        _state.update {
            it.copy(
                selectedProviderTypes =
                    updatedTypes,
                providerSettingsMessage = null,
                providerSettingsError = null,
            )
        }

        saveProviderPreferences(
            providerIds =
                currentState.selectedProviderIds,
            types = updatedTypes,
        )
    }

    private fun saveProviderPreferences(
        providerIds: Set<Int>,
        types: Set<WatchMonetizationType>,
    ) {
        pendingProviderSaves += 1

        _state.update {
            it.copy(
                isSavingProviderSettings = true,
            )
        }

        viewModelScope.launch {
            val result =
                providerPreferencesMutex.withLock {
                    updateWatchProviderPreferencesUseCase(
                        providerIds = providerIds,
                        monetizationTypes = types,
                    )
                }

            pendingProviderSaves -= 1

            result
                .onSuccess {
                    _state.update { currentState ->
                        currentState.copy(
                            isSavingProviderSettings =
                                pendingProviderSaves > 0,

                            providerSettingsMessage =
                                if (pendingProviderSaves == 0) {
                                    "Zapisano ustawienia serwisów."
                                } else {
                                    currentState
                                        .providerSettingsMessage
                                },

                            providerSettingsError =
                                null,
                        )
                    }
                }
                .onFailure { error ->
                    _state.update { currentState ->
                        currentState.copy(
                            selectedProviderIds =
                                if (pendingProviderSaves == 0) {
                                    persistedProviderIds
                                } else {
                                    currentState
                                        .selectedProviderIds
                                },

                            selectedProviderTypes =
                                if (pendingProviderSaves == 0) {
                                    persistedProviderTypes
                                } else {
                                    currentState
                                        .selectedProviderTypes
                                },

                            isSavingProviderSettings =
                                pendingProviderSaves > 0,

                            providerSettingsError =
                                error.message
                                    ?: "Nie udało się zapisać ustawień serwisów.",
                        )
                    }
                }
        }
    }

    fun refreshLibraryProviders() {
        if (_state.value.isRefreshingProviders) {
            return
        }

        viewModelScope.launch {
            _state.update {
                it.copy(
                    isRefreshingProviders = true,
                    providerSettingsMessage = null,
                    providerSettingsError = null,
                )
            }

            refreshLibraryWatchProvidersUseCase(
                forceRefresh = true,
            )
                .onSuccess { result ->
                    val message = when {
                        result.totalMovies == 0 -> {
                            "Brak filmów do odświeżenia."
                        }

                        result.failedMovieIds.isEmpty() -> {
                            "Zaktualizowano dostępność dla " +
                                    "${result.refreshedMovies} filmów."
                        }

                        else -> {
                            "Zaktualizowano " +
                                    "${result.refreshedMovies} z " +
                                    "${result.totalMovies} filmów."
                        }
                    }

                    _state.update {
                        it.copy(
                            isRefreshingProviders = false,
                            providerSettingsMessage = message,
                        )
                    }
                }
                .onFailure { error ->
                    _state.update {
                        it.copy(
                            isRefreshingProviders = false,
                            providerSettingsError =
                                error.message
                                    ?: "Nie udało się odświeżyć dostępności.",
                        )
                    }
                }
        }
    }


    private fun refreshProviderCatalog() {
        viewModelScope.launch {
            refreshWatchProviderCatalogUseCase(
                forceRefresh = false,
            )
                .onFailure { error ->
                    _state.update { currentState ->
                        currentState.copy(
                            areProviderSettingsLoading =
                                false,

                            providerSettingsError =
                                error.message
                                    ?: "Nie udało się odświeżyć katalogu serwisów.",
                        )
                    }
                }
        }
    }

    fun openEditProfileDialog(
        profile: Profile,
    ) {
        _state.update {
            it.copy(
                profileDialog = ProfileDialogState(
                    profileId = profile.id,
                    name = profile.name,
                    avatarPath = profile.avatarPath,
                ),
            )
        }
    }

    fun dismissProfileDialog() {
        if (
            _state.value
                .profileDialog
                ?.isSaving == true
        ) {
            return
        }

        _state.update {
            it.copy(profileDialog = null)
        }
    }

    fun onDialogNameChange(
        name: String,
    ) {
        _state.update { currentState ->
            val dialog = currentState.profileDialog
                ?: return@update currentState

            currentState.copy(
                profileDialog = dialog.copy(
                    name = name,
                    error = null,
                ),
            )
        }
    }

    fun onDialogAvatarPicked(
        avatarPath: String?,
    ) {
        if (avatarPath.isNullOrBlank()) {
            return
        }

        _state.update { currentState ->
            val dialog = currentState.profileDialog
                ?: return@update currentState

            currentState.copy(
                profileDialog = dialog.copy(
                    avatarPath = avatarPath,
                    error = null,
                ),
            )
        }
    }

    fun confirmProfileDialog() {
        val dialog = _state.value.profileDialog
            ?: return

        if (dialog.isSaving) {
            return
        }

        val normalizedName = dialog.name.trim()

        if (normalizedName.isBlank()) {
            _state.update { currentState ->
                currentState.copy(
                    profileDialog = dialog.copy(
                        error = "Nazwa profilu nie może być pusta",
                    ),
                )
            }

            return
        }

        viewModelScope.launch {
            _state.update { currentState ->
                currentState.copy(
                    profileDialog = dialog.copy(
                        name = normalizedName,
                        isSaving = true,
                        error = null,
                    ),
                )
            }

            updateProfileUseCase(
                profileId = dialog.profileId,
                name = normalizedName,
                avatarPath = dialog.avatarPath,
            )
                .onSuccess {
                    _state.update {
                        it.copy(profileDialog = null)
                    }
                }
                .onFailure { error ->
                    _state.update { currentState ->
                        val currentDialog =
                            currentState.profileDialog
                                ?: return@update currentState

                        currentState.copy(
                            profileDialog =
                                currentDialog.copy(
                                    isSaving = false,
                                    error = error.message
                                        ?: "Nie udało się zapisać profilu",
                                ),
                        )
                    }
                }
        }
    }

    private fun <T> Set<T>.toggle(
        value: T,
    ): Set<T> {
        return if (value in this) {
            this - value
        } else {
            this + value
        }
    }

}

private data class ProviderSettingsSource(
    val providers: List<WatchProvider>,
    val selectedProviderIds: Set<Int>,
    val selectedProviderTypes:
    Set<WatchMonetizationType>,
)