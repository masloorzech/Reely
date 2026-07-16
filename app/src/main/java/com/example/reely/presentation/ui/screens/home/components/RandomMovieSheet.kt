package com.example.reely.presentation.ui.screens.home.components

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.zIndex
import coil3.compose.AsyncImage
import com.composables.icons.lucide.Clapperboard
import com.composables.icons.lucide.Dices
import com.composables.icons.lucide.Lucide
import com.example.reely.R
import com.example.reely.domain.model.WatchlistEntry
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlinx.coroutines.delay

private enum class RandomMoviePhase {
    PREPARING,
    ROLLING,
    RESULT,
    EMPTY,
}

private val addedAtFormatter =
    DateTimeFormatter.ofPattern(
        "d MMMM yyyy",
        Locale.forLanguageTag("pl-PL"),
    )

@SuppressLint("UseOfNonLambdaOffsetOverload")
@Composable
fun RandomMovieSheet(
    moviesToRoll: List<WatchlistEntry>,
    previousMovieId: Int? = null,
    onMovieDrawn: (WatchlistEntry) -> Unit = {},
    onMovieClick: (Int) -> Unit = {},
    onDismiss: () -> Unit = {},
) {

    val winner = remember {
        val candidates = moviesToRoll
            .filterNot { entry ->
                moviesToRoll.size > 1 &&
                        entry.movie.id == previousMovieId
            }
            .ifEmpty {
                moviesToRoll
            }

        candidates.randomOrNull()
    }

    val currentOnMovieDrawn by rememberUpdatedState(onMovieDrawn)

    var displayedEntry by remember {
        mutableStateOf<WatchlistEntry?>(null)
    }

    var phase by remember {
        mutableStateOf(RandomMoviePhase.PREPARING)
    }

    LaunchedEffect(Unit) {
        if (winner == null) {
            phase = RandomMoviePhase.EMPTY
            return@LaunchedEffect
        }

        phase = RandomMoviePhase.PREPARING
        displayedEntry = null

        delay(600)

        phase = RandomMoviePhase.ROLLING

        if (moviesToRoll.size == 1) {
            displayedEntry = winner
            delay(850)
        } else {
            repeat(15) { step ->
                displayedEntry = moviesToRoll
                    .randomDifferentFrom(
                        movieId = displayedEntry?.movie?.id,
                    )

                delay(
                    65L + step * 12L,
                )
            }

            displayedEntry = winner
            delay(220)
        }

        phase = RandomMoviePhase.RESULT
        currentOnMovieDrawn(winner)
    }

    val isResult = phase == RandomMoviePhase.RESULT
    val isRolling = phase == RandomMoviePhase.ROLLING

    val posterScale by animateFloatAsState(
        targetValue = if (isResult) 1.03f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow,
        ),
        label = "random_movie_poster_scale",
    )

    val posterOffset by animateDpAsState(
        targetValue = if (isResult) (-12).dp else 0.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow,
        ),
        label = "random_movie_poster_offset",
    )

    val posterElevation by animateDpAsState(
        targetValue = if (isResult) 20.dp else 6.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessLow,
        ),
        label = "random_movie_poster_elevation",
    )

    val rollingTransition = rememberInfiniteTransition(
        label = "random_movie_rolling",
    )

    val rollingRotation by rollingTransition.animateFloat(
        initialValue = -2.5f,
        targetValue = 2.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 130,
                easing = LinearEasing,
            ),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "random_movie_rotation",
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
        ),
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
                    .padding(bottom = 12.dp)
                    .navigationBarsPadding(),
                shape = RoundedCornerShape(30.dp),
                color = MaterialTheme.colorScheme.background,
                tonalElevation = 6.dp,
            ) {
                Box(
                    modifier = Modifier.fillMaxWidth()
                        .background(
                            brush = Brush.linearGradient(
                                colorStops = arrayOf(
                                    0.00f to Color(0xFF7651E5).copy(alpha = 0.34f),
                                    0.28f to Color(0xFF35214F).copy(alpha = 0.78f),
                                    0.55f to Color(0xFF241A24).copy(alpha = 0.92f),
                                    0.78f to Color(0xFFFFA726).copy(alpha = 0.08f),
                                    1.00f to Color(0xFF07080D),
                                ),
                                start = Offset.Zero,
                                end = Offset(
                                    x = Float.POSITIVE_INFINITY,
                                    y = Float.POSITIVE_INFINITY,
                                ),
                            ),
                        )
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.popcorn_border),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp)
                            .align(Alignment.BottomCenter)
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                start = 20.dp,
                                top = 18.dp,
                                end = 20.dp,
                                bottom = 110.dp,
                            )
                            .offset(y = 28.dp)
                            .zIndex(1f),
                        verticalArrangement = Arrangement.spacedBy(18.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        AnimatedContent(
                            targetState = phase,
                            transitionSpec = {
                                fadeIn(tween(220)) togetherWith fadeOut(tween(120))
                            },
                            label = "random_movie_title",
                        ) { currentPhase ->
                            Text(
                                text = when (currentPhase) {
                                    RandomMoviePhase.PREPARING -> "Przygotujcie popcorn…"
                                    RandomMoviePhase.ROLLING -> "Losujemy…"
                                    RandomMoviePhase.RESULT -> "Dzisiaj oglądacie"
                                    RandomMoviePhase.EMPTY -> "Brak filmów do losowania"
                                },
                                style = MaterialTheme.typography.titleLarge,
                                textAlign = TextAlign.Center,
                            )
                        }

                        AnimatedContent(
                            targetState = displayedEntry,
                            transitionSpec = {
                                (
                                        slideInVertically(
                                            animationSpec = tween(130),
                                            initialOffsetY = { height -> height / 2 },
                                        ) + fadeIn(tween(130))
                                        ) togetherWith (
                                        slideOutVertically(
                                            animationSpec = tween(100),
                                            targetOffsetY = { height -> -height / 2 },
                                        ) + fadeOut(tween(90))
                                        )
                            },
                            label = "random_movie_poster",
                        ) { entry ->
                            RandomMoviePoster(
                                entry = entry,
                                isEmpty = phase == RandomMoviePhase.EMPTY,
                                onClick = {
                                    entry?.let {
                                        onMovieClick(it.movie.id)
                                    }
                                },
                                modifier = Modifier
                                    .width(190.dp)
                                    .aspectRatio(2f / 3f)
                                    .offset(y = posterOffset)
                                    .zIndex(1f)
                                    .graphicsLayer {
                                        scaleX = posterScale
                                        scaleY = posterScale
                                        rotationZ = if (isRolling) rollingRotation else 0f
                                    },
                                elevation = posterElevation,
                            )
                        }

                        val resultEntry = displayedEntry

                        AnimatedVisibility(
                            visible = isResult && resultEntry != null,
                            enter = fadeIn(tween(350)) + slideInVertically(
                                animationSpec = tween(350),
                                initialOffsetY = { height -> height / 2 },
                            ),
                            exit = fadeOut(),
                        ) {
                            resultEntry?.let { entry ->
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(6.dp),
                                ) {
                                    Text(
                                        text = entry.movie.title,
                                        style = MaterialTheme.typography.titleMedium,
                                        textAlign = TextAlign.Center,
                                    )

                                    Text(
                                        text = "Dodano do listy: ${entry.addedAt.toWatchlistDateText()}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.65f),
                                        textAlign = TextAlign.Center,
                                    )
                                }
                            }
                        }

                        if (phase == RandomMoviePhase.EMPTY) {
                            Text(
                                text = "Dodajcie najpierw przynajmniej jeden film do watchlisty.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.65f),
                                textAlign = TextAlign.Center,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RandomMoviePoster(
    modifier: Modifier = Modifier,
    entry: WatchlistEntry?,
    isEmpty: Boolean,
    onClick: () -> Unit = {},
    elevation: androidx.compose.ui.unit.Dp = 6.dp,
) {
    Surface(
        modifier = modifier,
        onClick = onClick,
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = elevation,
    ) {
        when {
            entry?.movie?.posterPath != null -> {
                AsyncImage(
                    model = entry.movie.posterPath,
                    contentDescription = entry.movie.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                )
            }

            entry != null -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            MaterialTheme.colorScheme.surface,
                        )
                        .padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    Icon(
                        imageVector = Lucide.Clapperboard,
                        contentDescription = null,
                        modifier = Modifier.size(32.dp),
                        tint = MaterialTheme
                            .colorScheme
                            .onSurface
                            .copy(alpha = 0.6f),
                    )

                    Text(
                        text = entry.movie.title,
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme
                            .colorScheme
                            .onSurface
                            .copy(alpha = 0.7f),
                        modifier = Modifier.padding(top = 8.dp),
                    )
                }
            }

            else -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(14.dp))
                        .background(
                            MaterialTheme.colorScheme.surface,
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = if (isEmpty) {
                            Lucide.Clapperboard
                        } else {
                            Lucide.Dices
                        },
                        contentDescription = null,
                        modifier = Modifier.size(42.dp),
                        tint = MaterialTheme
                            .colorScheme
                            .onSurface
                            .copy(alpha = 0.65f),
                    )
                }
            }
        }
    }
}

private fun List<WatchlistEntry>.randomDifferentFrom(
    movieId: Int?,
): WatchlistEntry? {
    val candidates = filterNot { entry ->
        entry.movie.id == movieId
    }.ifEmpty {
        this
    }

    return candidates.randomOrNull()
}

private fun Instant.toWatchlistDateText(): String {
    return atZone(
        ZoneId.systemDefault(),
    )
        .toLocalDate()
        .format(addedAtFormatter)
}