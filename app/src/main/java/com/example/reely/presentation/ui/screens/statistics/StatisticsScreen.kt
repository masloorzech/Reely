package com.example.reely.presentation.ui.screens.statistics

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.composables.icons.lucide.ChartBar
import com.composables.icons.lucide.Clapperboard
import com.composables.icons.lucide.Clock
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Star
import com.composables.icons.lucide.TvMinimalPlay
import com.example.reely.R
import com.example.reely.domain.model.GenreStatistics
import com.example.reely.domain.model.ProfileStatistics
import com.example.reely.domain.model.ReelyStatistics
import com.example.reely.domain.model.StatisticsMovie
import com.example.reely.presentation.ui.components.TopBar
import com.example.reely.presentation.ui.screens.profile.components.AvatarImage
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.ceil
import kotlin.math.max
import kotlin.math.roundToInt

@Composable
fun StatisticsScreen(
    modifier: Modifier = Modifier,
    viewModel: StatisticsScreenViewModel = hiltViewModel(),
    onMovieClick: (movieId: Int) -> Unit = {},
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                color = MaterialTheme.colorScheme.background
            ),
    ) {
        when {
            state.isLoading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                )
            }

            state.error != null -> {
                StatisticsError(
                    message = state.error.orEmpty(),
                    modifier = Modifier.align(Alignment.Center),
                )
            }

            state.statistics != null -> {
                StatisticsContent(
                    statistics = requireNotNull(state.statistics),
                    onMovieClick = onMovieClick,
                )
            }
        }
    }
}

@Composable
private fun StatisticsContent(
    statistics: ReelyStatistics,
    onMovieClick: (movieId: Int) -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = 16.dp,
            top = 16.dp,
            end = 16.dp,
            bottom = 112.dp,
        ),
        verticalArrangement = Arrangement.spacedBy(32.dp),
    ) {
        item {
            TopBar(
                sectionName = "Statystyki",
            )
        }

        if (!statistics.hasWatchedMovies) {
            item {
                EmptyStatisticsCard(
                    watchlistMovieCount = statistics.watchlistMovieCount,
                )
            }

            if (statistics.watchlistMovieCount > 0) {
                item {
                    WatchlistTimeSection(statistics)
                }
            }

            return@LazyColumn
        }

        item {
            StatisticsHeroCard(statistics)
        }

        item {
            StatisticsSummarySection(statistics)
        }

        if (statistics.profiles.isNotEmpty()) {
            item {
                ProfileRatingsSection(
                    profiles = statistics.profiles,
                )
            }
        }

        if (statistics.genres.isNotEmpty()) {
            item {
                MostWatchedGenresSection(
                    genres = statistics.genres,
                    totalUniqueMovies = statistics.uniqueMoviesCount,
                )
            }

            item {
                BestRatedGenresSection(
                    genres = statistics.genres,
                    profiles = statistics.profiles,
                )
            }
        }

        if (statistics.topTogetherMovies.isNotEmpty()) {
            item {
                TopMoviesSection(
                    movies = statistics.topTogetherMovies,
                    onMovieClick = onMovieClick,
                )
            }
        }

        item {
            CompatibilitySection(statistics)
        }

        if (statistics.monthlyActivity.isNotEmpty()) {
            item {
                MonthlyActivitySection(
                    statistics = statistics,
                )
            }
        }

        item {
            WatchlistTimeSection(statistics)
        }
    }
}

