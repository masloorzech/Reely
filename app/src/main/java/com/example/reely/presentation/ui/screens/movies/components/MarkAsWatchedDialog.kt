package com.example.reely.presentation.ui.screens.movies.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.reely.domain.model.Profile
import com.example.reely.domain.model.ProfilePosition
import com.example.reely.domain.model.Rating
import com.example.reely.presentation.ui.components.ReelyButton
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun MarkAsWatchedDialog(
    profiles: List<Profile>,
    initialWatchedAt: LocalDate,
    initialFirstRating: Rating?,
    initialSecondRating: Rating?,
    showWatchedDate: Boolean,
    isLoading: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (
        watchedAt: LocalDate,
        firstRating: Rating,
        secondRating: Rating,
    ) -> Unit,
) {
    val firstProfile = profiles.firstOrNull { profile ->
        profile.position == ProfilePosition.FIRST
    }

    val secondProfile = profiles.firstOrNull { profile ->
        profile.position == ProfilePosition.SECOND
    }

    val hasRequiredProfiles =
        firstProfile != null && secondProfile != null

    var firstRatingValue by remember(initialFirstRating) {
        mutableIntStateOf(
            initialFirstRating?.value ?: DEFAULT_RATING,
        )
    }

    var secondRatingValue by remember(initialSecondRating) {
        mutableIntStateOf(
            initialSecondRating?.value ?: DEFAULT_RATING,
        )
    }

    var watchedAtEpochDay by remember(initialWatchedAt) {
        mutableLongStateOf(initialWatchedAt.toEpochDay())
    }

    var isDatePickerVisible by remember {
        mutableStateOf(false)
    }

    val watchedAt = LocalDate.ofEpochDay(watchedAtEpochDay)

    Dialog(
        onDismissRequest = {
            if (!isLoading) {
                onDismiss()
            }
        },
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
        ),
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.BottomCenter,
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
                    .padding(bottom = 12.dp)
                    .navigationBarsPadding(),
                shape = RoundedCornerShape(30.dp),
                color = MaterialTheme.colorScheme.background,
                tonalElevation = 6.dp,
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = 20.dp,
                            vertical = 18.dp,
                        ),
                    verticalArrangement =
                        Arrangement.spacedBy(18.dp),
                ) {
                    DialogHandle()

                    Column(
                        verticalArrangement =
                            Arrangement.spacedBy(4.dp),
                    ) {
                        Text(
                            text = if (showWatchedDate) {
                                "Oznacz jako obejrzany"
                            } else {
                                "Edytuj oceny"
                            },
                            style =
                                MaterialTheme.typography.headlineSmall,
                            color =
                                MaterialTheme.colorScheme.onSurface,
                        )

                        Text(
                            text = if (hasRequiredProfiles) {
                                "Wybierz ocenę dla każdej osoby."
                            } else {
                                "Brakuje dwóch skonfigurowanych profili."
                            },
                            style =
                                MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme
                                .colorScheme
                                .onSurfaceVariant,
                        )
                    }

                    if (showWatchedDate) {
                        WatchedDateSection(
                            watchedAt = watchedAt,
                            enabled = !isLoading,
                            onDateClick = {
                                isDatePickerVisible = true
                            },
                        )
                    }

                    if (firstProfile != null) {
                        ProfileRatingSection(
                            profile = firstProfile,
                            rating = firstRatingValue,
                            enabled = !isLoading,
                            onRatingChange = { rating ->
                                firstRatingValue = rating
                            },
                        )
                    }

                    if (
                        firstProfile != null &&
                        secondProfile != null
                    ) {
                        DialogDivider()
                    }

                    if (secondProfile != null) {
                        ProfileRatingSection(
                            profile = secondProfile,
                            rating = secondRatingValue,
                            enabled = !isLoading,
                            onRatingChange = { rating ->
                                secondRatingValue = rating
                            },
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement =
                            Arrangement.spacedBy(10.dp),
                        verticalAlignment =
                            Alignment.CenterVertically,
                    ) {
                        ReelyButton(
                            modifier = Modifier.weight(1f),
                            text = "Anuluj",
                            enabled = !isLoading,
                            overrideContainerColor =
                                MaterialTheme
                                    .colorScheme
                                    .surfaceVariant,
                            onClick = onDismiss,
                        )

                        ReelyButton(
                            modifier = Modifier.weight(1f),
                            text = if (isLoading) {
                                "Zapisywanie..."
                            } else {
                                "Zapisz"
                            },
                            enabled =
                                !isLoading && hasRequiredProfiles,
                            onClick = {
                                onConfirm(
                                    watchedAt,
                                    Rating(firstRatingValue),
                                    Rating(secondRatingValue),
                                )
                            },
                        )
                    }
                }
            }
        }
    }

    if (isDatePickerVisible) {
        WatchedDatePicker(
            initialDate = watchedAt,
            onDismiss = {
                isDatePickerVisible = false
            },
            onDateSelected = { selectedDate ->
                watchedAtEpochDay = selectedDate.toEpochDay()
                isDatePickerVisible = false
            },
        )
    }
}

