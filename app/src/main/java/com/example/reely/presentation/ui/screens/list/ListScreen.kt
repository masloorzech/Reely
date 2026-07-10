package com.example.reely.presentation.ui.screens.list

import android.media.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.composables.icons.lucide.ArrowDown
import com.composables.icons.lucide.ArrowDownAZ
import com.composables.icons.lucide.ArrowUp
import com.composables.icons.lucide.Bookmark
import com.composables.icons.lucide.Clapperboard
import com.composables.icons.lucide.Clock
import com.composables.icons.lucide.Grid2x2
import com.composables.icons.lucide.Grid3x2
import com.composables.icons.lucide.Grip
import com.composables.icons.lucide.List
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Star
import com.composables.icons.lucide.TvMinimalPlay
import com.example.reely.domain.model.Movie
import com.example.reely.presentation.theme.ReelyTheme
import com.example.reely.presentation.ui.components.ReelyIconButton
import com.example.reely.presentation.ui.components.SmallSectionHeader
import com.example.reely.presentation.ui.components.ReelyListTabs
import com.example.reely.presentation.ui.components.ReelyMenuButton
import com.example.reely.presentation.ui.components.ReelySearchField
import com.example.reely.presentation.ui.components.ReelyTab
import com.example.reely.presentation.ui.components.TopBar
import com.example.reely.presentation.ui.screens.list.components.MovieList
import com.example.reely.presentation.ui.screens.search.components.MovieGrid
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


enum class MovieListTab(
    override val tabTitle: String,
    override val tabIcon: ImageVector?
) : ReelyTab {

    WATCHLIST(
        tabTitle = "Do obejrzenia",
        tabIcon = Lucide.TvMinimalPlay
    ),
    WATCHED(
        tabTitle = "Obejrzane",
        tabIcon = Lucide.Clapperboard
    )
}

enum class MovieSortOption(
    val label: String,
    val icon: ImageVector
) {
    RATING_DESC(
        label = "Najlepiej oceniane",
        icon = Lucide.Star
    ),
    NEWEST(
        label = "Najnowsze",
        icon = Lucide.ArrowDown
    ),
    OLDEST(
        label = "Najstarsze",
        icon = Lucide.ArrowUp
    ),
    TITLE_ASC(
        label = "Alfabetycznie",
        icon = Lucide.ArrowDownAZ
    ),
    ADDING_TIME(
        label = "Ostatnio dodane",
        icon = Lucide.Clock
    )
}
enum class MovieDisplayStyle(
    val icon: ImageVector
) {
    GRID(
        Lucide.Grid3x2
    ),
    LIST(
        Lucide.List
    )
}

@Composable
fun ListScreen(
    modifier : Modifier = Modifier,
    vm : ListScreenViewModel = hiltViewModel(),
    onMovieClick: (movieId: Int) -> Unit = {}
) {
    val lifecycleOwner = LocalLifecycleOwner.current

    val state = vm.state.collectAsState().value

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                vm.refresh()
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {

            TopBar(
                sectionName = "Twoje listy"
            )

            Spacer(modifier = Modifier.height(16.dp))

            ReelyListTabs(
                tabs = MovieListTab.entries,
                selectedTab = state.selectedTab,
                onTabSelected = vm::switchTab
            )

            Spacer(modifier = Modifier.height(16.dp))

            ReelySearchField(
                value = state.searchQuery,
                onValueChange = vm::changeSearchQuery,
                placeholder = "Wyszukaj film w sekcji ${state.selectedTab.tabTitle.lowercase()}...",
                onSearchClick = {
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row() {
                ReelyMenuButton(
                    selectedOption = state.selectedSort,
                    options = MovieSortOption.entries,
                    optionLabel = { it.label },
                    optionIcon = { it.icon },
                    onOptionSelected = { vm.switchSort(it)}
                )
                Spacer(Modifier.weight(1f))
                ReelyIconButton(
                    onClick = {vm.toggleDisplayStyle()},
                    icon = state.displayStyle.icon
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            when (state.displayStyle){
                MovieDisplayStyle.GRID -> {
                    MovieGrid(
                        movies = state.moviesList,
                        isLoading = state.isLoading,
                        onMovieClick = { movie ->
                            onMovieClick(movie.id)
                        }
                    )
                }
                MovieDisplayStyle.LIST -> {
                    MovieList(
                        movies = state.moviesList,
                        onMovieClick = { movie ->
                        onMovieClick(movie.id)}
                    )
                }

            }


        }
        Row(
            modifier = Modifier
                .height(64.dp)
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            MaterialTheme.colorScheme.background,
                        )
                    ),
                ),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ){}
    }
}