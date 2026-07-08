package com.example.reely.presentation.ui.screens.movies.components

import android.util.Log
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.example.reely.presentation.ui.formatWatchedDate

@Composable
fun PosterHeader(
    isLoading : Boolean,
    posterPath: String?,
    title: String?,
    releaseDate: String?,
    duration: String?,
    genres : String?
){

    Box(
        modifier = Modifier.height(360.dp)
    ){
        AsyncImage(
            model = posterPath,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            alignment = Alignment.TopCenter,
            modifier = Modifier.fillMaxSize(),
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(128.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            MaterialTheme.colorScheme.background,
                        )
                    ),
                )
                .align(Alignment.BottomCenter)
        ){
            if (!isLoading && title!=null && releaseDate != null && duration!=null && genres!=null){
                MovieMainData(
                    title = title,
                    releaseDate = releaseDate,
                    duration = duration,
                    genres = genres
                )
            }else {
                MovieMainDataSkeleton()
            }
        }
    }
}

@Composable
fun MovieMainData(
    title: String,
    releaseDate: String,
    duration: String,
    genres: String,
){

    Column(
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.Start,
        modifier = Modifier
            .fillMaxHeight()
            .padding(horizontal = 16.dp, vertical = 16.dp)
    ) {

        AutoResizeTitle(
            text = title,
            style = MaterialTheme.typography.displaySmall,
            color = MaterialTheme.colorScheme.onBackground,
            minFontSize = 22.sp
        )
        Spacer(
            modifier = Modifier.height(4.dp)
        )

        Text(
            "${releaseDate.formatWatchedDate()} · $duration \n$genres",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
        )

    }
}

@Composable
fun MovieMainDataSkeleton() {
    val transition = rememberInfiniteTransition(label = "movie_main_data_skeleton")

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

    val baseColor = MaterialTheme.colorScheme.surfaceContainer
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

    Column(
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.Start,
        modifier = Modifier
            .fillMaxHeight()
            .padding(horizontal = 16.dp, vertical = 16.dp)
    ) {
        Box(
            modifier = Modifier
                .width(240.dp)
                .height(36.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(shimmerBrush)
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        Box(
            modifier = Modifier
                .width(200.dp)
                .height(14.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(shimmerBrush)
        )
    }
}

@Composable
fun AutoResizeTitle(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.displaySmall,
    color: Color = MaterialTheme.colorScheme.onBackground,
    minFontSize: TextUnit = 22.sp
) {
    var currentFontSize by remember(text) {
        mutableStateOf(style.fontSize)
    }

    Text(
        text = text,
        modifier = modifier.fillMaxWidth(),
        style = style.copy(fontSize = currentFontSize),
        color = color,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        onTextLayout = { textLayoutResult ->
            if (
                textLayoutResult.hasVisualOverflow &&
                currentFontSize > minFontSize
            ) {
                currentFontSize = (currentFontSize.value - 1).sp
            }
        }
    )
}