@Composable
private fun DialogHandle() {
    Box(
        modifier = Modifier
            .width(36.dp)
            .height(4.dp)
            .clip(RoundedCornerShape(999.dp))
            .background(
                MaterialTheme.colorScheme
                    .onSurface
                    .copy(alpha = 0.18f),
            ),
    )
}

@Composable
private fun WatchedDateSection(
    watchedAt: LocalDate,
    enabled: Boolean,
    onDateClick: () -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(
            text = "Data oglądania",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        ReelyButton(
            modifier = Modifier.fillMaxWidth(),
            text = watchedAt.toDisplayDate(),
            enabled = enabled,
            overrideContainerColor =
                MaterialTheme.colorScheme.surfaceVariant,
            onClick = onDateClick,
        )
    }
}

@Composable
private fun ProfileRatingSection(
    profile: Profile,
    rating: Int,
    enabled: Boolean,
    onRatingChange: (Int) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = profile.name,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface,
            )

            Spacer(Modifier.weight(1f))

            Text(
                text = "$rating/10",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.tertiary,
            )
        }

        RatingStars(
            rating = rating,
            enabled = enabled,
            onRatingChange = onRatingChange,
        )
    }
}

@Composable
private fun DialogDivider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(
                MaterialTheme.colorScheme
                    .onSurface
                    .copy(alpha = 0.07f),
            ),
    )
}

@Composable
private fun WatchedDatePicker(
    initialDate: LocalDate,
    onDismiss: () -> Unit,
    onDateSelected: (LocalDate) -> Unit,
) {
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis =
            initialDate.toUtcMillis(),
    )

    val selectedDate = datePickerState
        .selectedDateMillis
        ?.toUtcLocalDate()

    val isSelectedDateValid =
        selectedDate != null &&
                !selectedDate.isAfter(LocalDate.now())

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                enabled = isSelectedDateValid,
                onClick = {
                    selectedDate?.let(onDateSelected)
                },
            ) {
                Text("Wybierz")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
            ) {
                Text("Anuluj")
            }
        },
    ) {
        DatePicker(
            state = datePickerState,
        )
    }
}

private fun LocalDate.toUtcMillis(): Long {
    return atStartOfDay(ZoneOffset.UTC)
        .toInstant()
        .toEpochMilli()
}

private fun Long.toUtcLocalDate(): LocalDate {
    return Instant
        .ofEpochMilli(this)
        .atZone(ZoneOffset.UTC)
        .toLocalDate()
}

private fun LocalDate.toDisplayDate(): String {
    return format(
        DateTimeFormatter.ofPattern(
            "d MMMM yyyy",
            Locale.forLanguageTag("pl-PL"),
        )
    )
}

private const val DEFAULT_RATING = 5