package com.example.reely.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.reely.domain.model.Movie
import com.example.reely.domain.model.averageRatingRounded
import java.util.Locale


private data class RatingMood(
    val icon: String,
    val color: Color,
)

@Composable
fun RatingResults(
    movie: Movie,
    modifier: Modifier = Modifier
) {

    val averageRating = movie.averageRatingRounded()

    val mood = ratingMood(averageRating)

    Column(
        modifier = modifier.padding(bottom = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = mood.icon,
                style = MaterialTheme.typography.titleSmall
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = String.format(Locale.US, "%.1f", averageRating),
                style = MaterialTheme.typography.titleSmall,
                color = mood.color
            )
        }
    }
}

@Composable
private fun ratingMood(score: Double?): RatingMood {
    if (score == null) {
        val color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.45f)

        return RatingMood(
            icon = "—",
            color = color,
        )
    }

    return when {
        score >= 9.0 -> RatingMood(
            icon = "🔥",
            color = Color(0xFFFFB86B),
        )

        score >= 8.0 -> RatingMood(
            icon = "💜",
            color = Color(0xFFA78BFA),
        )

        score >= 7.0 -> RatingMood(
            icon = "⭐",
            color = Color(0xFFFFD166),
        )

        score >= 6.0 -> RatingMood(
            icon = "✅",
            color = Color(0xFF4ADE80),
        )

        score >= 5.0 -> RatingMood(
            icon = "😐",
            color = Color(0xFFCBD5E1),
        )

        score >= 3.0 -> RatingMood(
            icon = "🥲",
            color = Color(0xFFFF8A65),
        )

        else -> RatingMood(
            icon = "💀",
            color = Color(0xFFFF6B6B),
        )
    }
}