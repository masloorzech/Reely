package com.example.reely.presentation.ui.screens.list.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.reely.domain.model.Movie
import com.example.reely.presentation.theme.ReelyTheme
import com.example.reely.presentation.ui.components.MovieRow

@Composable
fun MovieList(
    modifier: Modifier = Modifier,
    movies : List<Movie> = emptyList(),
    onMovieClick: (Movie) -> Unit = {},
) {

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        for (movie in movies) {
            item {
                MovieRow(
                    movie = movie,
                    onMovieClicked = onMovieClick
                )
            }
        }
    }
}

