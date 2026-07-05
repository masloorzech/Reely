package com.example.reely.presentation.ui.screens.homeScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.reely.domain.model.Movie
import com.example.reely.presentation.theme.ReelyTheme
import com.example.reely.presentation.ui.screens.homeScreen.components.HelloSection
import com.example.reely.presentation.ui.screens.homeScreen.components.LastSeenSection
import com.example.reely.presentation.ui.screens.homeScreen.components.OurStatsSection
import com.example.reely.presentation.ui.screens.homeScreen.components.RandomFilmCard

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        )
        {

            HelloSection(
                firstAvatarPath = null,
                secondAvatarPath = null
            )

            Spacer(modifier = Modifier.height(16.dp))

            RandomFilmCard(
                onClick = {}
            )

            Spacer(modifier = Modifier.height(16.dp))

            LastSeenSection(emptyList())

            Spacer(modifier = Modifier.height(16.dp))

            OurStatsSection()

            //CalendarSection()


        }


    }
}


@Preview(
    showBackground = true,
    backgroundColor = 0xFF0B0E14
)
@Composable
fun HomeScreenPreview() {
    ReelyTheme {
        HomeScreen()
    }
}