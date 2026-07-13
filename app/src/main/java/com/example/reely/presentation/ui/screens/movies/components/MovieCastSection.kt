package com.example.reely.presentation.ui.screens.movies.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.reely.domain.model.CastMember
import com.example.reely.presentation.ui.components.SmallSectionHeader

@Composable
fun MovieCastSection(
    cast: List<CastMember>?,
    isLoading: Boolean,
    modifier: Modifier = Modifier
) {

    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        SmallSectionHeader(
            sectionName = "Obsada:"
        )

        Spacer(Modifier.height(8.dp))

        when {
            isLoading -> {
                Text(
                    text = "Ładowanie obsady...",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onBackground.copy(
                        alpha = 0.6f
                    )
                )
            }

            cast != null -> when{

                cast.isEmpty() -> {
                    Text(
                        text = "Brak informacji o obsadzie.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onBackground.copy(
                            alpha = 0.6f
                        )
                    )
                }

                else -> {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(
                            items = cast,
                            key = { member -> member.id }
                        ) { member ->
                            CastMemberItem(
                                castMember = member
                            )
                        }
                    }
                }
            }
        }
    }
}