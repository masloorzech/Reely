package com.example.reely.presentation.ui.screens.list.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.reely.presentation.ui.components.MoviePoster
import com.example.reely.presentation.ui.screens.list.ListMovieItem
import com.example.reely.presentation.ui.screens.list.toPreview

@Composable
fun MovieListGrid(
    movies: List<ListMovieItem>,
    onMovieClick: (movieId: Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(4),
        contentPadding = PaddingValues(0.dp),
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        items(
            items = movies,
            key = { movie -> movie.id },
        ) { movie ->
            MoviePoster(
                movie = movie.toPreview(),
                onClick = {
                    onMovieClick(movie.id)
                }
            )
        }
    }
}
