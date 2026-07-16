package com.example.reely.domain.model

import java.time.LocalDate
import java.time.Instant

data class MovieViewing(
    val id: Long,
    val movieId: Int,
    val watchedAt: LocalDate,
    val createdAt: Instant,
    val updatedAt: Instant
)
