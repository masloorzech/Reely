package com.example.reely.presentation.ui.screens.movies


import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.core.tween
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ContentTransform
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
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
import com.example.reely.domain.model.MovieDetails
import com.example.reely.domain.model.MovieRating
import com.example.reely.domain.model.MovieViewing
import com.example.reely.domain.model.Profile
import com.example.reely.domain.model.ProfilePosition
import com.example.reely.domain.model.WatchProvider
import com.example.reely.presentation.ui.components.ReelyButton
import com.example.reely.presentation.ui.components.ReelyIconButton
import com.example.reely.presentation.ui.components.ReelyListTabs
import com.example.reely.presentation.ui.components.ReelyTab
import com.example.reely.presentation.ui.components.SmallSectionHeader
import com.example.reely.presentation.ui.genresText
import com.example.reely.presentation.ui.runtimeText
import com.example.reely.presentation.ui.screens.movies.components.MarkAsWatchedDialog
import com.example.reely.presentation.ui.screens.movies.components.MovieCastSection
import com.example.reely.presentation.ui.screens.movies.components.MovieCrewSection
import com.example.reely.presentation.ui.screens.movies.components.PosterHeader
import com.example.reely.presentation.ui.screens.movies.components.StarRating
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.collections.sortedBy

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

private enum class MovieAction {
    Hidden,
    RemoveFromWatched,
    RemoveFromWatchlist
}

@Composable
fun MovieDetailsScreen(
    onBack: () -> Unit = {},
    viewModel: MovieDetailsScreenViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val movie = state.movie

    val movieAction = when {
        movie == null -> MovieAction.Hidden

        state.isWatched ->
            MovieAction.RemoveFromWatched

        state.isOnWatchlist ->
            MovieAction.RemoveFromWatchlist

        else ->
            MovieAction.Hidden
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        Column {
            PosterHeader(
                isLoading = state.isMovieLoading,
                posterPath = movie?.posterPath,
                title = movie?.title,
                releaseDate = movie?.releaseDate?.toString()
                    ?: "Data nieznana",
                duration = movie?.runtimeText(),
                genres = movie?.genresText(),
            )

            Spacer(Modifier.height(8.dp))

            if (movie != null) {
                MovieDetailsContent(
                    movie = movie,
                    viewings = state.viewings,
                    isMovieProvidersLoading =
                        state.isMovieProvidersLoading,
                    availableProviderSections =
                        state.availableProviderSections,
                    selectedProviderSection =
                        state.selectedProviderSection,
                    selectedProviders =
                        state.selectedProviders,
                    onProviderSectionSelected =
                        viewModel::changeProvidersType,
                    cast = state.movieCredits?.cast,
                    crew = state.movieCredits?.crew,
                    isCastLoading =
                        state.isMovieCreditsLoading,
                ) {
                    when {
                        state.isWatched -> {
                            WatchedTopSection(
                                movie = movie,
                                profiles = state.profiles,
                                ratings = state.ratings,
                                averageUserRating =
                                    state.averageUserRating,
                                isSavingRatings =
                                    state.isSavingRatings,
                                onEditRatingsClick =
                                    viewModel::showEditRatingsDialog,
                            )
                        }

                        state.isOnWatchlist -> {
                            WatchlistTopSection(
                                movie = movie,
                                isLoading =
                                    state.isSavingRatings,
                                onMarkAsWatchedClick =
                                    viewModel::showRecordViewingDialog,
                            )
                        }

                        else -> {
                            NotWatchedTopSection(
                                movie = movie,
                                isLoading =
                                    state.isAddingToWatchlist,
                                onAddToWatchlistClick =
                                    viewModel::addToWatchlist,
                            )
                        }
                    }
                }
            }
        }

        MovieDetailsTopActions(
            movieAction = movieAction,
            state = state,
            onBack = onBack,
            onRemoveFromWatched =
                viewModel::removeFromWatched,
            onRemoveFromWatchlist =
                viewModel::removeFromWatchlist,
        )

        state.error?.let { error ->
            Text(
                text = "Coś poszło nie tak.\nPowód: $error",
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        MaterialTheme.colorScheme.errorContainer,
                    )
                    .padding(
                        horizontal = 16.dp,
                        vertical = 10.dp,
                    ),
                style = MaterialTheme.typography.bodySmall,
                color =
                    MaterialTheme.colorScheme.onErrorContainer,
                textAlign = TextAlign.Center,
            )
        }

        state.ratingDialogMode?.let { mode ->
            MarkAsWatchedDialog(
                profiles = state.profiles,
                initialWatchedAt = when (mode) {
                    RatingDialogMode.RECORD_VIEWING ->
                        LocalDate.now()

                    RatingDialogMode.EDIT_RATINGS ->
                        state.latestWatchedAt
                            ?: LocalDate.now()
                },
                initialFirstRating = state.ratingFor(
                    ProfilePosition.FIRST,
                ),
                initialSecondRating = state.ratingFor(
                    ProfilePosition.SECOND,
                ),
                showWatchedDate =
                    mode == RatingDialogMode.RECORD_VIEWING,
                isLoading = state.isSavingRatings,
                onDismiss = viewModel::hideRatingDialog,
                onConfirm =
                    viewModel::confirmRatingDialog,
            )
        }
    }
}

