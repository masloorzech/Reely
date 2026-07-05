package com.example.reely.presentation.ui.screens.homeScreen.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.composables.icons.lucide.Clapperboard
import com.composables.icons.lucide.Lucide
import com.example.reely.domain.model.Movie
import com.example.reely.presentation.ui.components.MoviePoster
import com.example.reely.presentation.ui.components.RatingResults
import java.io.File

@Composable
fun LastSeenSection(
    lastSeenFilmsList: List<Movie>
){

    Column(
        modifier = Modifier.fillMaxWidth()
    ){

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = "Ostatnio obejrzane",
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(Modifier.weight(1f))

            Text(
                text = "Zobacz więcej >",
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.bodyMedium
            )
        }
        Spacer(Modifier.height(8.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),

        ) {
            items(lastSeenFilmsList) { movie ->
                MovieCard(
                    movie = movie,
                    onClick = {
                        // kliknięcie filmu
                    }
                )
            }
        }

    }

}

@Composable
fun MovieCard(
    movie: Movie,
    onClick : () -> Unit = {}
){

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = movie.title,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onBackground
        )

        MoviePoster(
            movie = movie,
            content = {
                if (movie.ratings.isNotEmpty()) {
                    RatingResults(
                        modifier = Modifier.align(Alignment.BottomCenter),
                        movie = movie
                    )
                }
            }
        )
    }
}