package com.example.reely.presentation.mock

import com.example.reely.domain.model.Movie
import com.example.reely.domain.model.MovieUserState
import com.example.reely.domain.model.PosterPaths
import com.example.reely.domain.model.PublicRating
import com.example.reely.domain.model.UserId

val antoniId = UserId(1)
val secondUserId = UserId(2)

val mockMovie = Movie(
    id = 475557,
    title = "Joker",
    overview = "Arthur Fleck, samotny komik zmagający się z odrzuceniem społecznym i chorobą psychiczną, stopniowo przemienia się w jedną z najbardziej niepokojących postaci Gotham.",
    posterPaths = PosterPaths(
        localPosterPath = null,
        remotePosterPath = "/udDclJoHjfjb8Ekgsd4FDteOkCU.jpg"
    ),
    releaseDate = "2019-10-04",
    publicRating = PublicRating(
        average = 8.1,
        voteCount = 26000
    ),
    userStates = mapOf(
        antoniId to MovieUserState.Watched(
            watchedAt = "2024-07-04",
            rating = 8.5
        ),
        secondUserId to MovieUserState.Watched(
            watchedAt = "2024-07-05",
            rating = 9.0
        )
    )
)