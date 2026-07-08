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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.example.reely.domain.model.Movie
import java.util.Locale


@Composable
fun RatingResults(
    modifier: Modifier = Modifier,
    movie: Movie,
    style: TextStyle = MaterialTheme.typography.titleSmall,
) {

    val averageRating = 9.0

    val mood = RatingMood.fromScore(averageRating)

    Column(
        modifier = modifier.padding(bottom = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = String.format(Locale.US, "%.1f", averageRating),
                style = style,
                color = mood.color
            )
        }
    }
}

enum class RatingMood(
    val minScore: Double?,
    val color: Color,
) {
    Unrated(
        minScore = null,
        color = Color(0xFF94A3B8),
    ),

    Dead(
        minScore = 0.0,
        color = Color(0xFFEF4444),
    ),

    Sad(
        minScore = 3.0,
        color = Color(0xFFF97316),
    ),

    Neutral(
        minScore = 5.0,
        color = Color(0xFFEAB308),
    ),

    Okay(
        minScore = 6.0,
        color = Color(0xFFA3E635),
    ),

    Good(
        minScore = 7.0,
        color = Color(0xFF22C55E),
    ),

    Great(
        minScore = 8.0,
        color = Color(0xFF38BDF8),
    ),

    Fire(
        minScore = 9.0,
        color = Color(0xFFA78BFA),
    );

    companion object {
        fun fromScore(score: Double?): RatingMood {
            if (score == null) return Unrated

            return when {
                score >= Fire.minScore!! -> Fire
                score >= Great.minScore!! -> Great
                score >= Good.minScore!! -> Good
                score >= Okay.minScore!! -> Okay
                score >= Neutral.minScore!! -> Neutral
                score >= Sad.minScore!! -> Sad
                else -> Dead
            }
        }
    }
}