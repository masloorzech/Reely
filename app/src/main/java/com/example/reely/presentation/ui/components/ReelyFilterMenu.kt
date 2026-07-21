package com.example.reely.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.composables.icons.lucide.Funnel
import com.composables.icons.lucide.Lucide
import com.example.reely.presentation.theme.InputBackground
import com.example.reely.presentation.theme.ReelyTheme

@Composable
fun ReelyFilterMenu(
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    trigger: @Composable (
        onClick: () -> Unit
    ) -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    Box {
        trigger {
            onExpandedChange(true)
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = {
                onExpandedChange(false)
            },
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomEnd = 16.dp,
                bottomStart = 16.dp
            ),
            offset = DpOffset(
                y = 8.dp,
                x = 0.dp
            ),
            modifier = Modifier.background(InputBackground)
                .width(360.dp),
            content = {
                Column(
                    modifier = Modifier.padding(horizontal = 8.dp)
                ) {
                    content()
                }
            }
        )
    }
}


@Preview(showBackground = true)
@Composable
private fun FilterMenuPreview() {
    ReelyTheme {
        var expanded by remember {
            mutableStateOf(true)
        }

        val genres = listOf(
            "Akcja",
            "Komedia",
            "Dramat",
            "Horror",
            "Thriller",
            "Sci-Fi",
            "Fantasy",
            "Romans",
            "Animacja",
            "Dokumentalny",
            "Kryminał",
            "Przygodowy"
        )

        var selectedGenres by remember {
            mutableStateOf(setOf("Akcja", "Sci-Fi"))
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(120.dp)
        ) {
            ReelyFilterMenu(
                expanded = expanded,
                onExpandedChange = { expanded = it },
                trigger = { onClick ->
                    BadgedBox(
                        badge = {
                            if (selectedGenres.isNotEmpty()) {
                                Box(
                                    modifier = Modifier
                                        .offset(x = 2.dp, y = (-2).dp)
                                        .background(
                                            color = MaterialTheme.colorScheme.primary,
                                            shape = RoundedCornerShape(100)
                                        )
                                        .size(20.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = selectedGenres.size.toString(),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onPrimary
                                    )
                                }
                            }
                        }
                    ) {
                        ReelyIconButton(
                            icon = Lucide.Funnel,
                            onClick = onClick
                        )
                    }
                }
            ) {

                ReelyButton(
                    text = "Wyczyść filtry",
                    overrideContainerColor = MaterialTheme.colorScheme.background.copy(alpha = 0.6f),
                    onClick = {
                        selectedGenres = emptySet()
                        expanded = false
                    }
                )

                Spacer(Modifier.height(4.dp))

                HorizontalDivider()

                Text(
                    text = "Gatunki",
                    style = MaterialTheme.typography.labelMedium,
                )

                Spacer(Modifier.height(4.dp))

                val rowsCount = 2

                val genreRows = remember(genres) {
                    List(rowsCount) { rowIndex ->
                        genres.filterIndexed { index, _ ->
                            index % rowsCount == rowIndex
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .width(240.dp)
                        .horizontalScroll(rememberScrollState())
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        genreRows.forEach { rowGenres ->
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                rowGenres.forEach { genre ->
                                    ReelyChip(
                                        text = genre,
                                        selected = genre in selectedGenres,
                                        onClick = {
                                            selectedGenres =
                                                if (genre in selectedGenres) {
                                                    selectedGenres - genre
                                                } else {
                                                    selectedGenres + genre
                                                }
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}