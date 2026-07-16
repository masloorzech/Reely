package com.example.reely.domain.model

import java.time.Instant

data class MovieRating(
    val movieId: Int,
    val profileId: ProfileId,
    val rating: Rating,
    val createdAt: Instant,
    val updatedAt: Instant
)
