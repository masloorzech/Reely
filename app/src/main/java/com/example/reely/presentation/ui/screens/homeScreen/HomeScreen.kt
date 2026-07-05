package com.example.reely.presentation.ui.screens.homeScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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

val lastSeenPreview = listOf(
    Movie(
        id = 1,
        title = "Interstellar",
        overview = "Grupa astronautów wyrusza przez tunel czasoprzestrzenny, aby znaleźć nowy dom dla ludzkości.",
        posterPath = null,
        releaseDate = "2014-11-07",
        ratings = mapOf(
            1 to 9.0,
            2 to 8.5
        )
    ),
    Movie(
        id = 2,
        title = "The Dark Knight",
        overview = "Batman mierzy się z Jokerem, który pogrąża Gotham w chaosie i testuje granice moralności bohatera.",
        posterPath = null,
        releaseDate = "2008-07-18",
        ratings = mapOf(
            1 to 9.5,
            2 to 8.5
        )
    ),
    Movie(
        id = 3,
        title = "La La Land",
        overview = "Pianista jazzowy i początkująca aktorka próbują pogodzić miłość z marzeniami o karierze w Los Angeles.",
        posterPath = null,
        releaseDate = "2016-12-09",
        ratings = mapOf(
            1 to 8.0,
            2 to 7.8
        )
    ),
    Movie(
        id = 4,
        title = "Dune",
        overview = "Paul Atreides trafia na pustynną planetę Arrakis, gdzie rozpoczyna się walka o władzę, przyprawę i przyszłość rodu.",
        posterPath = null,
        releaseDate = "2021-10-22",
        ratings = mapOf(
            1 to 8.5,
            2 to 7.5
        )
    ),
    Movie(
        id = 5,
        title = "Whiplash",
        overview = "Młody perkusista trafia pod skrzydła bezwzględnego nauczyciela, który pcha go do granic wytrzymałości.",
        posterPath = null,
        releaseDate = "2014-10-10",
        ratings = mapOf(
            1 to 9.0,
            2 to 8.0
        )
    )
)

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
        Column()
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

            LastSeenSection(lastSeenPreview)

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