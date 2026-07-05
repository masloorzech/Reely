package com.example.reely.presentation.ui.screens.search.components

import androidx.compose.foundation.background
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
import androidx.compose.ui.unit.dp
import com.example.reely.presentation.ui.components.SmallSectionHeader

@Composable
fun RecentSearchesSection(
    modifier : Modifier = Modifier,
    recentSearches: List<String> = emptyList(),
    onClick : () -> Unit = {}
){

    SmallSectionHeader("Ostatnie wyszukiwania")
    Spacer(modifier = Modifier.height(8.dp))
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(recentSearches){ recentSearch ->
            RecentSearchChip(
                searchedText = recentSearch
            )
        }
    }


}

@Composable
fun RecentSearchChip(
    searchedText: String
){

    Box(
        modifier = Modifier
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(16.dp)
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