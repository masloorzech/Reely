package com.example.reely.presentation.ui.screens.movies

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.composables.icons.lucide.ArrowLeft
import com.composables.icons.lucide.Clock
import com.composables.icons.lucide.Gift
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Megaphone
import com.composables.icons.lucide.ShoppingCart
import com.composables.icons.lucide.Tv
import com.composables.icons.lucide.TvMinimalPlay
import com.composables.icons.lucide.X
import com.example.reely.domain.model.Movie
import com.example.reely.domain.model.WatchProvider
import com.example.reely.domain.model.genresText
import com.example.reely.domain.model.getAvailablePosterPath
import com.example.reely.domain.model.getAvaliable
import com.example.reely.domain.model.ourAverageRatingRounded
import com.example.reely.domain.model.runtimeText
import com.example.reely.presentation.ui.components.RatingResults
import com.example.reely.presentation.ui.components.ReelyButton
import com.example.reely.presentation.ui.components.ReelyIconButton
import com.example.reely.presentation.ui.components.ReelyListTabs
import com.example.reely.presentation.ui.components.ReelyTab
import com.example.reely.presentation.ui.components.SmallSectionHeader
import com.example.reely.presentation.ui.screens.home.components.OurStatsSection
import com.example.reely.presentation.ui.screens.movies.components.PosterHeader
import com.example.reely.presentation.ui.screens.movies.components.StarRating
import com.example.reely.presentation.ui.screens.movies.model.MovieDetailsUiMode
import com.example.reely.presentation.ui.screens.movies.model.detailsUiMode
import java.util.Locale

private enum class WatchProviderTab(
    val type: ProviderSectionType,
    override val tabTitle: String?,
    override val tabIcon: ImageVector?
) : ReelyTab {

    FLATRATE(ProviderSectionType.FLATRATE, null, Lucide.Tv),
    BUY(ProviderSectionType.BUY, null, Lucide.ShoppingCart),
    RENT(ProviderSectionType.RENT, null, Lucide.Clock),
    FREE(ProviderSectionType.FREE, null, Lucide.Gift),
    ADS(ProviderSectionType.ADS, null, Lucide.Megaphone);

    companion object {
        fun fromType(type: ProviderSectionType): WatchProviderTab {
            return entries.first { it.type == type }
        }
    }
}

@Composable
fun MovieDetailsScreen(
    onBack: () -> Unit = {},
    viewModel: MovieDetailsScreenViewModel = hiltViewModel()
) {
    val state = viewModel.state.collectAsState().value
    val movie = state.movie

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column {
            PosterHeader(
                isLoading = state.isMovieLoading,
                posterPath = movie?.getAvailablePosterPath(),
                title = movie?.title,
                releaseDate = movie?.releaseDate,
                duration = movie?.runtimeText(),
                genres = movie?.genresText()
            )

            Spacer(Modifier.height(8.dp))

            if (movie != null) {
                MovieDetailsContent(
                    movie = movie,
                    isMovieProvidersLoading = state.isMovieProvidersLoading,
                    availableProviderSections = state.availableProviderSections,
                    selectedProviderSection = state.selectedProviderSection,
                    selectedProviders = state.selectedProviders,
                    onProviderSectionSelected = viewModel::changeProvidersType
                ) {
                    when (movie.detailsUiMode()) {
                        MovieDetailsUiMode.NotWatched -> {
                            NotWatchedTopSection(
                                movie = movie,
                                onAddToWatchlistClick = viewModel::addToWatchlist
                            )
                        }

                        MovieDetailsUiMode.InWatchlist -> {
                            WatchlistTopSection(
                                movie = movie,
                                onMarkAsWatchedClick = viewModel::markAsWatched,
                                onRemoveFromWatchlistClick = viewModel::removeFromWatchlist
                            )
                        }

                        MovieDetailsUiMode.Watched -> {
                            WatchedTopSection(
                                movie = movie
                            )
                        }
                    }
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            ReelyIconButton(
                icon = Lucide.ArrowLeft,
                onClick = onBack
            )
        }
    }
}

@Composable
private fun MovieDetailsContent(
    movie: Movie,
    isMovieProvidersLoading: Boolean,
    availableProviderSections: List<ProviderSectionType>,
    selectedProviderSection: ProviderSectionType,
    selectedProviders: List<WatchProvider>,
    onProviderSectionSelected: (ProviderSectionType) -> Unit,
    topSection: @Composable ColumnScope.() -> Unit
) {
    val verticalScroll = rememberScrollState()
    val density = LocalDensity.current

    val showTopGradient by remember {
        derivedStateOf {
            verticalScroll.value > with(density) { 8.dp.roundToPx() }
        }
    }

    Box {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .verticalScroll(verticalScroll)
        ) {
            topSection()

            Spacer(Modifier.height(16.dp))

            WatchProvidersSection(
                isLoading = isMovieProvidersLoading,
                sections = availableProviderSections,
                selectedSection = selectedProviderSection,
                selectedProviders = selectedProviders,
                onSectionSelected = onProviderSectionSelected
            )

            Spacer(Modifier.height(16.dp))

            MovieDescriptionSection(
                description = movie.overview
            )

            Spacer(Modifier.height(32.dp))
        }

        MovieDetailsTopGradient(
            visible = showTopGradient,
            modifier = Modifier.align(Alignment.TopCenter)
        )
    }
}