@Composable
private fun StatisticsHeroCard(
    statistics: ReelyStatistics,
) {
    val shape = RoundedCornerShape(28.dp)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(248.dp)
            .clip(shape)
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF1B1230),
                        Color(0xFF120D22),
                        Color(0xFF0B1020)
                    )
                )
            )
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = 48.dp, y = (-48).dp)
                .size(230.dp)
                .blur(36.dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.55f),
                            Color.Transparent,
                        ),
                    ),
                    shape = CircleShape,
                ),
        )

        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(x = 60.dp, y = 70.dp)
                .size(180.dp)
                .background(
                    color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.11f),
                    shape = CircleShape,
                ),
        )

        Column(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(0.72f)
                .padding(20.dp),
        ) {
            Text(
                text = "WASZ FILMOWY CZAS",
                modifier = Modifier
                    .background(
                        color = Color.White.copy(alpha = 0.10f),
                        shape = RoundedCornerShape(100.dp),
                    )
                    .padding(
                        horizontal = 10.dp,
                        vertical = 6.dp,
                    ),
                style = MaterialTheme.typography.labelSmall,
                color = Color.White.copy(alpha = 0.82f),
            )

            Spacer(Modifier.height(14.dp))

            Text(
                text = formatLongDuration(
                    statistics.totalWatchedMinutes,
                ),
                style = MaterialTheme.typography.headlineLarge,
                color = Color.White,
                maxLines = 1,
            )

            Spacer(Modifier.height(6.dp))

            Text(
                text = statistics.watchedTimeFunFact.headline,
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )

            Spacer(Modifier.height(4.dp))

            Text(
                text = statistics.watchedTimeFunFact.description,
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.66f),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )

            Spacer(Modifier.weight(1f))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                HeroMetricPill(
                    value = statistics.uniqueMoviesCount.toString(),
                    label = "filmów",
                )

                HeroMetricPill(
                    value = statistics.viewingsCount.toString(),
                    label = "seansów",
                )
            }
        }
    }
}

@Composable
private fun HeroMetricPill(
    value: String,
    label: String,
) {
    Row(
        modifier = Modifier
            .background(
                color = Color.Black.copy(alpha = 0.28f),
                shape = RoundedCornerShape(100.dp),
            )
            .padding(
                horizontal = 10.dp,
                vertical = 6.dp,
            ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.labelMedium,
            color = Color.White,
        )

        Spacer(Modifier.width(4.dp))

        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = Color.White.copy(alpha = 0.64f),
        )
    }
}

@Composable
private fun StatisticsSummarySection(
    statistics: ReelyStatistics,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        SectionHeader(
            title = "Wasze liczby",
            description = "Krótki zapis całej wspólnej filmowej historii.",
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            MetricCard(
                modifier = Modifier.weight(1f),
                icon = Lucide.Clapperboard,
                value = statistics.uniqueMoviesCount.toString(),
                label = "unikalnych filmów",
            )

            MetricCard(
                modifier = Modifier.weight(1f),
                icon = Lucide.TvMinimalPlay,
                value = statistics.viewingsCount.toString(),
                label = "wspólnych seansów",
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            MetricCard(
                modifier = Modifier.weight(1f),
                icon = Lucide.Star,
                value = formatRating(statistics.togetherAverageRating),
                label = "średnia ocen",
            )

            MetricCard(
                modifier = Modifier.weight(1f),
                icon = Lucide.ChartBar,
                value = statistics.rewatchCount.toString(),
                label = "powtórnych seansów",
            )
        }
    }
}