@Composable
private fun MovieDetailsTopActions(
    movieAction: MovieAction,
    state: MovieDetailsScreenState,
    onBack: () -> Unit,
    onRemoveFromWatched: () -> Unit,
    onRemoveFromWatchlist: () -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(16.dp),
    ) {
        ReelyIconButton(
            icon = Lucide.ArrowLeft,
            onClick = onBack,
        )

        Spacer(Modifier.weight(1f))

        AnimatedContent(
            targetState = movieAction,
            contentAlignment = Alignment.CenterEnd,
            transitionSpec = {
                ContentTransform(
                    targetContentEnter =
                        fadeIn(
                            animationSpec = tween(220),
                        ) + scaleIn(
                            animationSpec = tween(220),
                            initialScale = 0.92f,
                        ),
                    initialContentExit =
                        fadeOut(
                            animationSpec = tween(140),
                        ) + scaleOut(
                            animationSpec = tween(140),
                            targetScale = 0.92f,
                        ),
                    sizeTransform =
                        SizeTransform(clip = false),
                )
            },
            label = "movieActionButton",
        ) { action ->
            when (action) {
                MovieAction.RemoveFromWatched -> {
                    ReelyButton(
                        text = if (
                            state.isRemovingFromWatched
                        ) {
                            "Usuwanie..."
                        } else {
                            "Usuń z obejrzanych"
                        },
                        enabled =
                            !state.isRemovingFromWatched,
                        onClick = onRemoveFromWatched,
                        overrideContainerColor =
                            MaterialTheme.colorScheme.surface,
                        leadingIcon = {
                            Icon(
                                imageVector = Lucide.Trash,
                                contentDescription = null,
                                tint = MaterialTheme
                                    .colorScheme
                                    .onBackground,
                                modifier =
                                    Modifier.size(16.dp),
                            )
                        },
                    )
                }

                MovieAction.RemoveFromWatchlist -> {
                    ReelyButton(
                        text = if (
                            state.isRemovingFromWatchlist
                        ) {
                            "Usuwanie..."
                        } else {
                            "Usuń z listy"
                        },
                        enabled =
                            !state.isRemovingFromWatchlist,
                        onClick = onRemoveFromWatchlist,
                        overrideContainerColor =
                            MaterialTheme.colorScheme.surface,
                        leadingIcon = {
                            Icon(
                                imageVector = Lucide.Trash,
                                contentDescription = null,
                                tint = MaterialTheme
                                    .colorScheme
                                    .onBackground,
                                modifier =
                                    Modifier.size(16.dp),
                            )
                        },
                    )
                }

                MovieAction.Hidden -> {
                    Spacer(Modifier.width(1.dp))
                }
            }
        }
    }
}

