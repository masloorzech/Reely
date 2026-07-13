package com.example.reely.presentation.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.reely.domain.model.StatsOverview
import com.example.reely.domain.model.User
import com.example.reely.domain.model.UserId
import com.example.reely.domain.usecase.statistics.ObserveStatsUseCase
import com.example.reely.domain.usecase.user.CreateUserUseCase
import com.example.reely.domain.usecase.user.GetAllUsersUseCase
import com.example.reely.domain.usecase.user.GetMainUserUseCase
import com.example.reely.domain.usecase.user.UpdateUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileScreenState(
    val isLoading: Boolean = false,

    val mainUser: User = User(
        id = UserId(-1),
        nickname = "",
        avatarPath = null
    ),
    val mainUserError: String? = null,

    val allUsers: List<User> = emptyList(),
    val allUsersError: String? = null,

    val userDialog: UserDialogState? = null
)

data class UserDialogState(
    val mode: UserDialogMode,
    val userId: UserId? = null,
    val nickname: String = "",
    val avatarPath: String? = null,
    val error: String? = null,
    val isSaving: Boolean = false
)

enum class UserDialogMode {
    Create,
    Edit
}

@HiltViewModel
class ProfileScreenViewModel @Inject constructor(
    private val getAllUsersUseCase: GetAllUsersUseCase,
    private val getMainUserUseCase: GetMainUserUseCase,
    private val createUserUseCase: CreateUserUseCase,
    private val updateUserUseCase: UpdateUserUseCase,
    observeStatsUseCase: ObserveStatsUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileScreenState())
    val state = _state.asStateFlow()

    val stats = observeStatsUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = StatsOverview()
        )

    init {
        loadUserData()
    }

    fun loadUserData() {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    isLoading = true,
                    allUsersError = null,
                    mainUserError = null
                )
            }

            val usersResult = getAllUsersUseCase()
            val mainUserResult = getMainUserUseCase()

            _state.update { currentState ->
                currentState.copy(
                    isLoading = false,

                    allUsers = usersResult.getOrDefault(
                        currentState.allUsers
                    ),
                    allUsersError = usersResult
                        .exceptionOrNull()
                        ?.message,

                    mainUser = mainUserResult.getOrDefault(
                        currentState.mainUser
                    ),
                    mainUserError = mainUserResult
                        .exceptionOrNull()
                        ?.message
                )
            }
        }
    }

    fun openCreateUserDialog() {
        _state.update {
            it.copy(
                userDialog = UserDialogState(
                    mode = UserDialogMode.Create
                )
            )
        }
    }

    fun openEditUserDialog(user: User) {
        _state.update {
            it.copy(
                userDialog = UserDialogState(
                    mode = UserDialogMode.Edit,
                    userId = user.id,
                    nickname = user.nickname,
                    avatarPath = user.avatarPath
                )
            )
        }
    }

    fun dismissUserDialog() {
        if (_state.value.userDialog?.isSaving == true) return

        _state.update {
            it.copy(userDialog = null)
        }
    }

    fun onDialogNicknameChange(nickname: String) {
        _state.update { currentState ->
            val dialog = currentState.userDialog
                ?: return@update currentState

            currentState.copy(
                userDialog = dialog.copy(
                    nickname = nickname,
                    error = null
                )
            )
        }
    }

    fun onDialogAvatarPicked(avatarPath: String?) {
        if (avatarPath.isNullOrBlank()) return

        _state.update { currentState ->
            val dialog = currentState.userDialog
                ?: return@update currentState

            currentState.copy(
                userDialog = dialog.copy(
                    avatarPath = avatarPath,
                    error = null
                )
            )
        }
    }

    fun confirmUserDialog() {
        val dialog = _state.value.userDialog ?: return
        val nickname = dialog.nickname.trim()

        if (nickname.isBlank()) {
            _state.update { currentState ->
                currentState.copy(
                    userDialog = dialog.copy(
                        error = "Nazwa użytkownika nie może być pusta"
                    )
                )
            }
            return
        }

        viewModelScope.launch {
            _state.update { currentState ->
                currentState.copy(
                    userDialog = dialog.copy(
                        isSaving = true
                    )
                )
            }

            runCatching {
                when (dialog.mode) {
                    UserDialogMode.Create -> {
                        createUserUseCase(
                            nickname = nickname,
                            avatarPath = dialog.avatarPath
                        )
                    }

                    UserDialogMode.Edit -> {
                        updateUserUseCase(
                            userId = requireNotNull(dialog.userId),
                            nickname = nickname,
                            avatarPath = dialog.avatarPath
                        )
                    }
                }
            }.onSuccess {
                _state.update {
                    it.copy(userDialog = null)
                }

                loadUserData()
            }.onFailure { error ->
                _state.update { currentState ->
                    val currentDialog = currentState.userDialog
                        ?: return@update currentState

                    currentState.copy(
                        userDialog = currentDialog.copy(
                            isSaving = false,
                            error = error.message
                                ?: "Nie udało się zapisać użytkownika"
                        )
                    )
                }
            }
        }
    }
}