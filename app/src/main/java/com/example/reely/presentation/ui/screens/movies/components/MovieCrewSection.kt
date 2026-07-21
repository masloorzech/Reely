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
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.reely.domain.model.CrewMember
import com.example.reely.presentation.ui.components.SmallSectionHeader
import com.example.reely.presentation.ui.screens.movies.CrewUiItem
import com.example.reely.presentation.ui.screens.movies.toCrewUiItems

@Composable
fun MovieCrewSection(
    crew: List<CrewMember>?,
    isLoading: Boolean,
    modifier: Modifier = Modifier
){
    val crewItems = remember(crew) {
        crew?.toCrewUiItems()
    }

    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        SmallSectionHeader(
            sectionName = "Twórcy:"
        )

        Spacer(Modifier.height(8.dp))

        when {
            isLoading -> {
                Text(
                    text = "Ładowanie twórców...",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onBackground.copy(
                        alpha = 0.6f
                    )
                )
            }

            crewItems != null -> when {

                crewItems.isEmpty() -> {
                    Text(
                        text = "Brak informacji o twórcach.",
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
                            items = crewItems,
                            key = { item: CrewUiItem -> item.personId }
                        ) { item: CrewUiItem ->
                            CrewMemberItem(
                                item = item
                            )
                        }
                    }
                }
            }
        }
    }
}