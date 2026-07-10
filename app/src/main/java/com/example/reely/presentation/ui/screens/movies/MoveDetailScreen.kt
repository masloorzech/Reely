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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.composables.icons.lucide.ArrowLeft
import com.composables.icons.lucide.Clock
import com.composables.icons.lucide.Gift
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Megaphone
import com.composables.icons.lucide.ShoppingCart
import com.composables.icons.lucide.Trash
import com.composables.icons.lucide.Tv
import com.composables.icons.lucide.TvMinimalPlay
import com.example.reely.domain.model.CastMember
import com.example.reely.domain.model.CrewMember
import com.example.reely.domain.model.Movie
import com.example.reely.domain.model.User
import com.example.reely.domain.model.UserStatsItem
import com.example.reely.domain.model.WatchProvider
import com.example.reely.domain.model.genresText
import com.example.reely.domain.model.getAvailablePosterPath
import com.example.reely.domain.model.getAvaliable
import com.example.reely.domain.model.isWatched
import com.example.reely.domain.model.ourAverageRatingRounded
import com.example.reely.domain.model.runtimeText
import com.example.reely.presentation.ui.components.RatingResults
import com.example.reely.presentation.ui.components.ReelyButton
import com.example.reely.presentation.ui.components.ReelyIconButton
import com.example.reely.presentation.ui.components.ReelyListTabs
import com.example.reely.presentation.ui.components.ReelyTab
import com.example.reely.presentation.ui.components.SmallSectionHeader
import com.example.reely.presentation.ui.formatWatchedDate
import com.example.reely.presentation.ui.model.UserRatingItems
import com.example.reely.presentation.ui.screens.home.components.OurStatsSection
import com.example.reely.presentation.ui.screens.movies.components.CastMemberItem
import com.example.reely.presentation.ui.screens.movies.components.CrewMemberItem
import com.example.reely.presentation.ui.screens.movies.components.MarkAsWatchedDialog
import com.example.reely.presentation.ui.screens.movies.components.PosterHeader
import com.example.reely.presentation.ui.screens.movies.components.StarRating
import com.example.reely.presentation.ui.screens.movies.toCrewUiItems
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

    val stats by viewModel.stats.collectAsStateWithLifecycle()

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
                    onProviderSectionSelected = viewModel::changeProvidersType,
                    cast = state.movieCredits?.cast,
                    crew = state.movieCredits?.crew,
                    isCastLoading = state.isMovieCreditsLoading
                ) {
                    when {
                        movie.isWatched() -> {
                            WatchedTopSection(
                                movie = movie,
                                users = state.users,
                            )
                        }

                        movie.isInWatchlist -> {
                            WatchlistTopSection(
                                movie = movie,
                                onMarkAsWatchedClick = viewModel::showRatingDialog,
                            )
                        }

                        else -> {
                            NotWatchedTopSection(
                                movie = movie,
                                onAddToWatchlistClick = viewModel::addToWatchlist
                            )
                        }
                    }
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(16.dp)
        ) {
            ReelyIconButton(
                icon = Lucide.ArrowLeft,
                onClick = onBack
            )
            Spacer(
                Modifier.weight(1f)
            )
            if (movie!=null) {
                when {
                    movie.isWatched() -> {
                        ReelyButton(
                            text = "Usuń z obejrzanych",
                            onClick = {
                                viewModel.removeFromWatched()
                            },
                            overrideContainerColor = MaterialTheme.colorScheme.surface,
                            leadingIcon = {
                                Icon(
                                    imageVector = Lucide.Trash,
                                    contentDescription = "Bin",
                                    tint = MaterialTheme.colorScheme.onBackground,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        )
                    }
                    movie.isInWatchlist -> {
                        ReelyButton(
                            text = "Usuń z listy",
                            onClick = {
                                viewModel.removeFromWatchlist()
                            },
                            overrideContainerColor = MaterialTheme.colorScheme.surface,
                            leadingIcon = {
                                Icon(
                                    imageVector = Lucide.Trash,
                                    contentDescription = "Bin",
                                    tint = MaterialTheme.colorScheme.onBackground,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        )
                    }
                }
            }
        }

        if (state.isRatingDialogVisible) {
            MarkAsWatchedDialog(
                users = state.users,
                initialRatings = movie?.userWatches
                    ?.mapValues { entry -> entry.value.rating }
                    .orEmpty(),
                isLoading = state.isMarkingAsWatched,
                onDismiss = viewModel::hideRatingDialog,
                onConfirm = viewModel::markAsWatched
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
    cast: List<CastMember>?,
    crew: List<CrewMember>?,
    isCastLoading: Boolean,
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

            MovieCastSection(
                cast = cast,
                isLoading = isCastLoading
            )

            MovieCrewSection(
                crew = crew,
                isLoading = isCastLoading
            )
        }

        MovieDetailsTopGradient(
            visible = showTopGradient,
            modifier = Modifier.align(Alignment.TopCenter)
        )
    }
}

@Composable
fun MovieCastSection(
    cast: List<CastMember>?,
    isLoading: Boolean,
    modifier: Modifier = Modifier
) {

    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        SmallSectionHeader(
            sectionName = "Obsada:"
        )

        Spacer(Modifier.height(8.dp))

        when {
            isLoading -> {
                Text(
                    text = "Ładowanie obsady...",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onBackground.copy(
                        alpha = 0.6f
                    )
                )
            }

            cast != null -> when{

                cast.isEmpty() -> {
                    Text(
                        text = "Brak informacji o obsadzie.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onBackground.copy(
                            alpha = 0.6f
                        )
                    )
                }

                else -> {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(
                            items = cast,
                            key = { member -> member.id }
                        ) { member ->
                            CastMemberItem(
                                castMember = member
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MovieCrewSection(
    crew: List<CrewMember>?,
    isLoading: Boolean,
    modifier: Modifier = Modifier
){
    val crewItems = remember(crew) {
        crew?.toCrewUiItems()
    }

    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        SmallSectionHeader(
            sectionName = "Twórcy:"
        )

        Spacer(Modifier.height(8.dp))

        when {
            isLoading -> {
                Text(
                    text = "Ładowanie twórców...",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onBackground.copy(
                        alpha = 0.6f
                    )
                )
            }

            crewItems != null -> when {

                crewItems.isEmpty() -> {
                    Text(
                        text = "Brak informacji o twórcach.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onBackground.copy(
                            alpha = 0.6f
                        )
                    )
                }

                else -> {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(
                            items = crewItems,
                            key = { item: CrewUiItem -> item.personId }
                        ) { item: CrewUiItem ->
                            CrewMemberItem(
                                item = item
                            )
                        }
                    }
                }
            }
        }
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
) {
    GlobalRatingSection(
        averageRating = movie.publicRating?.average,
        userCount = movie.publicRating?.voteCount
    )

    Spacer(Modifier.height(16.dp))

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

}

@Composable
private fun WatchedTopSection(
    movie: Movie,
    users: List<User>,
) {
    val usersById = users.associateBy { user ->
        user.id
    }

    Column(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        if (movie.userWatches.isNotEmpty()) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                RatingResults(
                    movie = movie,
                    style = MaterialTheme.typography.labelLarge
                )

                Spacer(Modifier.width(8.dp))


                StarRating(
                    score = movie.ourAverageRatingRounded()
                )

            }
        }

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
        textOverride = "Wasze oceny",
        userRatingItems = movie.toUserStatItemsForMovie(users)
    )

    Spacer(Modifier.height(16.dp))

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        movie.userWatches.entries.forEach { userData ->
            val user = usersById[userData.key]
            val watchedAt = userData.value.watchedAt
            val rating = userData.value.rating

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(
                        text = user?.nickname ?: "User ${userData.key.value}",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    Text(
                        text = watchedAt.formatWatchedDate(),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.55f)
                    )
                }

                Spacer(Modifier.weight(1f))

                if (rating != null) {
                    Text(
                        text = String.format(Locale.US, "%.1f", rating),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
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

    if (sections.isEmpty()) {
        SmallSectionHeader(
            sectionName = "Dostępność w Polsce:"
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = "Niestety ten film nie jest dostępny w Polsce, może następnym razem",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
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
                                .clip(RoundedCornerShape(12.dp)),
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