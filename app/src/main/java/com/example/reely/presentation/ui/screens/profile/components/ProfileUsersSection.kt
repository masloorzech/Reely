package com.example.reely.presentation.ui.screens.profile.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.reely.domain.model.Profile
import com.example.reely.domain.model.ProfilePosition
import com.example.reely.presentation.ui.components.SmallSectionHeader

@Composable
fun ProfileUsersSection(
    profiles: List<Profile>,
    isLoading: Boolean,
    error: String?,
    onProfileClick: (Profile) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
    ) {
        SmallSectionHeader(
            sectionName = "Wasze profile",
        )

        Spacer(Modifier.size(12.dp))

        when {
            isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
            }

            error != null -> {
                Text(
                    text = error,
                    style =
                        MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error,
                )
            }

            profiles.isEmpty() -> {
                Text(
                    text = "Nie znaleziono profili.",
                    style =
                        MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme
                        .onBackground
                        .copy(alpha = 0.6f),
                )
            }

            else -> {
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement =
                        Arrangement.spacedBy(24.dp),
                ) {
                    items(
                        items = profiles,
                        key = { profile ->
                            profile.id.toString()
                        },
                    ) { profile ->
                        ProfileItem(
                            profile = profile,
                            userColor = if (profile.position == ProfilePosition.FIRST) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary,
                            onClick = {
                                onProfileClick(profile)
                            },
                        )
                    }
                }

                Spacer(Modifier.size(12.dp))

                Text(
                    text = "Kliknij profil, aby zmienić nazwę lub awatar.",
                    style =
                        MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme
                        .onBackground
                        .copy(alpha = 0.6f),
                )
            }
        }
    }
}

@Composable
private fun ProfileItem(
    profile: Profile,
    userColor: Color,
    onClick: () -> Unit,
) {
    Column(
        horizontalAlignment =
            Alignment.CenterHorizontally,
        verticalArrangement =
            Arrangement.spacedBy(6.dp),
        modifier = Modifier.clickable(
            onClick = onClick,
        ),
    ) {
        AvatarImage(
            imagePath = profile.avatarPath,
            size = 88.dp,
            modifier = Modifier.border(
                width = 2.dp,
                color = userColor.copy(alpha = 0.5f),
                shape = CircleShape,
            ),
        )

        Text(
            text = profile.name,
            style =
                MaterialTheme.typography.labelLarge,
            color =
                MaterialTheme.colorScheme.onBackground,
        )

        Text(
            text = profile.position.displayName(),
            style =
                MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme
                .onBackground
                .copy(alpha = 0.5f),
        )
    }
}

private fun ProfilePosition.displayName(): String {
    return when (this) {
        ProfilePosition.FIRST -> "Osoba 1"
        ProfilePosition.SECOND -> "Osoba 2"
    }
}