@Composable
private fun MetricCard(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    value: String,
    label: String,
) {
    val shape = RoundedCornerShape(20.dp)

    Column(
        modifier = modifier
            .clip(shape)
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.surfaceVariant,
                        MaterialTheme.colorScheme.surface,
                    ),
                ),
            )
            .padding(16.dp),
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .background(
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.14f),
                    shape = CircleShape,
                ),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
                tint = MaterialTheme.colorScheme.primary,
            )
        }

        Spacer(Modifier.height(18.dp))

        Text(
            text = value,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.ExtraBold,
        )

        Spacer(Modifier.height(2.dp))

        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun ProfileRatingsSection(
    profiles: List<ProfileStatistics>,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        SectionHeader(
            title = "Jak oceniacie?",
            description = "Każde z Was ma własny styl oceniania filmów.",
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            profiles.take(2).forEachIndexed { index, profile ->
                ProfileRatingCard(
                    profile = profile,
                    accent = profileAccent(index),
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
private fun ProfileRatingCard(
    profile: ProfileStatistics,
    accent: Color,
    modifier: Modifier = Modifier,
) {
    val maxDistribution = max(
        profile.ratingDistribution.values.maxOrNull() ?: 0,
        1,
    )

    val mostFrequentRating = profile.ratingDistribution
        .filterValues { count -> count > 0 }
        .maxByOrNull { (_, count) -> count }
        ?.key

    val shape = RoundedCornerShape(22.dp)

    Column(
        modifier = modifier
            .clip(shape)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        accent.copy(alpha = 0.18f),
                        MaterialTheme.colorScheme.surface,
                    ),
                ),
            )
            .padding(16.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AvatarImage(
                imagePath = profile.profile.avatarPath,
                size = 38.dp,
            )

            Spacer(Modifier.width(10.dp))

            Text(
                text = profile.profile.name,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.titleSmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }

        Spacer(Modifier.height(18.dp))

        Row(
            verticalAlignment = Alignment.Bottom,
        ) {
            Text(
                text = formatRating(profile.averageRating),
                style = MaterialTheme.typography.headlineLarge,
                color = accent,
            )

            Spacer(Modifier.width(4.dp))

            Text(
                text = "/ 10",
                modifier = Modifier.padding(bottom = 5.dp),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        Text(
            text = "${profile.ratedMoviesCount} ocenionych filmów",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Spacer(Modifier.height(18.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(42.dp),
            horizontalArrangement = Arrangement.spacedBy(3.dp),
            verticalAlignment = Alignment.Bottom,
        ) {
            (1..10).forEach { score ->
                val count = profile.ratingDistribution[score] ?: 0
                val fraction = count.toFloat() / maxDistribution

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(
                            (7f + 35f * fraction).dp,
                        )
                        .background(
                            color = if (count > 0) {
                                accent.copy(
                                    alpha = 0.35f + 0.65f * fraction,
                                )
                            } else {
                                MaterialTheme.colorScheme.surfaceVariant
                            },
                            shape = RoundedCornerShape(
                                topStart = 4.dp,
                                topEnd = 4.dp,
                            ),
                        ),
                )
            }
        }

        Spacer(Modifier.height(8.dp))

        Text(
            text = mostFrequentRating?.let { rating ->
                "Najczęściej wystawiana ocena: $rating"
            } ?: "Brak wystawionych ocen",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun MostWatchedGenresSection(
    genres: List<GenreStatistics>,
    totalUniqueMovies: Int,
) {
    val visibleGenres = genres
        .sortedWith(
            compareByDescending<GenreStatistics> { genre ->
                genre.uniqueMoviesCount
            }
                .thenByDescending { genre ->
                    genre.viewingsCount
                }
                .thenBy { genre ->
                    genre.genre.name
                },
        )
        .take(7)

    Column(
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        SectionHeader(
            title = "Najczęściej oglądane",
            description = "Jaki procent Waszych filmów należy do danego gatunku.",
        )

        StatisticsSurface {
            visibleGenres.forEachIndexed { index, genre ->
                val fraction = if (totalUniqueMovies > 0) {
                    genre.uniqueMoviesCount.toFloat() /
                            totalUniqueMovies.toFloat()
                } else {
                    0f
                }

                GenrePopularityRow(
                    genre = genre,
                    fraction = fraction,
                    percentage = (
                            fraction * 100f
                            ).roundToInt(),
                    totalUniqueMovies = totalUniqueMovies,
                )

                if (index != visibleGenres.lastIndex) {
                    Spacer(Modifier.height(18.dp))
                }
            }
        }
    }
}

@Composable
private fun GenrePopularityRow(
    genre: GenreStatistics,
    fraction: Float,
    percentage: Int,
    totalUniqueMovies: Int,
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = genre.genre.name,
                modifier = Modifier.width(92.dp),
                style = MaterialTheme.typography.titleSmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )

            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(9.dp)
                    .background(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(100.dp),
                    ),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(
                            fraction.coerceIn(0f, 1f),
                        )
                        .fillMaxHeight()
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    Color(0xFF9A6CFF),
                                    MaterialTheme.colorScheme.primary,
                                    Color(0xFFCE57D8),
                                ),
                            ),
                            shape = RoundedCornerShape(100.dp),
                        ),
                )
            }

            Spacer(Modifier.width(12.dp))

            Text(
                text = "$percentage%",
                modifier = Modifier.width(42.dp),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        Spacer(Modifier.height(6.dp))

        Text(
            text = buildString {
                append("${genre.uniqueMoviesCount} z ")
                append("$totalUniqueMovies filmów")

                if (genre.viewingsCount != genre.uniqueMoviesCount) {
                    append(" • ${genre.viewingsCount} seansów")
                }

                append(" • ${formatShortDuration(genre.watchedMinutes)}")
            },
            modifier = Modifier.padding(start = 92.dp),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
                .copy(alpha = 0.72f),
        )
    }
}

@Composable
private fun BestRatedGenresSection(
    genres: List<GenreStatistics>,
    profiles: List<ProfileStatistics>,
) {
    val ratedGenres = genres
        .filter { genre ->
            genre.togetherAverageRating != null
        }
        .sortedByDescending { genre ->
            genre.togetherAverageRating
        }
        .take(6)

    if (ratedGenres.isEmpty()) {
        return
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        SectionHeader(
            title = "Jak oceniacie gatunki?",
            description = "Tutaj widać, które filmowe klimaty trafiają w Was najlepiej.",
        )

        StatisticsSurface {
            ratedGenres.forEachIndexed { index, genre ->
                GenreRatingRow(
                    position = index + 1,
                    genre = genre,
                    profiles = profiles,
                )

                if (index != ratedGenres.lastIndex) {
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 18.dp),
                        color = MaterialTheme.colorScheme.outlineVariant,
                    )
                }
            }
        }
    }
}

