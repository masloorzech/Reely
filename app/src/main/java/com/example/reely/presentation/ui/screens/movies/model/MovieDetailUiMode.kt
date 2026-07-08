package com.example.reely.presentation.ui.screens.movies.model

import com.example.reely.domain.model.Movie
import com.example.reely.domain.model.MovieUserState
import com.example.reely.domain.model.isInWatchlist

sealed interface MovieDetailsUiMode {
    data object NotWatched : MovieDetailsUiMode
    data object InWatchlist : MovieDetailsUiMode
    data object Watched : MovieDetailsUiMode
}

fun Movie.detailsUiMode(): MovieDetailsUiMode {
    return when {
        userStates.values.any { it is MovieUserState.Watched } -> {
            MovieDetailsUiMode.Watched
        }

        isInWatchlist() -> {
            MovieDetailsUiMode.InWatchlist
        }

        else -> {
            MovieDetailsUiMode.NotWatched
        }
    }
}