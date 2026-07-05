package com.example.reely.presentation.ui.screens.search

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
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

    var searchText by remember { mutableStateOf("") }

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
                value = searchText,
                onValueChange = { searchText = it },
                placeholder = "Szukaj filmu...",
                onSearchClick = {
                }
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
    }
}




@Preview
@Composable
fun SearchScreenPreview(){
    ReelyTheme {
        SearchScreen()
    }
}