@Composable
private fun MovieDetailsContent(
    movie: MovieDetails,
    viewings: List<MovieViewing>,
    isMovieProvidersLoading: Boolean,
    availableProviderSections:
    List<ProviderSectionType>,
    selectedProviderSection:
    ProviderSectionType,
    cast: List<CastMember>?,
    crew: List<CrewMember>?,
    isCastLoading: Boolean,
    selectedProviders: List<WatchProvider>,
    onProviderSectionSelected:
        (ProviderSectionType) -> Unit,
    topSection: @Composable ColumnScope.() -> Unit,
) {
    val verticalScroll = rememberScrollState()
    val density = LocalDensity.current

    val showTopGradient by remember {
        derivedStateOf {
            verticalScroll.value >
                    with(density) { 8.dp.roundToPx() }
        }
    }

    Box {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .verticalScroll(verticalScroll),
        ) {
            topSection()

            if (viewings.isNotEmpty()) {
                Spacer(Modifier.height(16.dp))

                ViewingHistorySection(
                    viewings = viewings,
                )
            }

            Spacer(Modifier.height(16.dp))

            WatchProvidersSection(
                isLoading = isMovieProvidersLoading,
                sections = availableProviderSections,
                selectedSection =
                    selectedProviderSection,
                selectedProviders = selectedProviders,
                onSectionSelected =
                    onProviderSectionSelected,
            )

            Spacer(Modifier.height(16.dp))

            MovieDescriptionSection(
                description = movie.overview,
            )

            Spacer(Modifier.height(16.dp))

            MovieCastSection(
                cast = cast,
                isLoading = isCastLoading,
            )

            Spacer(Modifier.height(16.dp))

            MovieCrewSection(
                crew = crew,
                isLoading = isCastLoading,
            )

            Spacer(Modifier.height(32.dp))
        }

        MovieDetailsTopGradient(
            visible = showTopGradient,
            modifier = Modifier.align(
                Alignment.TopCenter,
            ),
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
        ){}
    }
}

@Composable
private fun NotWatchedTopSection(
    movie: MovieDetails,
    isLoading: Boolean,
    onAddToWatchlistClick: () -> Unit,
) {
    GlobalRatingSection(
        averageRating = movie.publicRating,
        userCount = movie.publicVoteCount,
    )

    Spacer(Modifier.height(16.dp))

    ReelyButton(
        leadingIcon = {
            Icon(
                imageVector = Lucide.TvMinimalPlay,
                tint = Color.White,
                contentDescription = null,
            )
        },
        text = if (isLoading) {
            "Dodawanie..."
        } else {
            "Dodaj do listy"
        },
        enabled = !isLoading,
        onClick = onAddToWatchlistClick,
    )
}

@Composable
private fun WatchlistTopSection(
    movie: MovieDetails,
    isLoading: Boolean,
    onMarkAsWatchedClick: () -> Unit,
) {
    GlobalRatingSection(
        averageRating = movie.publicRating,
        userCount = movie.publicVoteCount,
    )

    Spacer(Modifier.height(16.dp))

    ReelyButton(
        leadingIcon = {
            Icon(
                imageVector = Lucide.TvMinimalPlay,
                tint = Color.White,
                contentDescription = null,
            )
        },
        text = "Obejrzany",
        enabled = !isLoading,
        onClick = onMarkAsWatchedClick,
    )
}

