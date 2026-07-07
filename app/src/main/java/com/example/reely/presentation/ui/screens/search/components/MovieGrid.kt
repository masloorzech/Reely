package com.example.reely.presentation.ui.screens.search.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.reely.domain.model.Movie
import com.example.reely.presentation.ui.components.MoviePoster
import com.example.reely.presentation.ui.components.MoviePosterSkeleton
import com.example.reely.presentation.ui.components.SmallSectionHeader

@Composable
fun MovieGrid(
    movies : List<Movie> = emptyList(),
    isLoading: Boolean = false,
    onMovieClick: (Movie) -> Unit = {}
){

    SmallSectionHeader("Popularne teraz")

    Spacer(modifier = Modifier.height(8.dp))

    LazyVerticalGrid(
        columns = GridCells.Fixed(4),
        contentPadding = PaddingValues(0.dp),
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        if (!isLoading) {
            items(movies) { movie ->
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    MoviePoster(
                        movie = movie,
                        onClick = onMovieClick
                    )
                }
            }
        } else {
            items(16) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    MoviePosterSkeleton()
                }
            }
        }
    }

}