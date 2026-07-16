package com.example.reely.presentation.ui.screens.search

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.reely.presentation.ui.components.ReelySearchField
import com.example.reely.presentation.ui.components.SmallSectionHeader
import com.example.reely.presentation.ui.components.TopBar
import com.example.reely.presentation.ui.screens.search.components.MovieGrid
import com.example.reely.presentation.ui.screens.search.components.RecentSearchesSection

@Composable
fun SearchScreen(
    modifier: Modifier = Modifier,
    onMovieClick: (Int) -> Unit,
    viewModel: SearchScreenViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val recentSearches by
    viewModel.recentSearches.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            when (event) {
                is SearchEvent.NavigateToMovieDetails -> {
                    onMovieClick(event.movieId)
                }
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
        ) {
            TopBar(
                sectionName = "Szukaj",
            )

            Spacer(modifier = Modifier.height(16.dp))

            ReelySearchField(
                value = state.searchQuery,
                onValueChange = viewModel::onQueryChange,
                placeholder = "Szukaj filmu...",
                onSearchClick = viewModel::search,
            )

            if (recentSearches.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))

                RecentSearchesSection(
                    recentSearches = recentSearches,
                    onClick = viewModel::onRecentSearchClick,
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            SmallSectionHeader(
                sectionName = state.section.title,
            )

            Spacer(modifier = Modifier.height(16.dp))

            MovieGrid(
                movies = state.movies,
                isLoading = state.isLoading,
                onMovieClick = viewModel::onMovieSelected,
            )

            Spacer(modifier = Modifier.height(16.dp))
        }

        state.error?.let { error ->
            Row(
                modifier = Modifier
                    .height(64.dp)
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                MaterialTheme.colorScheme.background,
                            ),
                        ),
                    ),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = error,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}