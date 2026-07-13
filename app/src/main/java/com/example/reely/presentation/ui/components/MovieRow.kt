package com.example.reely.presentation.ui.components

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.composables.icons.lucide.Calendar
import com.composables.icons.lucide.Clock
import com.composables.icons.lucide.EllipsisVertical
import com.composables.icons.lucide.Lucide
import com.example.reely.R
import com.example.reely.domain.model.Movie
import com.example.reely.domain.model.PosterPaths
import com.example.reely.domain.model.PublicRating
import com.example.reely.domain.model.UserId
import com.example.reely.domain.model.UserMovieWatch
import com.example.reely.domain.model.runtimeText
import com.example.reely.presentation.theme.ReelyTheme
import com.example.reely.presentation.theme.StarFilled
import com.example.reely.presentation.ui.formatWatchedDate
import kotlin.math.roundToLong

@SuppressLint("DefaultLocale")
@Composable
fun MovieRow(
    modifier: Modifier = Modifier,
    movie: Movie,
    onMovieClicked: (Movie) -> Unit = {}
){
    val isPreview = LocalInspectionMode.current

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(12.dp)
            )
            .clip(RoundedCornerShape(12.dp))
            .clickable(
                onClick = {
                    onMovieClicked(movie)
                }
            )
            .fillMaxWidth()
            .padding(4.dp)
    ){

        if (isPreview) {
            Image(
                painter = painterResource(R.drawable.preview_image),
                contentDescription = movie.title,
                modifier = Modifier
                    .width(64.dp)
                    .aspectRatio(2f / 3f)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Fit
            )
        } else {
            MoviePoster(movie = movie)
        }

        Spacer(Modifier.width(8.dp))

        Column(
            horizontalAlignment = Alignment.Start
        ){
            Text(
                text = movie.title,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onBackground
            )

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                Icon(
                    imageVector = Lucide.Calendar,
                    contentDescription = "",
                    tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                    modifier = Modifier.size(16.dp).padding(end = 4.dp)
                )
                Text(
                    text = movie.releaseDate.formatWatchedDate(),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                )


                Spacer(Modifier.width(2.dp))
                Text(
                    text = " · ",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                )
                Spacer(Modifier.width(2.dp))

                Icon(
                    imageVector = Lucide.Clock,
                    contentDescription = "",
                    tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                    modifier = Modifier.size(16.dp).padding(end = 4.dp)
                )
                Text(
                    text = movie.runtimeText() ?: "Brak danych",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                )

                Spacer(Modifier.width(16.dp))

                Icon(
                    imageVector = Icons.Filled.Star,
                    contentDescription = "",
                    tint = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.size(16.dp).padding(end = 4.dp)
                )

                Text(
                    text = String.format("%.1f/10", movie.publicRating?.average),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.tertiary
                )
            }

            Text(
                text = movie.overview,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f),
                maxLines = 3
            )

        }
    }

}