package com.example.reely.presentation.ui.screens.list.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.reely.presentation.theme.ReelyTheme
import com.example.reely.presentation.ui.components.MovieRow
import com.example.reely.presentation.ui.screens.list.ListMovieItem
import com.example.reely.presentation.ui.screens.list.MovieListTab

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