@Composable
private fun GenreRatingRow(
    position: Int,
    genre: GenreStatistics,
    profiles: List<ProfileStatistics>,
) {
    val togetherRating = genre.togetherAverageRating ?: 0.0

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(
                        color = if (position <= 3) {
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.17f)
                        } else {
                            MaterialTheme.colorScheme.surfaceVariant
                        },
                        shape = CircleShape,
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = position.toString(),
                    style = MaterialTheme.typography.labelMedium,
                    color = if (position <= 3) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    },
                )
            }

            Spacer(Modifier.width(12.dp))

            Text(
                text = genre.genre.name,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.titleSmall,
            )

            Icon(
                imageVector = Lucide.Star,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = MaterialTheme.colorScheme.tertiary,
            )

            Spacer(Modifier.width(5.dp))

            Text(
                text = formatRating(togetherRating),
                style = MaterialTheme.typography.titleSmall,
            )
        }

        Spacer(Modifier.height(10.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(7.dp)
                .background(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(100.dp),
                ),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(
                        (togetherRating / 10.0)
                            .toFloat()
                            .coerceIn(0f, 1f),
                    )
                    .fillMaxHeight()
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary,
                                MaterialTheme.colorScheme.secondary,
                            ),
                        ),
                        shape = RoundedCornerShape(100.dp),
                    ),
            )
        }

        Spacer(Modifier.height(10.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            profiles.take(2).forEachIndexed { index, profile ->
                genre.profileAverageRatings[profile.profile.id]
                    ?.let { average ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .background(
                                        color = profileAccent(index),
                                        shape = CircleShape,
                                    ),
                            )

                            Spacer(Modifier.width(5.dp))

                            Text(
                                text = "${profile.profile.name}: " +
                                        formatRating(average),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme
                                    .onSurfaceVariant,
                            )
                        }
                    }
            }
        }
    }
}

@Composable
private fun TopMoviesSection(
    movies: List<StatisticsMovie>,
    onMovieClick: (movieId: Int) -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        SectionHeader(
            title = "Najlepiej ocenione razem",
            description = "Filmy, przy których byliście najbardziej zgodni.",
        )

        LazyRow(
            contentPadding = PaddingValues(end = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            itemsIndexed(
                items = movies.take(10),
                key = { _, movie -> movie.movieId },
            ) { index, movie ->
                TopMovieCard(
                    movie = movie,
                    position = index + 1,
                    onClick = {
                        onMovieClick(movie.movieId)
                    },
                )
            }
        }
    }
}

