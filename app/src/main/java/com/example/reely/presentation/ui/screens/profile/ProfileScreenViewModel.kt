package com.example.reely.presentation.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.reely.domain.model.User
import com.example.reely.domain.model.UserId
import com.example.reely.domain.usecase.user.GetAllUsersUseCase
import com.example.reely.domain.usecase.user.GetMainUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileScreenState(

    val isLoading : Boolean = false,

    val mainUser : User = User(id = UserId(-1), nickname = "", avatarPath = null),
    val mainUserError: String? = null,

    val allUsers : List<User> = emptyList(),
    val allUsersError : String? = null,

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
    private val getAllUsersUseCase : GetAllUsersUseCase,
    private val getMainUserUseCase: GetMainUserUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileScreenState())

    val state = _state.asStateFlow()

    init {
        loadUserData()
    }

    fun loadUserData(){

        viewModelScope.launch {
            _state.update {
                it.copy(
                    isLoading = true
                )
            }

            getAllUsersUseCase()
                .onSuccess { users ->
                    _state.update {
                        it.copy(
                            allUsers = users
                        )
                    }
                }
                .onFailure { error ->
                    _state.update {
                        it.copy(
                            allUsersError = error.message
                        )
                    }
                }

            getMainUserUseCase()
                .onSuccess { user ->
                    _state.update {
                        it.copy(
                            mainUser = user
                        )
                    }
                }
                .onFailure { error ->
                    _state.update {
                        it.copy(
                            mainUserError = error.message
                        )
                    }
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
        _state.update { state ->
            val dialog = state.userDialog ?: return@update state

            state.copy(
                userDialog = dialog.copy(
                    nickname = nickname,
                    error = null
                )
            )
        }
    }

    fun onDialogAvatarSelected(localAvatarPath: String) {
        _state.update { state ->
            val dialog = state.userDialog ?: return@update state

            state.copy(
                userDialog = dialog.copy(
                    avatarPath = localAvatarPath
                )
            )
        }
    }

    fun confirmUserDialog() {
        val dialog = _state.value.userDialog ?: return
        val nickname = dialog.nickname.trim()

        if (nickname.isBlank()) {
            _state.update { state ->
                state.copy(
                    userDialog = dialog.copy(
                        error = "Nazwa użytkownika nie może być pusta"
                    )
                )
            }
            return
        }

        viewModelScope.launch {
            _state.update { state ->
                state.copy(
                    userDialog = dialog.copy(isSaving = true)
                )
            }

            val result = when (dialog.mode) {
                UserDialogMode.Create -> {
                    // createUserUseCase(
                    //     nickname = nickname,
                    //     avatarPath = dialog.avatarPath
                    // )
                    Result.success(Unit)
                }

                UserDialogMode.Edit -> {
                    // updateUserUseCase(
                    //     userId = dialog.userId!!,
                    //     nickname = nickname,
                    //     avatarPath = dialog.avatarPath
                    // )

                    Result.success(Unit)
                }
            }

            result
                .onSuccess {
                    loadUserData()

                    _state.update { state ->
                        state.copy(userDialog = null)
                    }
                }
                .onFailure { error ->
                    _state.update { state ->
                        val currentDialog = state.userDialog ?: return@update state

                        state.copy(
                            userDialog = currentDialog.copy(
                                isSaving = false,
                                error = error.message ?: "Nie udało się zapisać użytkownika"
                            )
                        )
                    }
                }
        }
    }

}