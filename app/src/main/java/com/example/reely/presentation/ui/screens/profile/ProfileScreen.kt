package com.example.reely.presentation.ui.screens.profile

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.reely.BuildConfig
import com.example.reely.presentation.ui.components.TopBar
import com.example.reely.presentation.ui.screens.profile.components.WatchProvidersSection
import com.example.reely.presentation.ui.screens.profile.components.AboutApplicationSection
import com.example.reely.presentation.ui.screens.profile.components.ProfileFormDialog
import com.example.reely.presentation.ui.screens.profile.components.ProfileSettingsSection
import com.example.reely.presentation.ui.screens.profile.components.ProfileUsersSection

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
                WatchProvidersSection(
                    providers = state.availableProviders,
                    selectedProviderIds = state.selectedProviderIds,
                    selectedTypes = state.selectedProviderTypes,
                    isLoading =
                        state.areProviderSettingsLoading,

                    isSaving =
                        state.isSavingProviderSettings,

                    isRefreshing =
                        state.isRefreshingProviders,

                    message =
                        state.providerSettingsMessage,

                    error =
                        state.providerSettingsError,

                    onProviderToggle =
                        viewModel::toggleWatchProvider,

                    onTypeToggle =
                        viewModel::toggleWatchProviderType,

                    onRefresh =
                        viewModel::refreshLibraryProviders,
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