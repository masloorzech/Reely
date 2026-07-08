package com.example.reely.presentation.ui.screens.movies.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun RatingStars(
    rating: Int,
    enabled: Boolean,
    modifier: Modifier = Modifier,
    onRatingChange: (Int) -> Unit
) {
    val safeRating = rating.coerceIn(0, 10)


    Row(
        modifier = modifier
            .fillMaxWidth()
            .alpha(if (enabled) 1f else 0.45f),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        (1..10).forEach { star ->
            val selected = star <= safeRating

            val tint by animateColorAsState(
                targetValue = if (selected) {
                    MaterialTheme.colorScheme.tertiary
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.28f)
                },
                label = "starTint"
            )

            IconButton(
                enabled = enabled,
                onClick = {
                    onRatingChange(star)
                },
                modifier = Modifier.size(30.dp)
            ) {
                Icon(
                    imageVector = if (selected) {
                        Icons.Filled.Star
                    } else {
                        Icons.Outlined.Star
                    },
                    contentDescription = "Ocena $star",
                    tint = tint,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    }
}