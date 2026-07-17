package com.example.reely.presentation.ui.screens.list.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.reely.presentation.ui.components.MovieRow
import com.example.reely.presentation.ui.screens.list.ListMovieItem

@Composable
fun MovieList(
    modifier: Modifier = Modifier,
    movies : List<ListMovieItem> = emptyList(),
    onMovieClick: (Int) -> Unit = {},
) {

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(bottom = 16.dp),
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

