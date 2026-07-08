package com.example.reely.presentation.ui.model

import com.example.reely.domain.model.User
import com.example.reely.domain.model.UserMovieWatch

data class UserRatingItems(
    val user: User,
    val watchedMovies: UserMovieWatch?
)