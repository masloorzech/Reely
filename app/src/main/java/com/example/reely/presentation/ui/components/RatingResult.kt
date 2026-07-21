package com.example.reely.presentation.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.reely.domain.model.RecentWatchedMovie
import java.util.Locale


@Composable
fun RatingResults(
    modifier: Modifier = Modifier,
    movie: RecentWatchedMovie,
    style: TextStyle = MaterialTheme.typography.titleSmall,
) {


    Column(
        modifier = modifier.padding(bottom = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            val rating = String
                .format(Locale.US, "%.1f", movie.averageUserRating)
                .removeSuffix(".0")

            Text(
                text = buildAnnotatedString {
                    withStyle(
                        SpanStyle(
                            baselineShift = BaselineShift.Superscript,
                            fontSize = 10.sp
                        )
                    ) {
                        append(rating)
                    }

                    append("⁄")

                    withStyle(
                        SpanStyle(
                            baselineShift = BaselineShift.Subscript,
                            fontSize = 10.sp
                        )
                    ) {
                        append("10")
                    }
                },
                style = style,
                color = MaterialTheme.colorScheme.tertiary
            )
        }
    }
}