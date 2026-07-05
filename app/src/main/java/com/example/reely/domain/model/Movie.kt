package com.example.reely.domain.model

data class Movie(
    val id: Int,
    val title: String,
    val overview: String,
    val posterPath: String?,
    val releaseDate: String?,
    val ratings: Map<Int, Double> = emptyMap()
)

fun Movie.averageRatingRounded(): Double? {
    if (ratings.isEmpty()) return null
    return kotlin.math.round(ratings.values.average() * 10) / 10
}