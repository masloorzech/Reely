package com.example.reely.domain.model

data class MoviePreview(
    val id: Int,
    val title: String,
    val posterPath: String?,
    val releaseYear: Int?
)