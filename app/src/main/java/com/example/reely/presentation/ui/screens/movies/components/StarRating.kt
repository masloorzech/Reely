package com.example.reely.presentation.ui.screens.movies.components

import android.graphics.drawable.Icon
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.StarRate
import androidx.compose.material.icons.outlined.StarRate
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.unit.dp
import com.example.reely.presentation.ui.components.RatingMood
import kotlin.math.floor

@Composable
fun StarRating(
    modifier: Modifier = Modifier,
    score: Double?,
    starColor: Color? = null, ) {

    if (score == null) {
        return
    }

    val maxStars = 10
    val safeScore = score.coerceIn(0.0, maxStars.toDouble())

    val fullStars = floor(safeScore).toInt()
    val fraction = (safeScore - fullStars).toFloat()

    val hasPartialStar = fraction > 0f
    val emptyStars = maxStars - fullStars - if (hasPartialStar) 1 else 0

    val ratingMood = RatingMood.fromScore(score)

    Row(
        modifier = modifier.padding(bottom = 8.dp)
    ) {
        repeat(fullStars) {
            Icon(
                imageVector = Icons.Filled.StarRate,
                contentDescription = "",
                tint = starColor?: ratingMood.color,
                modifier = Modifier.size(32.dp)
            )
        }

        if (hasPartialStar) {
            Box(
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.StarRate,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                    modifier = Modifier.size(32.dp)
                )

                Icon(
                    imageVector = Icons.Filled.StarRate,
                    contentDescription = null,
                    tint = starColor?: ratingMood.color,
                    modifier = Modifier
                        .size(32.dp)
                        .drawWithContent {
                            val contentScope = this

                            clipRect(right = size.width * fraction) {
                                contentScope.drawContent()
                            }
                        }
                )
            }
        }

        repeat(emptyStars) {
            Icon(
                imageVector = Icons.Filled.StarRate,
                contentDescription = "",
                tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                modifier = Modifier.size(32.dp)
            )
        }
    }
}