@Composable
private fun MovieDetailsTopGradient(
    visible: Boolean,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(animationSpec = tween(durationMillis = 200)),
        exit = fadeOut(animationSpec = tween(durationMillis = 200)),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier
                .height(64.dp)
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.background,
                            Color.Transparent
                        )
                    )
                )
        ) {}
    }
}

@Composable
private fun NotWatchedTopSection(
    movie: Movie,
    onAddToWatchlistClick: () -> Unit
) {
    GlobalRatingSection(
        averageRating = movie.publicRating?.average,
        userCount = movie.publicRating?.voteCount
    )

    Spacer(Modifier.height(16.dp))

    Row {
        ReelyButton(
            leadingIcon = {
                Icon(
                    imageVector = Lucide.TvMinimalPlay,
                    tint = Color.White,
                    contentDescription = null
                )
            },
            text = "Dodaj do listy",
            onClick = onAddToWatchlistClick
        )
    }
}

@Composable
private fun WatchlistTopSection(
    movie: Movie,
    onMarkAsWatchedClick: () -> Unit,
    onRemoveFromWatchlistClick: () -> Unit
) {
    GlobalRatingSection(
        averageRating = movie.publicRating?.average,
        userCount = movie.publicRating?.voteCount
    )

    Spacer(Modifier.height(16.dp))

    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ReelyButton(
            leadingIcon = {
                Icon(
                    imageVector = Lucide.TvMinimalPlay,
                    tint = Color.White,
                    contentDescription = null
                )
            },
            text = "Obejrzany",
            onClick = onMarkAsWatchedClick
        )

        ReelyButton(
            text = "Usuń z listy",
            overrideContainerColor = MaterialTheme.colorScheme.surface,
            leadingIcon = {
                Icon(
                    imageVector = Lucide.X,
                    tint = Color.White,
                    contentDescription = null
                )
            },
            onClick = onRemoveFromWatchlistClick
        )
    }
}

@Composable
private fun WatchedTopSection(
    movie: Movie
) {
    Column(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        StarRating(
            score = movie.ourAverageRatingRounded()
        )

        Spacer(Modifier.height(8.dp))

        RatingResults(
            movie = movie,
            style = MaterialTheme.typography.labelLarge
        )

        movie.publicRating?.let { publicRating ->
            if (publicRating.average != null && publicRating.voteCount != null) {
                Spacer(Modifier.height(8.dp))

                Text(
                    text = String.format(
                        Locale.US,
                        "Globalne oceny: %.1f · %,d ocen",
                        publicRating.average,
                        publicRating.voteCount
                    ),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                )
            }
        }
    }

    Spacer(Modifier.height(8.dp))

    OurStatsSection(
        textOverride = null
    )

    Spacer(Modifier.height(16.dp))

    Text(
        text = "Obejrzany · przez",
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
    )
}

@Composable
private fun GlobalRatingSection(
    averageRating: Double?,
    userCount: Int?
) {
    StarRating(score = averageRating)

    if (averageRating != null && userCount != null) {
        Text(
            text = String.format(
                Locale.US,
                "Globalne oceny: %.1f · %,d ocen",
                averageRating,
                userCount
            ),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
        )
    }
}

@Composable
fun WatchProvidersSection(
    isLoading: Boolean,
    sections: List<ProviderSectionType>,
    selectedSection: ProviderSectionType,
    selectedProviders: List<WatchProvider>,
    onSectionSelected: (ProviderSectionType) -> Unit
) {
    if (isLoading) {
        SmallSectionHeader(
            sectionName = "Ładowanie dostępności..."
        )
        return
    }

    if (sections.isEmpty()) {
        SmallSectionHeader(
            sectionName = "Brak informacji o dostępności w Polsce"
        )
        return
    }

    val tabs = sections.map { WatchProviderTab.fromType(it) }

    val selectedTab = tabs.firstOrNull { it.type == selectedSection }
        ?: tabs.first()

    SmallSectionHeader(
        sectionName = "Dostępne w Polsce ${selectedTab.type.text}"
    )

    Spacer(Modifier.height(8.dp))

    ReelyListTabs(
        tabs = tabs,
        selectedTab = selectedTab,
        onTabSelected = { tab ->
            onSectionSelected(tab.type)
        }
    )

    Spacer(Modifier.height(8.dp))

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        selectedProviders.forEach { provider ->
            item(
                key = provider.providerId
            ) {
                Column(
                    modifier = Modifier.width(68.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.surface),
                        contentAlignment = Alignment.Center
                    ) {
                        AsyncImage(
                            model = provider.logoPaths?.getAvaliable(),
                            contentDescription = provider.providerName,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(5.dp)
                                .clip(RoundedCornerShape(9.dp)),
                            contentScale = ContentScale.Fit
                        )
                    }

                    Spacer(Modifier.height(4.dp))

                    Text(
                        text = provider.providerName,
                        modifier = Modifier.fillMaxWidth(),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.75f),
                        textAlign = TextAlign.Center,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        lineHeight = 12.sp
                    )
                }
            }
        }
    }
}

@Composable
fun MovieDescriptionSection(
    description: String?
) {
    SmallSectionHeader("O filmie:")

    Spacer(Modifier.height(8.dp))

    Text(
        text = description.takeUnless { it.isNullOrBlank() } ?: "Brak opisu.",
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
        lineHeight = 20.sp
    )
}