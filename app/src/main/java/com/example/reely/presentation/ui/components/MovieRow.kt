package com.example.reely.presentation.ui.components

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.unit.dp
import com.composables.icons.lucide.Calendar
import com.composables.icons.lucide.Clock
import com.composables.icons.lucide.Lucide
import com.example.reely.presentation.ui.screens.list.ListMovieItem
import com.example.reely.presentation.ui.screens.list.toPreview
import com.example.reely.presentation.ui.screens.movies.toPolishDate
import com.example.reely.presentation.ui.toRuntimeText

@SuppressLint("DefaultLocale")
@Composable
fun MovieRow(
    modifier: Modifier = Modifier,
    movie: ListMovieItem,
    onMovieClicked: (Int) -> Unit = {}
){

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
                    onMovieClicked(movie.id)
                }
            )
            .fillMaxWidth()
            .padding(4.dp)
    ){


        MoviePoster(movie = movie.toPreview())


        Spacer(Modifier.width(8.dp))

        Column(
            horizontalAlignment = Alignment.Start
        ){
            Text(
                text = movie.title,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(Modifier.height(4.dp))

            Row() {

                Text(
                    text = "Ocena globalna:",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                )

                Spacer(Modifier.width(4.dp))

                Icon(
                    imageVector = Icons.Filled.Star,
                    contentDescription = "",
                    tint = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.size(16.dp).padding(end = 2.dp)
                )

                Text(
                    text = String.format("%.1f/10", movie.publicRating),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.tertiary
                )
            }

            Spacer(Modifier.height(4.dp))

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
                    text = movie.releaseDate?.toPolishDate().toString(),
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
                    text = movie.runtime?.toRuntimeText() ?: "Brak danych",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                )

            }

            Spacer(Modifier.height(4.dp))

            Text(
                text = movie.overview?: "Brak opisu",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f),
                maxLines = 2
            )

            Spacer(Modifier.height(8.dp))

            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                items(movie.genres){ genre ->

                    Box(
                        modifier = Modifier.background(
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(64.dp)
                        )
                            .padding(4.dp)
                    ){
                        Text(
                            text = genre.name,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                        )
                    }

                }

            }

        }
    }

}