@Composable
private fun TopMovieCard(
    movie: StatisticsMovie,
    position: Int,
    onClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .width(126.dp)
            .clickable(onClick = onClick),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(2f / 3f)
                .clip(RoundedCornerShape(18.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant),

            contentAlignment = Alignment.Center,
        ) {
            if (movie.posterPath.isNullOrBlank()) {
                Icon(
                    imageVector = Lucide.Clapperboard,
                    contentDescription = null,
                    modifier = Modifier.size(32.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            } else {
                AsyncImage(
                    model = movie.posterPath,
                    contentDescription = movie.title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                )
            }

            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(8.dp)
                    .size(31.dp)
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary,
                                MaterialTheme.colorScheme.secondary,
                            ),
                        ),
                        shape = CircleShape,
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = position.toString(),
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.White,
                )
            }

            Row(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(8.dp)
                    .background(
                        color = Color.Black.copy(alpha = 0.82f),
                        shape = RoundedCornerShape(100.dp),
                    )
                    .padding(
                        horizontal = 8.dp,
                        vertical = 5.dp,
                    ),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = Lucide.Star,
                    contentDescription = null,
                    modifier = Modifier.size(12.dp),
                    tint = MaterialTheme.colorScheme.tertiary,
                )

                Spacer(Modifier.width(4.dp))

                Text(
                    text = formatRating(
                        movie.togetherAverageRating,
                    ),
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White,
                )
            }
        }

        Spacer(Modifier.height(9.dp))

        Text(
            text = movie.title,
            style = MaterialTheme.typography.titleSmall,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )

        if (movie.viewingCount > 1) {
            Spacer(Modifier.height(2.dp))

            Text(
                text = "${movie.viewingCount} seanse",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun CompatibilitySection(
    statistics: ReelyStatistics,
) {
    val averageDifference = statistics.averageRatingDifference

    val compatibility = averageDifference
        ?.let { difference ->
            (
                    100.0 -
                            difference.coerceIn(0.0, 9.0) /
                            9.0 *
                            100.0
                    ).roundToInt()
        }
        ?.coerceIn(0, 100)

    Column(
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        SectionHeader(
            title = "Filmowe dopasowanie",
            description = "Jak często patrzycie na filmy podobnie?",
        )

        val shape = RoundedCornerShape(24.dp)

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(shape)
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary
                                .copy(alpha = 0.20f),
                            MaterialTheme.colorScheme.secondary
                                .copy(alpha = 0.10f),
                            MaterialTheme.colorScheme.surface,
                        ),
                    ),
                )
                .padding(20.dp),
        ) {
            if (
                compatibility == null ||
                statistics.comparedMoviesCount == 0
            ) {
                Text(
                    text = "Potrzeba więcej wspólnie ocenionych filmów",
                    style = MaterialTheme.typography.titleMedium,
                )

                Spacer(Modifier.height(6.dp))

                Text(
                    text = "Gdy oba profile ocenią te same filmy, pokażemy Waszą zgodność.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                return@Column
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .background(
                            color = MaterialTheme.colorScheme.primary
                                .copy(alpha = 0.17f),
                            shape = CircleShape,
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "$compatibility%",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }

                Spacer(Modifier.width(16.dp))

                Column {
                    Text(
                        text = compatibilityDescription(compatibility),
                        style = MaterialTheme.typography.titleMedium,
                    )

                    Spacer(Modifier.height(4.dp))

                    Text(
                        text = "Średnia różnica: " +
                                "${formatRating(averageDifference)} pkt",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            Spacer(Modifier.height(18.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .background(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(100.dp),
                    ),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(compatibility / 100f)
                        .fillMaxHeight()
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.primary,
                                    MaterialTheme.colorScheme.secondary,
                                ),
                            ),
                            shape = RoundedCornerShape(100.dp),
                        ),
                )
            }

            Spacer(Modifier.height(18.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                CompatibilityMetric(
                    modifier = Modifier.weight(1f),
                    value = statistics.identicalRatingsCount.toString(),
                    label = "identycznych ocen",
                )

                CompatibilityMetric(
                    modifier = Modifier.weight(1f),
                    value = statistics.comparedMoviesCount.toString(),
                    label = "porównanych filmów",
                )
            }
        }
    }
}

