package com.example.reely.presentation.ui

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