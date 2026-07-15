package com.example.reely.domain.model

import java.time.Instant

data class WatchlistEntry(
    val movie: MovieDetails,
    val addedAt: Instant,
)