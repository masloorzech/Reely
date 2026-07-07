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
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.composables.icons.lucide.ArrowLeft
import com.composables.icons.lucide.Lucide
import com.example.reely.domain.model.Movie
import com.example.reely.domain.model.ourAverageRatingRounded
import com.example.reely.domain.usecase.LoadMovieDataUseCase
import com.example.reely.presentation.ui.components.RatingResults
import com.example.reely.presentation.ui.components.ReelyIconButton
import com.example.reely.presentation.ui.components.SmallSectionHeader
import com.example.reely.presentation.ui.screens.home.components.OurStatsSection
import com.example.reely.presentation.ui.screens.movies.components.PosterHeader
import com.example.reely.presentation.ui.screens.movies.components.StarRating
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import com.example.reely.domain.model.PublicRating
import com.example.reely.domain.model.genresText
import com.example.reely.domain.model.getAvailablePosterPath
import com.example.reely.domain.model.runtimeText
import com.example.reely.presentation.navigation.MovieDetailsRoute
import dagger.hilt.android.lifecycle.HiltViewModel


data class MovieDetailsScreenState(
    val movie: Movie? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

@Composable
fun MovieDetailsScreen(
    onBack : () -> Unit = {},
    viewModel: MovieDetailsScreenViewModel = viewModel()
) {

    val state = viewModel.state.collectAsState().value

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                color = MaterialTheme.colorScheme.background
            )
    ) {
        Column {

            PosterHeader(
                isLoading = state.isLoading,
                posterPath = state.movie?.getAvailablePosterPath(),
                title = state.movie?.title,
                releaseDate = state.movie?.releaseDate,
                duration = state.movie?.runtimeText(),
                genres = state.movie?.genresText()
            )

            if (state.movie!=null){
                NotWatchedMovieScreen(
                    description = state.movie.overview,
                    averageRating = state.movie.publicRating?.average,
                    userCount = state.movie.publicRating?.voteCount
                )
            }




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

@HiltViewModel
class MovieDetailsScreenViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val loadMovieDataUseCase: LoadMovieDataUseCase
): ViewModel(){

    private val route = savedStateHandle.toRoute<MovieDetailsRoute>()
    private val _state = MutableStateFlow(MovieDetailsScreenState())
    val state = _state.asStateFlow()


    init{
        loadMovieDetails(route.movieId)
    }

    fun loadMovieDetails(movieId: Int){
        viewModelScope.launch {
            _state.update {
                it.copy(
                    isLoading = true
                )
            }

            loadMovieDataUseCase(movieId)
                .onSuccess { movie ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            movie = movie
                        )
                    }
                }
                .onFailure { error ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = error.message + "\n" + error.cause
                        )
                    }
                }

        }
    }


}

@Composable
fun NotWatchedMovieScreen(
    description: String?,
    averageRating: Double?,
    userCount: Int?

){

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState())

    ) {

        Spacer(Modifier.height(16.dp))

        StarRating(score = averageRating)

        Text(
            text = String.format(
                Locale.US,
                "Globalne oceny: %.1f · %,d ocen",
                averageRating,
                userCount
            ),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
        )

        Spacer(Modifier.height(16.dp))

        SmallSectionHeader("O filmie:")

        Spacer(Modifier.height(8.dp))

        Text(
            text = description.toString(),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
            lineHeight = 20.sp
        )

    }
}

@Composable
fun WatchedMovieScreen(
    movie : Movie
) {

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
                    score = movie.ourAverageRatingRounded()
                )
                Spacer(
                    Modifier.width(8.dp)
                )

                RatingResults(
                    movie = movie,
                    style = MaterialTheme.typography.labelLarge
                )
                if (movie.publicRating != null) {
                    Row {
                        Text(
                            text = String.format(
                                Locale.US,
                                "Globalne oceny: %.1f · %,d ocen",
                                movie.publicRating.average,
                                movie.publicRating.voteCount
                            ),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                        )
                        Spacer(Modifier.weight(1f))
                    }
                }
            }

        }

        Spacer(Modifier.height(8.dp))

        OurStatsSection(
            textOverride = null
        )

        Spacer(Modifier.height(16.dp))

        Text(
            text = "Obejrzany · przez",
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