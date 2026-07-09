package com.example.reely.presentation.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.reely.domain.model.User
import com.example.reely.domain.usecase.user.GetAllUsersUseCase
import com.example.reely.presentation.theme.ReelyTheme
import com.example.reely.presentation.ui.screens.home.components.HelloSection
import com.example.reely.presentation.ui.screens.home.components.HomeScreenViewModel
import com.example.reely.presentation.ui.screens.home.components.LastSeenSection
import com.example.reely.presentation.ui.screens.home.components.OurStatsSection
import com.example.reely.presentation.ui.screens.home.components.RandomFilmCard
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    vm : HomeScreenViewModel = viewModel()
) {

    val state = vm.state.collectAsState().value

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        )
        {

            HelloSection(
                firstAvatarPath = state.users.firstOrNull()?.avatarPath,
                secondAvatarPath = state.users.getOrNull(1)?.avatarPath
            )

            Spacer(modifier = Modifier.height(16.dp))

            RandomFilmCard(
                onClick = {}
            )

            Spacer(modifier = Modifier.height(16.dp))

            LastSeenSection(emptyList())

            Spacer(modifier = Modifier.height(16.dp))

            OurStatsSection(
                userRatingItems = null
            )

            //CalendarSection()

        }


    }
}


@Preview(
    showBackground = true,
    backgroundColor = 0xFF0B0E14
)
@Composable
fun HomeScreenPreview() {
    ReelyTheme {
        HomeScreen()
    }
}