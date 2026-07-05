package com.example.reely.data.remote.tmdb

object TmdbImageConfig {
    const val IMAGE_BASE_URL = "https://image.tmdb.org/t/p/"
    const val POSTER_SIZE = "w500"
}

fun String.toTmdbPosterUrl(): String {
    return "${TmdbImageConfig.IMAGE_BASE_URL}${TmdbImageConfig.POSTER_SIZE}$this"
}