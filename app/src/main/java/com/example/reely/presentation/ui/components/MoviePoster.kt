package com.example.reely.presentation.ui.components

import android.util.Log
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.composables.icons.lucide.Clapperboard
import com.composables.icons.lucide.Lucide
import com.example.reely.domain.model.Movie
import java.io.File

@Composable
fun MoviePoster(
    movie: Movie,
    content: @Composable  BoxScope.() -> Unit = {},
    onClick : (Movie) -> Unit = {}
){

    val posterModel = remember(movie.posterPaths) {
        val localFile = movie.posterPaths
            ?.localPosterPath
            ?.let { File(it) }
            ?.takeIf { it.exists() }

        localFile ?: movie.posterPaths?.remotePosterPath
    }

    Log.d("MoviePoster", "movie=${movie.title}")
    Log.d("MoviePoster", "localPosterPath=${movie.posterPaths?.localPosterPath}")
    Log.d("MoviePoster", "remotePosterPath=${movie.posterPaths?.remotePosterPath}")
    Log.d("MoviePoster", "posterModel=$posterModel")

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .width(100.dp)
            .height(150.dp)
            .clip(
                RoundedCornerShape(8.dp)
            )
            .background(
                color = MaterialTheme.colorScheme.surface,
            )
            .clickable(
                onClick = {
                    Log.d("Result", movie.id.toString())
                    onClick(movie)
                }
            )
    ) {
        if (posterModel != null) {
            AsyncImage(
                model = posterModel,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
                onLoading = {
                    Log.d("MoviePoster", "LOADING: ${movie.title} | model=$posterModel")
                },
                onSuccess = {
                    Log.d("MoviePoster", "SUCCESS: ${movie.title} | model=$posterModel")
                },
                onError = { error ->
                    Log.e(
                        "MoviePoster",
                        "ERROR: ${movie.title} | model=$posterModel",
                        error.result.throwable
                    )
                }
            )
        } else {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Lucide.Clapperboard,
                    contentDescription = "",
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                )
                Text(
                    text = movie.title,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                    textAlign = TextAlign.Center
                )
            }

        }
        content()
    }
}

@Composable
fun MoviePosterSkeleton(
    content: @Composable BoxScope.() -> Unit = {}
) {
    val transition = rememberInfiniteTransition(label = "movie_poster_skeleton")

    val shimmerOffset by transition.animateFloat(
        initialValue = -300f,
        targetValue = 900f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1200,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_offset"
    )

    val baseColor = MaterialTheme.colorScheme.surface
    val highlightColor = MaterialTheme.colorScheme.surfaceContainerLow

    val shimmerBrush = Brush.linearGradient(
        colors = listOf(
            baseColor,
            highlightColor,
            baseColor
        ),
        start = Offset(shimmerOffset, shimmerOffset),
        end = Offset(shimmerOffset + 300f, shimmerOffset + 300f)
    )

    Box(
        modifier = Modifier
            .width(100.dp)
            .height(150.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(shimmerBrush),
        content = content
    )
}

@Composable
fun MoviePosterEmpty(
    content: @Composable  BoxScope.() -> Unit = {}
){

    Box(
        modifier = Modifier
            .width(100.dp)
            .height(150.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(
                color = MaterialTheme.colorScheme.surface
            ),
        content = content
    )
}