package com.example.reely

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.reely.domain.usecase.user.EnsureProfilesExistUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface MainState {

    data object Loading : MainState

    data object Ready : MainState

    data class Error(
        val message: String,
    ) : MainState
}

@HiltViewModel
class MainViewModel @Inject constructor(
    private val ensureProfilesExistUseCase: EnsureProfilesExistUseCase,
) : ViewModel() {

    private val _state =
        MutableStateFlow<MainState>(MainState.Loading)

    val state: StateFlow<MainState> =
        _state.asStateFlow()

    init {
        initialize()
    }

    fun initialize() {
        viewModelScope.launch {
            _state.value = MainState.Loading

            ensureProfilesExistUseCase()
                .onSuccess {
                    _state.value = MainState.Ready
                }
                .onFailure { exception ->
                    _state.value = MainState.Error(
                        message = exception.message
                            ?: "Nie udało się utworzyć profili",
                    )
                }
        }
    }
}