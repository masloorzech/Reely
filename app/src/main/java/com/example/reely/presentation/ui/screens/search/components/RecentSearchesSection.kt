package com.example.reely.presentation.ui.screens.search.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.example.reely.presentation.ui.components.SmallSectionHeader


private val SEARCHES_DEFAULTS = listOf(
    "Skazani na Shawshank",
    "Ojciec chrzestny",
    "Mroczny Rycerz",
    "Ojciec chrzestny II",
    "Dwunastu gniewnych ludzi",
    "Władca Pierścieni: Powrót króla"
)

@Composable
fun RecentSearchesSection(
    modifier : Modifier = Modifier,
    recentSearches: List<String> = emptyList(),
    onClick : (String) -> Unit = {}
){

    val searches: List<String> = recentSearches + SEARCHES_DEFAULTS.takeLast(6-recentSearches.size)

    SmallSectionHeader("Ostatnie wyszukiwania")

    Spacer(modifier = Modifier.height(8.dp))
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(searches){ recentSearch ->
            RecentSearchChip(
                searchedText = recentSearch,
                onClick = onClick
            )
        }
    }


}

@Composable
fun RecentSearchChip(
    searchedText: String,
    onClick: (String) -> Unit
){

    Box(
        modifier = Modifier
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(16.dp)
            )
            .clip(RoundedCornerShape(16.dp))
            .clickable(
                onClick = {
                    onClick(searchedText)
                }
            )
            .padding(8.dp)
    ){
        Text(
            text = searchedText,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onBackground
        )
    }

}