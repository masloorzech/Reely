package com.example.reely.presentation.ui.screens.profile.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.reely.presentation.ui.components.ReelyButton
import com.example.reely.presentation.ui.components.ReelyTextField

@Composable
fun ProfileFormDialog(
    name: String,
    avatarPath: String?,
    error: String?,
    isLoading: Boolean,
    onNameChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onAvatarClick: () -> Unit,
    onConfirm: () -> Unit,
) {
    val canConfirm =
        name.trim().isNotEmpty() && !isLoading

    Dialog(
        onDismissRequest = {
            if (!isLoading) {
                onDismiss()
            }
        },
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
        ),
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.BottomCenter,
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
                    .padding(bottom = 12.dp)
                    .navigationBarsPadding(),
                shape = RoundedCornerShape(30.dp),
                color = MaterialTheme.colorScheme.background,
                tonalElevation = 6.dp,
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = 20.dp,
                            vertical = 18.dp,
                        ),
                    verticalArrangement =
                        Arrangement.spacedBy(18.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .size(
                                width = 36.dp,
                                height = 4.dp,
                            )
                            .clip(RoundedCornerShape(999.dp))
                            .background(
                                MaterialTheme.colorScheme
                                    .onSurface
                                    .copy(alpha = 0.18f),
                            ),
                    )

                    Column(
                        verticalArrangement =
                            Arrangement.spacedBy(4.dp),
                    ) {
                        Text(
                            text = "Edytuj profil",
                            style =
                                MaterialTheme.typography.headlineSmall,
                            color =
                                MaterialTheme.colorScheme.onSurface,
                        )

                        Text(
                            text = "Zmień nazwę lub zdjęcie profilu.",
                            style =
                                MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme
                                .colorScheme
                                .onSurfaceVariant,
                        )
                    }

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment =
                            Alignment.CenterHorizontally,
                        verticalArrangement =
                            Arrangement.spacedBy(10.dp),
                    ) {
                        AvatarImage(
                            imagePath = avatarPath,
                            size = 84.dp,
                        )

                        ReelyButton(
                            text = if (
                                avatarPath.isNullOrBlank()
                            ) {
                                "Dodaj zdjęcie"
                            } else {
                                "Zmień zdjęcie"
                            },
                            enabled = !isLoading,
                            overrideContainerColor =
                                MaterialTheme
                                    .colorScheme
                                    .surfaceVariant,
                            onClick = onAvatarClick,
                        )
                    }

                    Column(
                        verticalArrangement =
                            Arrangement.spacedBy(8.dp),
                    ) {
                        Text(
                            text = "Nazwa profilu",
                            style =
                                MaterialTheme.typography.titleSmall,
                            color =
                                MaterialTheme.colorScheme.onSurface,
                        )

                        ReelyTextField(
                            value = name,
                            onValueChange = onNameChange,
                            placeholder = "Np. Bartek",
                            enabled = !isLoading,
                            imeAction = ImeAction.Done,
                            onImeAction = {
                                if (canConfirm) {
                                    onConfirm()
                                }
                            },
                        )

                        if (!error.isNullOrBlank()) {
                            Text(
                                text = error,
                                style =
                                    MaterialTheme.typography.bodySmall,
                                color =
                                    MaterialTheme.colorScheme.error,
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement =
                            Arrangement.spacedBy(10.dp),
                        verticalAlignment =
                            Alignment.CenterVertically,
                    ) {
                        ReelyButton(
                            modifier = Modifier.weight(1f),
                            text = "Anuluj",
                            enabled = !isLoading,
                            overrideContainerColor =
                                MaterialTheme
                                    .colorScheme
                                    .surfaceVariant,
                            onClick = onDismiss,
                        )

                        ReelyButton(
                            modifier = Modifier.weight(1f),
                            text = if (isLoading) {
                                "Zapisywanie..."
                            } else {
                                "Zapisz"
                            },
                            enabled = canConfirm,
                            onClick = onConfirm,
                        )
                    }
                }
            }
        }
    }
}