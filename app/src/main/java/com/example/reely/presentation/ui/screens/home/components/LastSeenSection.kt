package com.example.reely.presentation.ui.screens.home.components

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.reely.domain.model.MoviePreview
import com.example.reely.domain.model.RecentWatchedMovie
import com.example.reely.domain.model.toPreview
import com.example.reely.presentation.ui.components.MoviePoster
import com.example.reely.presentation.ui.components.MoviePosterEmpty
import com.example.reely.presentation.ui.components.RatingResults

@Composable
fun LastSeenSection(
    lastSeenMoviesList: List<RecentWatchedMovie>,
    onMovieClick: (MoviePreview) -> Unit = {},
    onSeeMoreClick: () -> Unit = {}
){

    Column(
        modifier = Modifier.fillMaxWidth()
            .padding(vertical = 8.dp)
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
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.clickable(
                    onClick = onSeeMoreClick
                )
            )
        }
        Spacer(Modifier.height(8.dp))

        if (lastSeenMoviesList.isEmpty()){

            Box(

            ){
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    repeat(5){
                        item {
                            MoviePosterEmpty() {}
                        }
                    }
                }

                Text(
                    text = "Nie masz jeszcze żadnych obejrzanych filmów.\nObejrzyj coś, a pojawi się tutaj.",
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.labelMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.align(alignment = Alignment.Center)
                )

            }

        }else {
            FiledLastSeenMoviesList(
                lastSeenMoviesList = lastSeenMoviesList,
                onMovieClick = onMovieClick
            )
        }

    }

}

@Composable
fun FiledLastSeenMoviesList(
    lastSeenMoviesList : List<RecentWatchedMovie>,
    onMovieClick: (MoviePreview) -> Unit
){
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
        items(lastSeenMoviesList) { movie ->
            MovieCard(
                movie = movie,
                onClick = onMovieClick
            )
        }
    }


}

@Composable
fun MovieCard(
    movie: RecentWatchedMovie,
    onClick : (MoviePreview) -> Unit = {}
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
            movie = movie.toPreview(),
            onClick = onClick,
            content = {
                Box (
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(32.dp)
                        .background(
                            color = MaterialTheme.colorScheme.background.copy(alpha = 0.6f),
                            shape = RoundedCornerShape(
                                topStart = 8.dp,
                                topEnd = 0.dp,
                                bottomEnd = 0.dp,
                                bottomStart = 0.dp
                            )
                        )
                ) {
                    RatingResults(
                        movie = movie
                    )
                }
            }
        )
    }
}