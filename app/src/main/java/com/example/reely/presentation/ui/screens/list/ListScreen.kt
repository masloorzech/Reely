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
import com.example.reely.domain.model.MovieProviderFilter
import com.example.reely.domain.model.ProviderFilterScope
import com.example.reely.domain.model.WatchMonetizationType
import com.example.reely.domain.model.WatchProvider
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

    var filterExpanded by remember {
        mutableStateOf(false)
    }

    val activeFiltersCount = state.activeFiltersCount()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                MaterialTheme.colorScheme.background,
            )
            .padding(top = 16.dp, start = 16.dp, end = 16.dp),
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
                                if (activeFiltersCount > 0) {
                                    FilterBadge(
                                        count =
                                            activeFiltersCount,
                                    )
                                }
                            },
                        ) {
                            ReelyIconButton(
                                icon = Lucide.Funnel,
                                onClick = onClick,
                            )
                        }
                    },
                    content = {
                        ReelyButton(
                            text = "Wyczyść filtry",
                            overrideContainerColor =
                                MaterialTheme.colorScheme
                                    .background
                                    .copy(alpha = 0.6f),
                            onClick = {
                                viewModel.clearFilters()
                                filterExpanded = false
                            },
                        )

                        Spacer(Modifier.height(8.dp))

                        HorizontalDivider()

                        Spacer(Modifier.height(12.dp))

                        GenreFilters(
                            genres = state.genres,
                            selectedGenreIds =
                                state.selectedGenreIds,
                            onToggle =
                                viewModel::toggleGenre,
                        )

                        Spacer(Modifier.height(16.dp))

                        HorizontalDivider()

                        Spacer(Modifier.height(12.dp))

                        ProviderFilters(
                            providerFilter = state.providerFilter,
                            providers = state.availableProviders,
                            myProviderIds = state.myProviderIds,
                            unsyncedMoviesCount = state.unsyncedMoviesCount,
                            isRefreshingProviders = state.isRefreshingProviders,
                            providerMessage = state.providerMessage,
                            onEnabledChange = viewModel::setProviderFilterEnabled,
                            onScopeSelected = viewModel::selectProviderScope,
                            onProviderToggle = viewModel::toggleFilterProvider,
                            onTypeToggle = viewModel::toggleFilterMonetizationType,
                            onRefresh = viewModel::refreshProviders,
                        )
                    },
                )

                Spacer(Modifier.width(8.dp))

                ReelyIconButton(
                    onClick =
                        viewModel::toggleDisplayStyle,
                    icon = state.displayStyle.icon,
                )
            }

            if (
                state.providerFilter.isEnabled &&
                state.unsyncedMoviesCount > 0
            ) {
                Spacer(Modifier.height(8.dp))

                Text(
                    text = "Nie sprawdzono dostępności dla " +
                            "${state.unsyncedMoviesCount} filmów.",
                    style =
                        MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme
                        .onBackground
                        .copy(alpha = 0.6f),
                )
            }

            state.providerMessage?.let { message ->
                Spacer(Modifier.height(8.dp))

                Text(
                    text = message,
                    style =
                        MaterialTheme.typography.bodySmall,
                    color =
                        MaterialTheme.colorScheme.primary,
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
private fun FilterBadge(
    count: Int,
) {
    Box(
        modifier = Modifier
            .offset(
                x = 2.dp,
                y = (-2).dp,
            )
            .background(
                color =
                    MaterialTheme.colorScheme.primary,
                shape =
                    RoundedCornerShape(100.dp),
            )
            .size(20.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = count.toString(),
            style =
                MaterialTheme.typography.labelSmall,
            color =
                MaterialTheme.colorScheme.onPrimary,
        )
    }
}

@Composable
private fun ProviderFilters(
    providerFilter: MovieProviderFilter,
    providers: List<WatchProvider>,
    myProviderIds: Set<Int>,
    unsyncedMoviesCount: Int,
    isRefreshingProviders: Boolean,
    providerMessage: String?,
    onEnabledChange: (Boolean) -> Unit,
    onScopeSelected: (ProviderFilterScope) -> Unit,
    onProviderToggle: (Int) -> Unit,
    onTypeToggle: (WatchMonetizationType) -> Unit,
    onRefresh: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment =
                Alignment.CenterVertically,
        ) {
            Text(
                text = "Dostępność",
                style =
                    MaterialTheme.typography.labelMedium,
            )

            Spacer(Modifier.weight(1f))

            ReelyChip(
                text = if (
                    providerFilter.isEnabled
                ) {
                    "Włączony"
                } else {
                    "Wyłączony"
                },
                selected =
                    providerFilter.isEnabled,
                onClick = {
                    onEnabledChange(
                        !providerFilter.isEnabled
                    )
                },
            )
        }

        if (providerFilter.isEnabled) {
            Spacer(Modifier.height(12.dp))

            ProviderScopeFilters(
                selectedScope =
                    providerFilter.scope,
                onScopeSelected =
                    onScopeSelected,
            )

            Spacer(Modifier.height(16.dp))

            MonetizationTypeFilters(
                selectedTypes =
                    providerFilter
                        .monetizationTypes,
                onToggle =
                    onTypeToggle,
            )

            if (
                providerFilter
                    .monetizationTypes
                    .isEmpty()
            ) {
                Spacer(Modifier.height(8.dp))

                FilterWarning(
                    text = "Wybierz co najmniej jeden " +
                            "rodzaj dostępu.",
                )
            }

            when (providerFilter.scope) {
                ProviderFilterScope.ANY_PROVIDER -> {
                }

                ProviderFilterScope.MY_PROVIDERS -> {
                    Spacer(Modifier.height(16.dp))

                    if (myProviderIds.isEmpty()) {
                        FilterWarning(
                            text = "Nie wybrano jeszcze " +
                                    "Waszych serwisów w ustawieniach.",
                        )
                    } else {
                        Text(
                            text = "Wybrane w ustawieniach: " +
                                    myProviderIds.size,
                            style =
                                MaterialTheme.typography
                                    .bodySmall,
                            color =
                                MaterialTheme.colorScheme
                                    .onBackground
                                    .copy(alpha = 0.6f),
                        )
                    }
                }

                ProviderFilterScope.SELECTED_PROVIDERS -> {
                    Spacer(Modifier.height(16.dp))

                    ProviderSelectionFilters(
                        providers = providers,
                        selectedProviderIds =
                            providerFilter
                                .selectedProviderIds,
                        onToggle =
                            onProviderToggle,
                    )

                    if (
                        providerFilter
                            .selectedProviderIds
                            .isEmpty()
                    ) {
                        Spacer(Modifier.height(8.dp))

                        FilterWarning(
                            text = "Wybierz co najmniej " +
                                    "jeden serwis.",
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        HorizontalDivider()

        Spacer(Modifier.height(12.dp))

        Text(
            text = if (unsyncedMoviesCount > 0) {
                "Dostępność nie została sprawdzona " +
                        "dla $unsyncedMoviesCount filmów."
            } else {
                "Dostępność filmów jest aktualna."
            },
            style =
                MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme
                .onBackground
                .copy(alpha = 0.6f),
        )

        Spacer(Modifier.height(8.dp))

        Row(
            verticalAlignment =
                Alignment.CenterVertically,
            horizontalArrangement =
                Arrangement.spacedBy(8.dp),
        ) {
            ReelyButton(
                text = if (isRefreshingProviders) {
                    "Odświeżanie..."
                } else {
                    "Odśwież dostępność"
                },
                overrideContainerColor =
                    MaterialTheme.colorScheme
                        .background
                        .copy(alpha = 0.6f),
                onClick = {
                    if (!isRefreshingProviders) {
                        onRefresh()
                    }
                },
            )

            if (isRefreshingProviders) {
                CircularProgressIndicator(
                    modifier =
                        Modifier.size(20.dp),
                    strokeWidth = 2.dp,
                )
            }
        }

        providerMessage?.let { message ->
            Spacer(Modifier.height(8.dp))

            Text(
                text = message,
                style =
                    MaterialTheme.typography.bodySmall,
                color =
                    MaterialTheme.colorScheme.primary,
            )
        }
    }
}

@Composable
private fun ProviderScopeFilters(
    selectedScope: ProviderFilterScope,
    onScopeSelected: (
        ProviderFilterScope
    ) -> Unit,
) {
    Column {
        Text(
            text = "Gdzie film ma być dostępny?",
            style =
                MaterialTheme.typography.labelMedium,
        )

        Spacer(Modifier.height(8.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(
                    rememberScrollState()
                ),
            horizontalArrangement =
                Arrangement.spacedBy(8.dp),
        ) {
            ProviderFilterScope.entries.forEach {
                    scope ->

                ReelyChip(
                    text = scope.label(),
                    selected =
                        scope == selectedScope,
                    onClick = {
                        onScopeSelected(scope)
                    },
                )
            }
        }
    }
}

private fun ProviderFilterScope.label(): String {
    return when (this) {
        ProviderFilterScope.ANY_PROVIDER ->
            "Dowolny serwis"

        ProviderFilterScope.MY_PROVIDERS ->
            "Wasze serwisy"

        ProviderFilterScope.SELECTED_PROVIDERS ->
            "Wybierz serwisy"
    }
}

@Composable
private fun MonetizationTypeFilters(
    selectedTypes:
    Set<WatchMonetizationType>,
    onToggle: (
        WatchMonetizationType
    ) -> Unit,
) {
    Column {
        Text(
            text = "Rodzaj dostępu",
            style =
                MaterialTheme.typography.labelMedium,
        )

        Spacer(Modifier.height(8.dp))

        val rowsCount = 2

        val typeRows = remember {
            List(rowsCount) { rowIndex ->
                WatchMonetizationType.entries
                    .filterIndexed { index, _ ->
                        index % rowsCount == rowIndex
                    }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(
                    rememberScrollState()
                ),
        ) {
            Column(
                verticalArrangement =
                    Arrangement.spacedBy(8.dp),
            ) {
                typeRows.forEach { rowTypes ->
                    Row(
                        horizontalArrangement =
                            Arrangement.spacedBy(8.dp),
                    ) {
                        rowTypes.forEach { type ->
                            ReelyChip(
                                text =
                                    type.displayName,
                                selected =
                                    type in selectedTypes,
                                onClick = {
                                    onToggle(type)
                                },
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ProviderSelectionFilters(
    providers: List<WatchProvider>,
    selectedProviderIds: Set<Int>,
    onToggle: (Int) -> Unit,
) {
    Column {
        Text(
            text = "Serwisy",
            style =
                MaterialTheme.typography.labelMedium,
        )

        Spacer(Modifier.height(8.dp))

        if (providers.isEmpty()) {
            FilterWarning(
                text = "Brak pobranego katalogu serwisów. " +
                        "Użyj przycisku odświeżania.",
            )

            return
        }

        val sortedProviders = remember(providers) {
            providers.sortedWith(
                compareBy<WatchProvider> { provider ->
                    provider.displayPriority
                        ?: Int.MAX_VALUE
                }.thenBy { provider ->
                    provider.providerName.lowercase()
                }
            )
        }

        val rowsCount = 2

        val providerRows =
            remember(sortedProviders) {
                List(rowsCount) { rowIndex ->
                    sortedProviders.filterIndexed {
                            index,
                            _,
                        ->
                        index % rowsCount == rowIndex
                    }
                }
            }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(
                    rememberScrollState()
                ),
        ) {
            Column(
                verticalArrangement =
                    Arrangement.spacedBy(8.dp),
            ) {
                providerRows.forEach { rowProviders ->
                    Row(
                        horizontalArrangement =
                            Arrangement.spacedBy(8.dp),
                    ) {
                        rowProviders.forEach { provider ->
                            ReelyChip(
                                text =
                                    provider.providerName,
                                selected =
                                    provider.providerId in
                                            selectedProviderIds,
                                onClick = {
                                    onToggle(
                                        provider.providerId
                                    )
                                },
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FilterWarning(
    text: String,
) {
    Text(
        text = text,
        style =
            MaterialTheme.typography.bodySmall,
        color =
            MaterialTheme.colorScheme.error,
    )
}

@Composable
private fun GenreFilters(
    genres: List<Genre> = emptyList(),
    selectedGenreIds: Set<Int> = emptySet(),
    onToggle: (Genre) -> Unit
){
    Column {
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
        return "Nie znaleziono filmu " +
                "„${state.searchQuery.trim()}”."
    }

    val filtersAreActive =
        state.selectedGenreIds.isNotEmpty() ||
                state.providerFilter.isEnabled

    if (filtersAreActive) {
        return if (
            state.providerFilter.isEnabled &&
            state.unsyncedMoviesCount > 0
        ) {
            "Brak filmów spełniających filtry. " +
                    "Dostępność części filmów nie została " +
                    "jeszcze sprawdzona."
        } else {
            "Brak filmów spełniających wybrane filtry."
        }
    }

    return when (state.selectedTab) {
        MovieListTab.WATCHLIST ->
            "Watchlista jest pusta."

        MovieListTab.WATCHED ->
            "Brak obejrzanych filmów."
    }
}

private fun ListScreenUiState.activeFiltersCount(): Int {
    val genreFiltersCount =
        selectedGenreIds.size

    val providerFiltersCount =
        if (providerFilter.isEnabled) {
            1
        } else {
            0
        }

    return genreFiltersCount +
            providerFiltersCount
}