@Composable
private fun WatchedTopSection(
    movie: MovieDetails,
    profiles: List<Profile>,
    ratings: List<MovieRating>,
    averageUserRating: Double?,
    isSavingRatings: Boolean,
    onEditRatingsClick: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment =
            Alignment.CenterHorizontally,
    ) {
        averageUserRating?.let { average ->
            Row(
                verticalAlignment =
                    Alignment.CenterVertically,
            ) {
                Text(
                    text = String.format(
                        Locale.US,
                        "%.1f",
                        average,
                    ),
                    style =
                        MaterialTheme.typography.labelLarge,
                    color =
                        MaterialTheme.colorScheme.onBackground,
                )

                Spacer(Modifier.width(8.dp))

                StarRating(
                    score = average,
                )
            }
        }

        if (
            movie.publicRating != null &&
            movie.publicVoteCount != null
        ) {
            Spacer(Modifier.height(8.dp))

            Text(
                text = String.format(
                    Locale.US,
                    "Globalne oceny: %.1f · %,d ocen",
                    movie.publicRating,
                    movie.publicVoteCount,
                ),
                style =
                    MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme
                    .onBackground
                    .copy(alpha = 0.6f),
            )
        }
    }

    Spacer(Modifier.height(16.dp))

    SmallSectionHeader(
        sectionName = "Wasze oceny",
    )

    Spacer(Modifier.height(8.dp))

    Column(
        verticalArrangement =
            Arrangement.spacedBy(8.dp),
    ) {
        profiles
            .sortedBy { profile ->
                profile.position.ordinal
            }
            .forEach { profile ->
                val rating = ratings
                    .firstOrNull { movieRating ->
                        movieRating.profileId == profile.id
                    }
                    ?.rating

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            MaterialTheme.colorScheme.surface,
                        )
                        .padding(
                            horizontal = 12.dp,
                            vertical = 10.dp,
                        ),
                    verticalAlignment =
                        Alignment.CenterVertically,
                ) {
                    Text(
                        text = profile.name,
                        style =
                            MaterialTheme.typography.labelLarge,
                        color =
                            MaterialTheme.colorScheme.onSurface,
                    )

                    Spacer(Modifier.weight(1f))

                    Text(
                        text = rating?.let {
                            "${it.value}/10"
                        } ?: "Brak oceny",
                        style =
                            MaterialTheme.typography.labelLarge,
                        color = MaterialTheme
                            .colorScheme
                            .tertiary,
                    )
                }
            }
    }

    Spacer(Modifier.height(16.dp))

    ReelyButton(
        text = if (isSavingRatings) {
            "Zapisywanie..."
        } else {
            "Edytuj oceny"
        },
        enabled = !isSavingRatings,
        onClick = onEditRatingsClick,
    )
}

@Composable
private fun ViewingHistorySection(
    viewings: List<MovieViewing>,
) {
    SmallSectionHeader(
        sectionName = "Historia oglądania",
    )

    Spacer(Modifier.height(8.dp))

    Column(
        verticalArrangement =
            Arrangement.spacedBy(8.dp),
    ) {
        viewings
            .sortedWith(
                compareByDescending<MovieViewing> {
                    it.watchedAt
                }.thenByDescending {
                    it.id
                },
            )
            .forEachIndexed { index, viewing ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            MaterialTheme.colorScheme.surface,
                        )
                        .padding(
                            horizontal = 12.dp,
                            vertical = 10.dp,
                        ),
                    verticalAlignment =
                        Alignment.CenterVertically,
                ) {
                    Column(
                        verticalArrangement =
                            Arrangement.spacedBy(2.dp),
                    ) {
                        Text(
                            text = if (index == 0) {
                                "Ostatnie oglądanie"
                            } else {
                                "Poprzednie oglądanie"
                            },
                            style = MaterialTheme
                                .typography
                                .labelMedium,
                            color = MaterialTheme
                                .colorScheme
                                .onSurface,
                        )

                        Text(
                            text = viewing.watchedAt
                                .toPolishDate(),
                            style = MaterialTheme
                                .typography
                                .bodySmall,
                            color = MaterialTheme
                                .colorScheme
                                .onSurface
                                .copy(alpha = 0.6f),
                        )
                    }
                }
            }
    }
}

fun LocalDate.toPolishDate(): String {
    return format(
        DateTimeFormatter.ofPattern(
            "d MMMM yyyy",
            Locale.forLanguageTag("pl-PL"),
        )
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
                            model = provider.logoPath,
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