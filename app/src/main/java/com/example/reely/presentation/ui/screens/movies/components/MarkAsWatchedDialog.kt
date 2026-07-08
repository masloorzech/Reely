package com.example.reely.presentation.ui.screens.movies.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.reely.domain.model.User
import com.example.reely.domain.model.UserId
import com.example.reely.presentation.ui.components.RatingMood
import com.example.reely.presentation.ui.components.ReelyButton
import kotlin.collections.forEach
import kotlin.collections.set

@Composable
fun MarkAsWatchedDialog(
    users: List<User>,
    isLoading: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (Map<UserId, Double?>) -> Unit
) {
    val ratings = remember(users) {
        mutableStateMapOf<UserId, Int>().apply {
            users.forEach { user ->
                this[user.id] = 7
            }
        }
    }

    Dialog(
        onDismissRequest = {
            if (!isLoading) {
                onDismiss()
            }
        },
        properties = DialogProperties(
            usePlatformDefaultWidth = false
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.BottomCenter
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
                    .padding(bottom = 12.dp)
                    .navigationBarsPadding(),
                shape = RoundedCornerShape(30.dp),
                color = MaterialTheme.colorScheme.background,
                tonalElevation = 6.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 18.dp),
                    verticalArrangement = Arrangement.spacedBy(18.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .width(36.dp)
                            .height(4.dp)
                            .clip(RoundedCornerShape(999.dp))
                            .background(
                                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.18f)
                            )
                    )

                    Column(
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "Oceń film",
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Text(
                            text = if (users.isEmpty()) {
                                "Brak użytkowników do ocenienia."
                            } else {
                                "Wybierz ocenę dla każdej osoby."
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    if (users.isNotEmpty()) {
                        LazyColumn(
                            modifier = Modifier.heightIn(max = 380.dp),
                            verticalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            items(
                                items = users,
                                key = { user -> user.id.toString() }
                            ) { user ->
                                val rating = ratings[user.id] ?: 7
                                val mood = RatingMood.fromScore(rating.toDouble())

                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 10.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = user.nickname,
                                            style = MaterialTheme.typography.titleSmall,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )

                                        Spacer(Modifier.weight(1f))

                                        Text(
                                            text = "$rating/10",
                                            style = MaterialTheme.typography.titleMedium,
                                            color = mood.color
                                        )
                                    }

                                    RatingStars(
                                        rating = rating,
                                        enabled = !isLoading,
                                        onRatingChange = { newRating ->
                                            ratings[user.id] = newRating
                                        }
                                    )
                                }

                                if (user != users.last()) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(1.dp)
                                            .background(
                                                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.07f)
                                            )
                                    )
                                }
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        ReelyButton(
                            modifier = Modifier.weight(1f),
                            text = "Anuluj",
                            overrideContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                            onClick = {
                                if (!isLoading) {
                                    onDismiss()
                                }
                            }
                        )

                        ReelyButton(
                            modifier = Modifier.weight(1f),
                            text = if (isLoading) "Zapisywanie..." else "Zapisz",
                            onClick = {
                                if (!isLoading && users.isNotEmpty()) {
                                    onConfirm(
                                        ratings.mapValues { entry ->
                                            entry.value.toDouble()
                                        }
                                    )
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}