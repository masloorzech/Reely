package com.example.reely.presentation.ui.screens.profile

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.composables.icons.lucide.Import
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.SquareArrowUpRight
import com.example.reely.BuildConfig
import com.example.reely.domain.model.Profile
import com.example.reely.domain.model.ProfilePosition
import com.example.reely.presentation.ui.components.TopBar
import com.example.reely.presentation.ui.components.SmallSectionHeader
import com.example.reely.presentation.ui.screens.profile.components.AvatarImage
import com.example.reely.presentation.ui.screens.profile.components.ProfileFormDialog

@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    onImportClick: () -> Unit = {},
    onExportClick: () -> Unit = {},
    viewModel: ProfileScreenViewModel = hiltViewModel(),
) {
    val state by
    viewModel.state.collectAsStateWithLifecycle()

    val avatarPickerLauncher =
        rememberLauncherForActivityResult(
            contract =
                ActivityResultContracts.PickVisualMedia(),
            onResult = { uri ->
                viewModel.onDialogAvatarPicked(
                    uri?.toString(),
                )
            },
        )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                MaterialTheme.colorScheme.background,
            ),
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = 16.dp,
                end = 16.dp,
                top = 16.dp,
                bottom = 40.dp,
            ),
            verticalArrangement =
                Arrangement.spacedBy(24.dp),
        ) {
            item {
                TopBar(
                    sectionName = "Profile",
                )
            }

            item {
                ProfileUsersSection(
                    profiles = state.profiles,
                    isLoading = state.isLoading,
                    error = state.error,
                    onProfileClick =
                        viewModel::openEditProfileDialog,
                )
            }

            item {
                ProfileSettingsSection(
                    onImportClick = onImportClick,
                    onExportClick = onExportClick,
                )
            }

            item {
                AboutApplicationSection(
                    versionName =
                        BuildConfig.VERSION_NAME,
                )
            }
        }

        state.profileDialog?.let { dialog ->
            ProfileFormDialog(
                name = dialog.name,
                avatarPath = dialog.avatarPath,
                error = dialog.error,
                isLoading = dialog.isSaving,
                onNameChange =
                    viewModel::onDialogNameChange,
                onAvatarClick = {
                    avatarPickerLauncher.launch(
                        PickVisualMediaRequest(
                            ActivityResultContracts
                                .PickVisualMedia
                                .ImageOnly,
                        )
                    )
                },
                onDismiss =
                    viewModel::dismissProfileDialog,
                onConfirm =
                    viewModel::confirmProfileDialog,
            )
        }
    }
}

@Composable
private fun ProfileUsersSection(
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
                color = MaterialTheme.colorScheme
                    .primary
                    .copy(alpha = 0.5f),
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

@Composable
private fun ProfileSettingsSection(
    onImportClick: () -> Unit,
    onExportClick: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
    ) {
        SmallSectionHeader(
            sectionName = "Ustawienia i dane",
        )

        Spacer(Modifier.size(12.dp))

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
        ) {
            Column {
                ProfileActionRow(
                    title = "Importuj dane",
                    description =
                        "Wczytaj wcześniej zapisaną kopię danych aplikacji",
                    icon = Lucide.Import,
                    onClick = onImportClick,
                )

                HorizontalDivider(
                    modifier = Modifier.padding(
                        horizontal = 16.dp,
                    ),
                    color = MaterialTheme.colorScheme
                        .onSurface
                        .copy(alpha = 0.08f),
                )

                ProfileActionRow(
                    title = "Eksportuj dane",
                    description =
                        "Utwórz kopię zapasową danych aplikacji",
                    icon = Lucide.SquareArrowUpRight,
                    onClick = onExportClick,
                )
            }
        }
    }
}

@Composable
private fun ProfileActionRow(
    title: String,
    description: String,
    icon: ImageVector,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Surface(
            shape = RoundedCornerShape(10.dp),
            color = MaterialTheme.colorScheme
                .primary
                .copy(alpha = 0.12f),
        ) {
            Box(
                modifier = Modifier.size(40.dp),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp),
                )
            }
        }

        Spacer(Modifier.size(12.dp))

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement =
                Arrangement.spacedBy(2.dp),
        ) {
            Text(
                text = title,
                style =
                    MaterialTheme.typography.labelLarge,
                color =
                    MaterialTheme.colorScheme.onSurface,
            )

            Text(
                text = description,
                style =
                    MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme
                    .onSurface
                    .copy(alpha = 0.6f),
            )
        }
    }
}

@Composable
private fun AboutApplicationSection(
    versionName: String,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
    ) {
        SmallSectionHeader(
            sectionName = "O aplikacji",
        )

        Spacer(Modifier.size(12.dp))

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement =
                    Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = "Reely",
                    style =
                        MaterialTheme.typography.titleMedium,
                    color =
                        MaterialTheme.colorScheme.onSurface,
                )

                Text(
                    text = "Wspólne miejsce do zapisywania filmów, ocen i historii oglądania.",
                    style =
                        MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme
                        .onSurface
                        .copy(alpha = 0.7f),
                )

                HorizontalDivider(
                    modifier = Modifier.padding(
                        vertical = 4.dp,
                    ),
                    color = MaterialTheme.colorScheme
                        .onSurface
                        .copy(alpha = 0.08f),
                )

                Text(
                    text = "Wersja $versionName",
                    style =
                        MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme
                        .onSurface
                        .copy(alpha = 0.5f),
                )
            }
        }
    }
}