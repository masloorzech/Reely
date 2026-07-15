package com.example.reely.presentation.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.reely.domain.model.Profile
import com.example.reely.domain.model.ProfileId
import com.example.reely.domain.usecase.user.ObserveProfilesUseCase
import com.example.reely.domain.usecase.user.UpdateProfileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ProfileScreenState(
    val profiles: List<Profile> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null,
    val profileDialog: ProfileDialogState? = null,
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
    private val observeProfilesUseCase: ObserveProfilesUseCase,
    private val updateProfileUseCase: UpdateProfileUseCase,
) : ViewModel() {

    private val _state =
        MutableStateFlow(ProfileScreenState())

    val state = _state.asStateFlow()

    init {
        observeProfiles()
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
}