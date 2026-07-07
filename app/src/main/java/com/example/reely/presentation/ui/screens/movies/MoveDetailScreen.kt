package com.example.reely.presentation.ui.screens.movies

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.reely.R
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import com.composables.icons.lucide.ArrowLeft
import com.composables.icons.lucide.Lucide
import com.example.reely.domain.model.averageRatingRounded
import com.example.reely.presentation.theme.ReelyTheme
import com.example.reely.presentation.ui.components.RatingResults
import com.example.reely.presentation.ui.components.ReelyIconButton
import com.example.reely.presentation.ui.components.SmallSectionHeader
import com.example.reely.presentation.ui.components.ratedMockMovie
import com.example.reely.presentation.ui.screens.home.components.OurStatsSection
import com.example.reely.presentation.ui.screens.movies.components.StarRating

@Composable
fun MovieDetailsScreen(
    movieId: Int,
    onBack : () -> Unit = {}
) {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                color = MaterialTheme.colorScheme.background
            )
    ) {
        Column {

            PosterHeader()

            WatchedMovieScreen()


        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        ReelyIconButton(
            icon = Lucide.ArrowLeft,
            onClick = onBack
        )
    }


}

@Composable
fun WatchedMovieScreen() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState())

    ) {

        Spacer(
            Modifier.height(16.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth()
        ) {

            Column(
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {

                StarRating(
                    score = ratedMockMovie.averageRatingRounded()
                )
                Spacer(
                    Modifier.width(8.dp)
                )
                RatingResults(
                    movie = ratedMockMovie,
                    style = MaterialTheme.typography.labelLarge
                )

            }

        }

        Spacer(Modifier.height(8.dp))

        OurStatsSection(
            textOverride = null
        )

        Spacer(Modifier.height(16.dp))

        Text(
            text = "Obejrzany · 4 lipca 2024",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
        )

        Spacer(Modifier.height(16.dp))

        SmallSectionHeader("O filmie:")

        Spacer(Modifier.height(8.dp))

        Text(
            text = "Historia jednego z cieszących się najgorszą sławą superprzestępców uniwersum DC — Jokera. Przedstawiony przez Phillipsa obraz śledzi losy kultowego czarnego charakteru, człowieka zepchniętego na margines. To nie tylko kontrowersyjne studium postaci, ale także opowieść ku przestrodze w szerszym kontekście.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
            lineHeight = 20.sp
        )

    }
}
@Composable
fun PosterHeader(){
    Box(
        modifier = Modifier.height(360.dp)
    ){
        Image(
            painter = painterResource(R.drawable.preview_image),
            contentDescription = "",
            contentScale = ContentScale.Crop,
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(128.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            MaterialTheme.colorScheme.background,
                        )
                    ),
                )
                .align(Alignment.BottomCenter)
        ){
            Column(
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.Start,
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(horizontal = 16.dp, vertical = 16.dp)
            ) {

                Text(
                    "Joker",
                    style = MaterialTheme.typography.displaySmall,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(
                    modifier = Modifier.width(4.dp)
                )

                Text(
                    "2020 · 2h 29m · Dramat, Sci-Fi",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                )

            }

        }
    }
}

@Composable
@Preview
fun MovieDetailsScreenPreview(){
    ReelyTheme {

        MovieDetailsScreen(
            movieId = 1
        )

    }
}