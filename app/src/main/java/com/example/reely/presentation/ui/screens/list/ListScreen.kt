package com.example.reely.presentation.ui.screens.list

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.composables.icons.lucide.ArrowDown
import com.composables.icons.lucide.ArrowDownAZ
import com.composables.icons.lucide.ArrowUp
import com.composables.icons.lucide.Clapperboard
import com.composables.icons.lucide.Clock
import com.composables.icons.lucide.Grid3x2
import com.composables.icons.lucide.List
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Star
import com.composables.icons.lucide.TvMinimalPlay
import com.example.reely.presentation.ui.components.ReelyIconButton
import com.example.reely.presentation.ui.components.ReelyListTabs
import com.example.reely.presentation.ui.components.ReelyMenuButton
import com.example.reely.presentation.ui.components.ReelySearchField
import com.example.reely.presentation.ui.components.ReelyTab
import com.example.reely.presentation.ui.components.TopBar
import com.example.reely.presentation.ui.screens.list.components.MovieList
import com.example.reely.presentation.ui.screens.list.components.MovieListGrid

enum class MovieListTab(
    override val tabTitle: String,
    override val tabIcon: ImageVector?,
) : ReelyTab {

    WATCHLIST(
        tabTitle = "Do obejrzenia",
        tabIcon = Lucide.TvMinimalPlay,
    ),

    WATCHED(
        tabTitle = "Obejrzane",
        tabIcon = Lucide.Clapperboard,
    ),
}

enum class MovieSortOption(
    val label: String,
    val icon: ImageVector,
) {
    RATING_DESC(
        label = "Najlepiej oceniane",
        icon = Lucide.Star,
    ),

    NEWEST(
        label = "Najnowsze",
        icon = Lucide.ArrowDown,
    ),

    OLDEST(
        label = "Najstarsze",
        icon = Lucide.ArrowUp,
    ),

    TITLE_ASC(
        label = "Alfabetycznie",
        icon = Lucide.ArrowDownAZ,
    ),

    ADDING_TIME(
        label = "Ostatnio dodane",
        icon = Lucide.Clock,
    ),
}

enum class MovieDisplayStyle(
    val icon: ImageVector,
) {
    GRID(Lucide.Grid3x2),
    LIST(Lucide.List),
}

@Composable
fun ListScreen(
    modifier: Modifier = Modifier,
    viewModel: ListScreenViewModel = hiltViewModel(),
    onMovieClick: (movieId: Int) -> Unit = {},
) {
    val state by
    viewModel.state.collectAsStateWithLifecycle()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                MaterialTheme.colorScheme.background,
            )
            .padding(16.dp),
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
        ) {
            TopBar(
                sectionName = "Twoje listy",
            )

            Spacer(Modifier.height(16.dp))

            ReelyListTabs(
                tabs = MovieListTab.entries,
                selectedTab = state.selectedTab,
                onTabSelected = viewModel::switchTab,
            )

            Spacer(Modifier.height(16.dp))

            ReelySearchField(
                value = state.searchQuery,
                onValueChange =
                    viewModel::changeSearchQuery,
                placeholder =
                    "Wyszukaj film w sekcji ${
                        state.selectedTab
                            .tabTitle
                            .lowercase()
                    }...",
                onSearchClick = {},
            )

            Spacer(Modifier.height(16.dp))

            Row(
                verticalAlignment =
                    Alignment.CenterVertically,
            ) {
                ReelyMenuButton(
                    selectedOption = state.selectedSort,
                    options = MovieSortOption.entries,
                    optionLabel = { option ->
                        option.label
                    },
                    optionIcon = { option ->
                        option.icon
                    },
                    onOptionSelected =
                        viewModel::switchSort,
                )

                Spacer(Modifier.width(8.dp))

                Text(
                    text = "Filmy: ${state.moviesList.size}",
                    style =
                        MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme
                        .onBackground
                        .copy(alpha = 0.6f),
                )

                Spacer(Modifier.weight(1f))

                ReelyIconButton(
                    onClick =
                        viewModel::toggleDisplayStyle,
                    icon = state.displayStyle.icon,
                )
            }

            Spacer(Modifier.height(16.dp))

            ListContent(
                modifier = Modifier.weight(1f),
                state = state,
                onMovieClick = onMovieClick,
            )
        }
    }
}

@Composable
private fun ListContent(
    state: ListScreenUiState,
    onMovieClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    when {
        state.isLoading -> {
            Box(
                modifier = modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator()
            }
        }

        state.error != null -> {
            Box(
                modifier = modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = state.error,
                    style =
                        MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error,
                )
            }
        }

        state.moviesList.isEmpty() -> {
            Box(
                modifier = modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = emptyListMessage(state),
                    style =
                        MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme
                        .onBackground
                        .copy(alpha = 0.6f),
                )
            }
        }

        state.displayStyle == MovieDisplayStyle.GRID -> {
            MovieListGrid(
                modifier = modifier,
                movies = state.moviesList,
                onMovieClick = onMovieClick,
            )
        }

        else -> {
            MovieList(
                modifier = modifier,
                movies = state.moviesList,
                onMovieClick = onMovieClick,
            )
        }
    }
}

private fun emptyListMessage(
    state: ListScreenUiState,
): String {
    if (state.searchQuery.isNotBlank()) {
        return "Nie znaleziono filmu „${state.searchQuery.trim()}”."
    }

    return when (state.selectedTab) {
        MovieListTab.WATCHLIST ->
            "Watchlista jest pusta."

        MovieListTab.WATCHED ->
            "Brak obejrzanych filmów."
    }
}