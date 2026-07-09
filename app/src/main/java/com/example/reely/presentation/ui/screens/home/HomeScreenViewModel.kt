package com.example.reely.presentation.ui.screens.home.components

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.reely.domain.model.User
import com.example.reely.domain.usecase.user.GetAllUsersUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HomeScreenUIState(

    val users: List<User> = emptyList(),
    val error: String? = null

)

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val getAllUsersUseCase: GetAllUsersUseCase
) : ViewModel(){

    private val _state = MutableStateFlow(HomeScreenUIState())

    val state = _state.asStateFlow()

    init {
        loadInitialData()
    }

    fun loadInitialData(){
        viewModelScope.launch {
            getAllUsersUseCase().onSuccess { users ->
                _state.update {
                    it.copy(
                        users = users
                    )
                }
            }
                .onFailure {
                        error ->
                    _state.update {
                        it.copy(
                            error = error.message
                        )
                    }

                }
        }
    }
}
