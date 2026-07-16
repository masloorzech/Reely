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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.reely.presentation.theme.ReelyTheme
import com.example.reely.presentation.ui.screens.home.components.HelloSection
import com.example.reely.presentation.ui.screens.home.components.LastSeenSection
import com.example.reely.presentation.ui.screens.home.components.RandomFilmCard
import com.example.reely.presentation.ui.screens.home.components.RandomMovieSheet
import com.example.reely.presentation.ui.screens.home.components.WatchedMoviesCalendarSection

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    onMovieClick: (movieId: Int) -> Unit = {},
    onSeeMoreClick: () -> Unit = {},
    viewModel: HomeScreenViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    var showRandomMovieSheet by rememberSaveable {
        mutableStateOf(false)
    }

    var lastRandomMovieId by rememberSaveable {
        mutableStateOf<Int?>(null)
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
        ) {
            HelloSection(
                firstAvatarPath =
                    state.profiles.firstOrNull()?.avatarPath,
                secondAvatarPath =
                    state.profiles.getOrNull(1)?.avatarPath,
            )

            Spacer(modifier = Modifier.height(16.dp))

            RandomFilmCard(
                onClick = {
                    showRandomMovieSheet = true
                },
            )

            Spacer(modifier = Modifier.height(16.dp))

            LastSeenSection(
                lastSeenMoviesList = state.recentSeenMovies.distinctBy{it.movieId},
                onMovieClick = { movie ->
                    onMovieClick(movie.id)
                },
                onSeeMoreClick = onSeeMoreClick,
            )

            state.error?.let { message ->
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = message,
                    color = MaterialTheme.colorScheme.error,
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            WatchedMoviesCalendarSection(
                month = state.calendarMonth,
                moviesByDate = state.watchedMoviesByDate,
            )
        }
        if (showRandomMovieSheet) {
            RandomMovieSheet(
                moviesToRoll = state.watchlist,
                previousMovieId = lastRandomMovieId,
                onMovieDrawn = { selectedEntry ->
                    lastRandomMovieId = selectedEntry.movie.id
                },
                onMovieClick = { movieId ->
                    showRandomMovieSheet = false
                    onMovieClick(movieId)
                },
                onDismiss = {
                    showRandomMovieSheet = false
                },
            )
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