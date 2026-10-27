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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.snapshotFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import java.time.temporal.ChronoUnit
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.reely.domain.model.RecentWatchedMovie
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

private const val CENTER_PAGE = 1200
private const val PAGE_COUNT = 2401

@Composable
fun WatchedMoviesCalendarSection(
    month: YearMonth,
    moviesByDate: Map<LocalDate, List<RecentWatchedMovie>>,
    onMonthChange: (YearMonth) -> Unit,
    modifier: Modifier = Modifier
) {

    val initialMonthString = rememberSaveable {
        month.toString()
    }

    val initialMonth = remember(initialMonthString) {
        YearMonth.parse(initialMonthString)
    }

    val pagerState = rememberPagerState(
        initialPage = CENTER_PAGE,
        pageCount = { PAGE_COUNT }
    )
    val currentMonth by rememberUpdatedState(month)
    val currentOnMonthChange by rememberUpdatedState(onMonthChange)


    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.settledPage }
            .distinctUntilChanged()
            .collect { page ->
                val selectedMonth = initialMonth.plusMonths(
                    (page - CENTER_PAGE).toLong()
                )

                if (selectedMonth != currentMonth) {
                    currentOnMonthChange(selectedMonth)
                }
            }
    }

    LaunchedEffect(month) {
        val offset = ChronoUnit.MONTHS.between(
            initialMonth,
            month
        )

        val targetPage = CENTER_PAGE.toLong() + offset

        if (
            targetPage in 0L until PAGE_COUNT.toLong() &&
            pagerState.currentPage != targetPage.toInt()
        ) {
            pagerState.animateScrollToPage(targetPage.toInt())
        }
    }

    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        val displayedMonth = initialMonth.plusMonths(
            (pagerState.currentPage - CENTER_PAGE).toLong()
        )

        SmallSectionHeader(
            sectionName = "Obejrzane w ${displayedMonth.toPolishTitle()}:"
        )

        Spacer(Modifier.size(12.dp))

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxWidth(),
            pageSpacing = 12.dp
        ) { page ->
            val pageMonth = initialMonth.plusMonths(
                (page - CENTER_PAGE).toLong()
            )

            CalendarMonthContent(
                month = pageMonth,
                moviesByDate = moviesByDate,
                modifier = Modifier.padding(horizontal = 6.dp)
            )
        }
    }
}

@Composable
private fun CalendarMonthContent(
    month: YearMonth,
    moviesByDate: Map<LocalDate, List<RecentWatchedMovie>>,
    modifier: Modifier = Modifier
) {
    val calendarCells = remember(month) {
        val cells = buildCalendarCells(month)
        cells + List(42 - cells.size) { null }
    }

    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            WEEKDAY_NAMES.forEach { weekday ->
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = weekday,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onBackground.copy(
                            alpha = 0.55f
                        )
                    )
                }
            }
        }

        Spacer(Modifier.size(6.dp))

        calendarCells.chunked(7).forEach { week ->
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
    movies: List<RecentWatchedMovie>,
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