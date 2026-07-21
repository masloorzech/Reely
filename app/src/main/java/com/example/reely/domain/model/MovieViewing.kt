package com.example.reely.domain.model

import java.time.Instant
import java.time.LocalDate

data class MovieViewing(
    val id: Long,
    val movieId: Int,
    val watchedAt: LocalDate,
    val createdAt: Instant,
    val updatedAt: Instant
)
