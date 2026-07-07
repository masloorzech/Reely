package com.example.reely.presentation.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.reely.domain.model.Movie


val mockMovie: Movie = Movie(
    id = 1234,
    title = "Lorem ipsum dolor",
    overview = "s simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's " +
            "standard dummy text ever since 1966, when designers at Letraset and James Mosley, the librarian at St Bride " +
            "Printing Library in London, took a 1914 Cicero translation and scrambled it to make dummy text for",
    posterPaths = null,
    releaseDate = "01.01.2002",
)

val ratedMockMovie : Movie = Movie(
    id = 2137,
    title = "Lorem ipsum dolor",
    overview = "s simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's " +
            "standard dummy text ever since 1966, when designers at Letraset and James Mosley, the librarian at St Bride " +
            "Printing Library in London, took a 1914 Cicero translation and scrambled it to make dummy text for",
    posterPaths = null,
    releaseDate = "01.01.2002",
    ratings = mapOf(
        Pair(1, 8.1),
        Pair(2, 9.2)
    )

)

@Composable
fun MovieRow(
    modifier: Modifier = Modifier,
    movie: Movie = mockMovie
){


}
