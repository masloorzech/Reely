package com.example.reely.presentation.ui

import com.example.reely.domain.model.MovieDetails
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

fun String?.formatWatchedDate(): String {
    if (this.isNullOrBlank()) return "Data nieznana"

    return runCatching {
        val date = LocalDate.parse(this.take(10))
        val formatter = DateTimeFormatter.ofPattern(
            "d MMMM yyyy",
            Locale.forLanguageTag("pl-PL")
        )

        date.format(formatter)
    }.getOrElse {
        this
    }
}


fun MovieDetails.runtimeText(): String? {
    val totalMinutes = runtimeMinutes
        ?.takeIf { it > 0 }
        ?: return null

    val hours = totalMinutes / 60
    val minutes = totalMinutes % 60

    return when {
        hours == 0 -> "$minutes min"
        minutes == 0 -> "$hours godz."
        else -> "$hours godz. $minutes min"
    }
}

fun MovieDetails.genresText(): String? {
    return genres
        .map { genre -> genre.name.trim() }
        .filter { name -> name.isNotEmpty() }
        .takeIf { names -> names.isNotEmpty() }
        ?.joinToString(separator = " • ")
}


fun Int.toRuntimeText(): String? {
    val totalMinutes = takeIf { it > 0 }
        ?: return null

    val hours = totalMinutes / 60
    val minutes = totalMinutes % 60

    return when {
        hours == 0 -> "$minutes min"
        minutes == 0 -> "$hours godz."
        else -> "$hours godz. $minutes min"
    }
}