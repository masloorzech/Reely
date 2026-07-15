package com.example.reely.presentation.ui.screens.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.reely.domain.model.MoviePreview
import com.example.reely.presentation.ui.components.SmallSectionHeader
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

private val WEEKDAY_NAMES = listOf(
    "Pn",
    "Wt",
    "Śr",
    "Cz",
    "Pt",
    "Sb",
    "Nd"
)

@Composable
fun WatchedMoviesCalendarSection(
    month: YearMonth,
    moviesByDate: Map<LocalDate, List<MoviePreview>>,
    modifier: Modifier = Modifier
) {
    val calendarCells = remember(month) {
        buildCalendarCells(month)
    }

    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        SmallSectionHeader(
            sectionName = "Obejrzane w ${month.toPolishTitle()}:"
        )

        Spacer(Modifier.size(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            WEEKDAY_NAMES.forEach { weekday ->
                Text(
                    text = weekday,
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onBackground.copy(
                        alpha = 0.55f
                    ),
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(Modifier.size(6.dp))

        calendarCells
            .chunked(7)
            .forEach { week ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    week.forEach { date ->
                        Box(
                            modifier = Modifier.weight(1f)
                        ) {
                            if (date == null) {
                                Spacer(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .aspectRatio(0.72f)
                                )
                            } else {
                                WatchedCalendarDay(
                                    date = date,
                                    movies = moviesByDate[date].orEmpty()
                                )
                            }
                        }
                    }
                }

                Spacer(Modifier.size(4.dp))
            }
    }
}

@Composable
private fun WatchedCalendarDay(
    date: LocalDate,
    movies: List<MoviePreview>,
    modifier: Modifier = Modifier
) {
    val movie = movies.firstOrNull()
    val posterPath = movie?.posterPath
    val isToday = date == LocalDate.now()

    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(0.72f)
            .clip(RoundedCornerShape(7.dp))
            .background(MaterialTheme.colorScheme.surface)
            .then(
                if (isToday) {
                    Modifier.border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(7.dp)
                    )
                } else {
                    Modifier
                }
            )
    ) {
        if (posterPath != null) {
            AsyncImage(
                model = posterPath,
                contentDescription = movie.title,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .fillMaxWidth()
                    .padding(5.dp)
            ) {
                Text(
                    text = date.dayOfMonth.toString(),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        } else {
            Text(
                text = date.dayOfMonth.toString(),
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(5.dp),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface.copy(
                    alpha = 0.55f
                )
            )
        }

        if (movies.size > 1) {
            Text(
                text = "+${movies.size - 1}",
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(3.dp)
                    .clip(CircleShape)
                    .background(
                        MaterialTheme.colorScheme.primary
                    )
                    .padding(
                        horizontal = 5.dp,
                        vertical = 2.dp
                    ),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onPrimary,
                maxLines = 1,
                overflow = TextOverflow.Clip
            )
        }
    }
}

private fun buildCalendarCells(
    month: YearMonth
): List<LocalDate?> {
    val firstDay = month.atDay(1)

    val emptyCellsBefore = firstDay.dayOfWeek.value - 1

    val days = (1..month.lengthOfMonth()).map { day ->
        month.atDay(day)
    }

    val usedCells = emptyCellsBefore + days.size
    val emptyCellsAfter = (7 - usedCells % 7) % 7

    return buildList {
        repeat(emptyCellsBefore) {
            add(null)
        }

        addAll(days)

        repeat(emptyCellsAfter) {
            add(null)
        }
    }
}

private fun YearMonth.toPolishTitle(): String {
    val locale = Locale.forLanguageTag("pl-PL")

    val monthName = month.getDisplayName(
        TextStyle.FULL_STANDALONE,
        locale
    )

    return "$monthName $year"
}