@Composable
private fun CompatibilityMetric(
    value: String,
    label: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .background(
                color = MaterialTheme.colorScheme.background
                    .copy(alpha = 0.42f),
                shape = RoundedCornerShape(16.dp),
            )
            .padding(14.dp),
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary,
        )

        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun MonthlyActivitySection(
    statistics: ReelyStatistics,
) {
    val maxViewings = max(
        statistics.monthlyActivity
            .maxOfOrNull { month -> month.viewingsCount }
            ?: 0,
        1,
    )

    Column(
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        SectionHeader(
            title = "Ostatnie 12 miesięcy",
            description = "Kiedy najczęściej urządzaliście filmowe wieczory?",
        )

        StatisticsSurface {
            val months = statistics.monthlyActivity
                .sortedBy { activity ->
                    activity.month
                }

            val monthsListState = rememberLazyListState()

            LaunchedEffect(months.size) {
                if (months.isNotEmpty()) {
                    monthsListState.scrollToItem(
                        index = months.lastIndex,
                    )
                }
            }

            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                state = monthsListState,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.Bottom,
            ) {
                itemsIndexed(
                    items = months,
                    key = { _, activity ->
                        activity.month.toString()
                    },
                ) { _, activity ->
                    MonthBar(
                        month = activity.month,
                        viewingsCount = activity.viewingsCount,
                        maxViewings = maxViewings,
                    )
                }
            }
        }
    }
}

@Composable
private fun MonthBar(
    month: YearMonth,
    viewingsCount: Int,
    maxViewings: Int,
) {
    val fraction = viewingsCount.toFloat() / maxViewings

    Column(
        modifier = Modifier.width(30.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = if (viewingsCount > 0) {
                viewingsCount.toString()
            } else {
                ""
            },
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Spacer(Modifier.height(5.dp))

        Box(
            modifier = Modifier
                .height(86.dp)
                .width(15.dp),
            contentAlignment = Alignment.BottomCenter,
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(
                        if (viewingsCount == 0) {
                            5.dp
                        } else {
                            (18f + 68f * fraction).dp
                        },
                    )
                    .background(
                        brush = if (viewingsCount == 0) {
                            Brush.verticalGradient(
                                listOf(
                                    MaterialTheme.colorScheme.surfaceVariant,
                                    MaterialTheme.colorScheme.surfaceVariant,
                                ),
                            )
                        } else {
                            Brush.verticalGradient(
                                listOf(
                                    MaterialTheme.colorScheme.secondary,
                                    MaterialTheme.colorScheme.primary,
                                ),
                            )
                        },
                        shape = RoundedCornerShape(
                            topStart = 6.dp,
                            topEnd = 6.dp,
                        ),
                    ),
            )
        }

        Spacer(Modifier.height(7.dp))

        Text(
            text = formatMonth(month),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun WatchlistTimeSection(
    statistics: ReelyStatistics,
) {
    val movieEvenings = if (statistics.watchlistMinutes > 0L) {
        ceil(statistics.watchlistMinutes / 120.0).toInt()
    } else {
        0
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        SectionHeader(
            title = "Ile oglądania przed Wami?",
            description = "Czas wszystkich filmów czekających na wspólny seans.",
        )

        val shape = RoundedCornerShape(24.dp)

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(shape)
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF171128),
                            Color(0xFF131925),
                            MaterialTheme.colorScheme.surface,
                        ),
                    ),
                )
                .padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .background(
                            color = MaterialTheme.colorScheme.primary
                                .copy(alpha = 0.17f),
                            shape = CircleShape,
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Lucide.Clock,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        tint = MaterialTheme.colorScheme.primary,
                    )
                }

                Spacer(Modifier.width(14.dp))

                Column(
                    modifier = Modifier.weight(1f),
                ) {
                    Text(
                        text = if (statistics.watchlistMovieCount == 0) {
                            "Watchlista jest pusta"
                        } else {
                            formatLongDuration(
                                statistics.watchlistMinutes,
                            )
                        },
                        style = MaterialTheme.typography.headlineMedium,
                    )

                    Spacer(Modifier.height(2.dp))

                    Text(
                        text = "${statistics.watchlistMovieCount} filmów czeka na seans",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )

                    if (movieEvenings > 0) {
                        Spacer(Modifier.height(8.dp))

                        Text(
                            text = "To około $movieEvenings filmowych wieczorów.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StatisticsSurface(
    content: @Composable () -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        color = MaterialTheme.colorScheme.surface,
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
        ) {
            content()
        }
    }
}

