package com.example.reely.presentation.ui.screens.list

import android.media.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.composables.icons.lucide.Bookmark
import com.composables.icons.lucide.Clapperboard
import com.composables.icons.lucide.Grid2x2
import com.composables.icons.lucide.Grip
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.TvMinimalPlay
import com.example.reely.presentation.theme.ReelyTheme
import com.example.reely.presentation.ui.components.ReelyIconButton
import com.example.reely.presentation.ui.components.SmallSectionHeader
import com.example.reely.presentation.ui.components.ReelyListTabs
import com.example.reely.presentation.ui.components.ReelyMenuButton
import com.example.reely.presentation.ui.components.ReelySearchField
import com.example.reely.presentation.ui.components.ReelyTab


enum class MovieListTab(
    override val tabTitle: String,
    override val tabIcon: ImageVector?
) : ReelyTab {
    WATCHED(
        tabTitle = "Obejrzane",
        tabIcon = Lucide.Clapperboard
    ),

    WATCHLIST(
        tabTitle = "Do obejrzenia",
        tabIcon = Lucide.TvMinimalPlay
    ),

    FAVOURITE(
      tabTitle = "Ulubione",
        tabIcon = Lucide.Bookmark
    )
}

enum class MovieSortOption(
    val title: String
) {
    RATING_DESC("Najwyżej oceniane"),
    NEWEST("Najnowsze"),
    OLDEST("Najstarsze"),
    TITLE_ASC("Alfabetycznie")
}

@Composable
fun ListScreen(
    modifier : Modifier = Modifier
) {

    var selectedSort by rememberSaveable {
        mutableStateOf(MovieSortOption.RATING_DESC)
    }

    var selectedTab by rememberSaveable {
        mutableStateOf(MovieListTab.WATCHED)
    }

    var searchText by remember { mutableStateOf("") }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        )
        {

            SmallSectionHeader(
                sectionName = "Twoje listy"
            )

            Spacer(modifier = Modifier.height(16.dp))

            ReelyListTabs(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it as MovieListTab }
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row() {
                ReelyMenuButton(
                    selectedOption = selectedSort,
                    options = MovieSortOption.entries,
                    optionLabel = { it.title },
                    onOptionSelected = { selectedSort = it }
                )
                Spacer(Modifier.weight(1f))
                ReelyIconButton(
                    onClick = {},
                    icon = Lucide.Grid2x2
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            //Lista

            ReelySearchField(
                value = searchText,
                onValueChange = { searchText = it },
                placeholder = "Wyszukaj film w sekcji ${selectedTab.tabTitle.lowercase()}...",
                onSearchClick = {
                }
            )

            //Button navigacji do szukaj :D
        }
    }
}

@Preview
@Composable
fun ListScreenPreview(){
    ReelyTheme {
        ListScreen()
    }
}