package com.example.reely

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.reely.domain.usecase.EnsureMainUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class MainViewModel @Inject constructor(
    private val ensureMainUserUseCase: EnsureMainUserUseCase
) : ViewModel() {

    init {
        ensureMainUser()
    }

    private fun ensureMainUser() {
        viewModelScope.launch {
            ensureMainUserUseCase()
        }
    }
}