@Composable
private fun SectionHeader(
    title: String,
    description: String,
) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium,
        )

        Spacer(Modifier.height(4.dp))

        Text(
            text = description,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground
                .copy(alpha = 0.58f),
        )
    }
}

@Composable
private fun EmptyStatisticsCard(
    watchlistMovieCount: Int,
) {
    val shape = RoundedCornerShape(28.dp)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary
                            .copy(alpha = 0.28f),
                        MaterialTheme.colorScheme.surface,
                    ),
                ),
            )
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            painter = painterResource(R.drawable.popcorn_splash),
            contentDescription = null,
            modifier = Modifier.size(120.dp),
        )

        Text(
            text = "Wasza filmowa historia dopiero się zaczyna",
            style = MaterialTheme.typography.headlineMedium,
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = if (watchlistMovieCount > 0) {
                "Na watchliście macie już $watchlistMovieCount filmów. Obejrzyjcie pierwszy, a Reely policzy resztę."
            } else {
                "Dodajcie filmy do watchlisty i zapisujcie wspólne seanse."
            },
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun StatisticsError(
    message: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            imageVector = Lucide.ChartBar,
            contentDescription = null,
            modifier = Modifier.size(42.dp),
            tint = MaterialTheme.colorScheme.error,
        )

        Spacer(Modifier.height(14.dp))

        Text(
            text = "Nie udało się wczytać statystyk",
            style = MaterialTheme.typography.titleMedium,
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.error,
        )
    }
}

@Composable
private fun profileAccent(
    index: Int,
): Color {
    return if (index == 0) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.secondary
    }
}

private fun compatibilityDescription(
    compatibility: Int,
): String {
    return when {
        compatibility >= 90 -> "Niemal filmowe bratnie dusze"
        compatibility >= 75 -> "Bardzo podobny filmowy gust"
        compatibility >= 55 -> "Zgadzacie się częściej niż kłócicie"
        compatibility >= 35 -> "Różnice dodają Waszym seansom charakteru"
        else -> "Dwa zupełnie różne filmowe światy"
    }
}

private fun formatRating(
    rating: Double?,
): String {
    if (rating == null || rating.isNaN()) {
        return "—"
    }

    return String.format(
        Locale.forLanguageTag("pl-PL"),
        "%.1f",
        rating,
    )
}

private fun formatLongDuration(
    minutes: Long,
): String {
    if (minutes <= 0L) {
        return "0 min"
    }

    val days = minutes / MINUTES_IN_DAY
    val hours = (
            minutes % MINUTES_IN_DAY
            ) / MINUTES_IN_HOUR
    val remainingMinutes = minutes % MINUTES_IN_HOUR

    return buildList {
        if (days > 0L) {
            add("$days dni")
        }

        if (hours > 0L) {
            add("$hours godz.")
        }

        if (remainingMinutes > 0L && days == 0L) {
            add("$remainingMinutes min")
        }
    }.joinToString(" ")
}

private fun formatShortDuration(
    minutes: Long,
): String {
    if (minutes <= 0L) {
        return "0 min"
    }

    val hours = minutes / 60L
    val remainingMinutes = minutes % 60L

    return when {
        hours == 0L -> "$remainingMinutes min"
        remainingMinutes == 0L -> "$hours godz."
        else -> "$hours godz. $remainingMinutes min"
    }
}

private fun formatMonth(
    month: YearMonth,
): String {
    return month
        .atDay(1)
        .format(
            DateTimeFormatter.ofPattern(
                "MMM",
                Locale.forLanguageTag("pl-PL"),
            ),
        )
        .removeSuffix(".")
}

private const val MINUTES_IN_HOUR = 60L
private const val MINUTES_IN_DAY = 24L * MINUTES_IN_HOUR