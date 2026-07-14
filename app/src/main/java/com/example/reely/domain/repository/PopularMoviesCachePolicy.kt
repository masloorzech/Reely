package com.example.reely.domain.repository

object PopularMovieCachePolicy {

    const val FRESH_FOR_MILLIS: Long =
        6L * 60L * 60L * 1_000L

    const val HARD_DELETE_AFTER_MILLIS: Long =
        7L * 24L * 60L * 60L * 1_000L
}