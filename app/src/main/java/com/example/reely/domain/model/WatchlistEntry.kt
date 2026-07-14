package com.example.reely.domain.model

import java.time.Instant

data class WatchlistEntry(
    val movieId: Int,
    val addedAt: Instant
)