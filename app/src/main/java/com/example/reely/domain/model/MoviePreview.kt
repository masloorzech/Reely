package com.example.reely.domain.model

import java.time.LocalDate

data class MoviePreview(
    val id: Int,
    val title: String,
    val posterPath: String?,
    val releaseDate: LocalDate?
)