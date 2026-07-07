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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.reely.presentation.theme.ReelyTheme
import com.example.reely.presentation.ui.components.ReelySearchField
import com.example.reely.presentation.ui.components.TopBar
import com.example.reely.presentation.ui.screens.search.components.MovieGrid
import com.example.reely.presentation.ui.screens.search.components.RecentSearchesSection

@Composable
fun SearchScreen(
    modifier : Modifier = Modifier,
    viewModel: SearchScreenViewModel = viewModel()
) {

    val state = viewModel.state.collectAsState().value

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {

            TopBar(
                sectionName = "Szukaj"
            )

            Spacer(modifier.height(16.dp))

            ReelySearchField(
                value = state.searchQuery,
                onValueChange = viewModel::onQueryChange,
                placeholder = "Szukaj filmu...",
                onSearchClick = viewModel::search
            )

            Spacer(modifier.height(16.dp))

            RecentSearchesSection(
                recentSearches = listOf(
                    "Interstellar",
                    "Woda",
                    "Kształt wody",
                    "Sparkle",
                    "Dash"
                )
            )

            Spacer(modifier.height(32.dp))

            MovieGrid(
                movies = state.movies,
                isLoading = state.isLoading || state.error!=null
            )

        }
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
                        )
                    ),
                ),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ){
            //TODO Remove
            if (state.error != null) {
                Text(
                    text = "O nie pojawił się błąd \uD83D\uDE31\n${state.error}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}




@Preview
@Composable
fun SearchScreenPreview(){
    ReelyTheme {
        SearchScreen(
        )
    }
}