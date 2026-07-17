package com.example.reely.presentation.ui.screens.statistics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.reely.domain.model.ReelyStatistics
import com.example.reely.domain.usecase.statistics.ObserveStatisticsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

data class StatisticsScreenUiState(
    val statistics: ReelyStatistics? = null,
    val isLoading: Boolean = true,
    val error: String? = null,
)

@HiltViewModel
class StatisticsScreenViewModel @Inject constructor(
    observeStatisticsUseCase: ObserveStatisticsUseCase,
) : ViewModel() {

    val state: StateFlow<StatisticsScreenUiState> =
        observeStatisticsUseCase()
            .map { statistics ->
                StatisticsScreenUiState(
                    statistics = statistics,
                    isLoading = false,
                    error = null,
                )
            }
            .catch { exception ->
                emit(
                    StatisticsScreenUiState(
                        statistics = null,
                        isLoading = false,
                        error = exception.message
                            ?: "Nie udało się wczytać statystyk.",
                    ),
                )
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(
                    stopTimeoutMillis = 5_000,
                ),
                initialValue = StatisticsScreenUiState(),
            )
}