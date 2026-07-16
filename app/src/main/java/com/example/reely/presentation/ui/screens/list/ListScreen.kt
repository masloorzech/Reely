package com.example.reely.presentation.ui.screens.list

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.composables.icons.lucide.Funnel
import com.composables.icons.lucide.Grid3x2
import com.composables.icons.lucide.List
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Star
import com.composables.icons.lucide.TvMinimalPlay
import com.example.reely.domain.model.Genre
import com.example.reely.presentation.ui.components.ReelyButton
import com.example.reely.presentation.ui.components.ReelyChip
import com.example.reely.presentation.ui.components.ReelyFilterMenu
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
    val state by viewModel.state.collectAsStateWithLifecycle()

    var filterExpanded by remember { mutableStateOf(false) }

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

                ReelyFilterMenu(
                    expanded = filterExpanded,
                    onExpandedChange = {
                        filterExpanded = it
                    },
                    trigger = { onClick ->
                        BadgedBox(
                            badge = {
                                if (state.selectedGenreIds.isNotEmpty()) {
                                    Box(
                                        modifier = Modifier
                                            .offset(x = 2.dp, y = (-2).dp)
                                            .background(
                                                color = MaterialTheme.colorScheme.primary,
                                                shape = RoundedCornerShape(100)
                                            )
                                            .size(20.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = state.selectedGenreIds.size.toString(),
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onPrimary
                                        )
                                    }
                                }
                            }
                        ) {
                            ReelyIconButton(
                                icon = Lucide.Funnel,
                                onClick = onClick
                            )
                        }
                    },
                    content = {
                        Row() {
                            ReelyButton(
                                text = "Wyczyść filtry",
                                overrideContainerColor = MaterialTheme.colorScheme.background.copy(
                                    alpha = 0.6f
                                ),
                                onClick = {
                                    viewModel.clearFilters()
                                    filterExpanded = false
                                }
                            )
                        }

                        Spacer(Modifier.height(4.dp))

                        HorizontalDivider()

                        Spacer(Modifier.height(4.dp))

                        GenreFilters(
                            genres = state.genres,
                            selectedGenreIds = state.selectedGenreIds,
                            onToggle = viewModel::toggleGenre
                        )

                        Spacer(Modifier.height(16.dp))


                    }
                )
                Spacer(Modifier.width(8.dp))
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
private fun GenreFilters(
    genres: List<Genre> = emptyList(),
    selectedGenreIds: Set<Int> = emptySet(),
    onToggle: (Genre) -> Unit
){
    Column() {
        Text(
            text = "Gatunki",
            style = MaterialTheme.typography.labelMedium,
        )

        Spacer(Modifier.height(4.dp))

        val rowsCount = 2

        val genreRows = remember(genres) {
            List(rowsCount) { rowIndex ->
                genres.filterIndexed { index, _ ->
                    index % rowsCount == rowIndex
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                genreRows.forEach { rowGenres ->
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        rowGenres.forEach { genre ->
                            ReelyChip(
                                text = genre.name,
                                selected = genre.id in selectedGenreIds,
                                onClick = {
                                    onToggle(genre)
                                }
                            )
                        }
                    }
                }
            }
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
