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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.composables.icons.lucide.Import
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.SquareArrowUpRight
import com.example.reely.BuildConfig
import com.example.reely.domain.model.StatsOverview
import com.example.reely.domain.model.User
import com.example.reely.presentation.ui.components.ReelyButton
import com.example.reely.presentation.ui.components.SmallSectionHeader
import com.example.reely.presentation.ui.components.TopBar
import com.example.reely.presentation.ui.screens.home.components.OurStatsSection
import com.example.reely.presentation.ui.screens.profile.components.AvatarImage
import com.example.reely.presentation.ui.screens.profile.components.UserFormDialog
import java.util.Locale

@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    onImportClick: () -> Unit = {},
    onExportClick: () -> Unit = {},
    viewModel: ProfileScreenViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val stats by viewModel.stats.collectAsStateWithLifecycle()

    val avatarPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            viewModel.onDialogAvatarPicked(uri?.toString())
        }
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = 16.dp,
                end = 16.dp,
                top = 16.dp,
                bottom = 40.dp
            ),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item {
                TopBar(
                    sectionName = "Profil"
                )
            }

            item {
                ProfileUsersSection(
                    users = state.allUsers,
                    mainUser = state.mainUser,
                    onUserClick = viewModel::openEditUserDialog,
                    onAddUserClick = viewModel::openCreateUserDialog
                )
            }

            item {
                ProfileStatsSection(
                    stats = stats
                )
            }

            item {
                ProfileSettingsSection(
                    onImportClick = onImportClick,
                    onExportClick = onExportClick
                )
            }

            item {
                AboutApplicationSection(
                    versionName = BuildConfig.VERSION_NAME
                )
            }
        }

        state.userDialog?.let { dialog ->
            UserFormDialog(
                mode = dialog.mode,
                nickname = dialog.nickname,
                avatarPath = dialog.avatarPath,
                error = dialog.error,
                isLoading = dialog.isSaving,
                onNicknameChange = viewModel::onDialogNicknameChange,
                onAvatarClick = {
                    avatarPickerLauncher.launch(
                        PickVisualMediaRequest(
                            ActivityResultContracts.PickVisualMedia.ImageOnly
                        )
                    )
                },
                onDismiss = viewModel::dismissUserDialog,
                onConfirm = viewModel::confirmUserDialog
            )
        }
    }
}

@Composable
private fun ProfileUsersSection(
    users: List<User>,
    mainUser: User,
    onUserClick: (User) -> Unit,
    onAddUserClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        SmallSectionHeader(
            sectionName = "Użytkownicy"
        )

        Spacer(Modifier.size(12.dp))

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(
                items = users,
                key = { user -> user.id.value }
            ) { user ->
                ProfileUserItem(
                    user = user,
                    isMainUser = user.id == mainUser.id,
                    onClick = {
                        onUserClick(user)
                    }
                )
            }
        }

        Spacer(Modifier.size(12.dp))

        Text(
            text = "Kliknij awatar, aby edytować użytkownika.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onBackground.copy(
                alpha = 0.6f
            )
        )

        Spacer(Modifier.size(12.dp))

        ReelyButton(
            text = "Dodaj użytkownika",
            onClick = onAddUserClick,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun ProfileUserItem(
    user: User,
    isMainUser: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp),
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        AvatarImage(
            imagePath = user.avatarPath,
            size = 88.dp,
            modifier = Modifier.then(
                if (isMainUser) {
                    Modifier.border(
                        width = 3.dp,
                        color = MaterialTheme.colorScheme.primary,
                        shape = CircleShape
                    )
                } else {
                    Modifier
                }
            )
        )

        Text(
            text = user.nickname,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onBackground
        )

        if (isMainUser) {
            Text(
                text = "Główny",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun ProfileStatsSection(
    stats: StatsOverview
) {

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        SmallSectionHeader(
            sectionName = "Twoje statystyki"
        )

        Spacer(Modifier.size(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ProfileStatCard(
                label = "Obejrzane filmy",
                value = stats.totalWatchedMovies.toString(),
                modifier = Modifier.weight(1f)
            )

            ProfileStatCard(
                label = "Czas oglądania",
                value = stats.totalWatchTimeMinutes.toWatchTimeText(),
                modifier = Modifier.weight(1f)
            )
        }
        if (stats.userStats.isNotEmpty()) {
            Spacer(Modifier.size(20.dp))

            OurStatsSection(
                textOverride = "Pozostali użytkownicy",
                userRatingItems = stats.userStats
            )
        }
    }
}

private fun Int.toWatchTimeText(): String {
    val totalMinutes = coerceAtLeast(0)
    val hours = totalMinutes / 60
    val minutes = totalMinutes % 60

    return when {
        hours > 0 && minutes > 0 -> "${hours}h ${minutes}min"
        hours > 0 -> "${hours}h"
        else -> "${minutes}min"
    }
}

@Composable
private fun ProfileStatCard(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1
            )

            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(
                    alpha = 0.6f
                )
            )
        }
    }
}

@Composable
private fun ProfileSettingsSection(
    onImportClick: () -> Unit,
    onExportClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        SmallSectionHeader(
            sectionName = "Ustawienia i dane"
        )

        Spacer(Modifier.size(12.dp))

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column {
                ProfileActionRow(
                    title = "Importuj dane",
                    description = "Wczytaj wcześniej zapisane dane aplikacji",
                    icon = Lucide.Import,
                    onClick = onImportClick
                )

                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    color = MaterialTheme.colorScheme.onSurface.copy(
                        alpha = 0.08f
                    )
                )

                ProfileActionRow(
                    title = "Eksportuj dane",
                    description = "Utwórz kopię zapasową swoich danych",
                    icon = Lucide.SquareArrowUpRight,
                    onClick = onExportClick
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
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            shape = RoundedCornerShape(10.dp),
            color = MaterialTheme.colorScheme.primary.copy(
                alpha = 0.12f
            )
        ) {
            Box(
                modifier = Modifier.size(40.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Spacer(Modifier.size(12.dp))

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(
                    alpha = 0.6f
                )
            )
        }
    }
}

@Composable
private fun AboutApplicationSection(
    versionName: String
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        SmallSectionHeader(
            sectionName = "O aplikacji"
        )

        Spacer(Modifier.size(12.dp))

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Reely",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = "Twoje wspólne miejsce do zapisywania filmów, ocen i historii oglądania.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(
                        alpha = 0.7f
                    )
                )

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 4.dp),
                    color = MaterialTheme.colorScheme.onSurface.copy(
                        alpha = 0.08f
                    )
                )

                Text(
                    text = "Wersja $versionName",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(
                        alpha = 0.5f
                    )
                )
            }
        }
    }
}