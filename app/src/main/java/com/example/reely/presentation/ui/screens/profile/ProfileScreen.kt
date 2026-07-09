package com.example.reely.presentation.ui.screens.profile

import androidx.compose.runtime.Composable

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.composables.icons.lucide.Import
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.SquareArrowUpRight
import com.example.reely.presentation.theme.ReelyTheme
import com.example.reely.presentation.ui.components.ReelyButton
import com.example.reely.presentation.ui.components.SmallSectionHeader
import com.example.reely.presentation.ui.components.TopBar
import com.example.reely.presentation.ui.screens.profile.components.AvatarImage
import com.example.reely.presentation.ui.screens.profile.components.UserFormDialog

@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    viewModel: ProfileScreenViewModel = viewModel()
) {

    val state = viewModel.state.collectAsState().value

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        )
        {

            TopBar(
                sectionName = "Profil"
            )

            Spacer(modifier = Modifier.height(16.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(
                    space = 12.dp,
                    alignment = Alignment.CenterHorizontally
                ),
                modifier = Modifier.fillMaxWidth()
            ){
                for (user in state.allUsers){
                    item{
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            if (user.id == state.mainUser.id) {
                                AvatarImage(
                                    imagePath = user.avatarPath,
                                    size = 96.dp,
                                    modifier = Modifier.border(
                                        width = 2.dp,
                                        color = MaterialTheme.colorScheme.primary,
                                        shape = CircleShape,
                                    ).clickable {
                                        viewModel.openEditUserDialog(user)
                                    }
                                )
                            }else{
                                AvatarImage(
                                    imagePath = user.avatarPath,
                                    size = 96.dp,
                                    modifier = Modifier.clickable {
                                        viewModel.openEditUserDialog(user)
                                    }
                                )
                            }
                            Text(
                                user.nickname,
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Aby edytować użytkownika klikni w avatar",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.height(16.dp))

            ReelyButton(
                text = "Dodaj użytkownika",
                onClick = {
                    viewModel.openCreateUserDialog()
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            HorizontalDivider(
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.08f)
            )

            Spacer(modifier = Modifier.height(16.dp))

            SmallSectionHeader(
                "Ustawienia"
            )

            Spacer(modifier = Modifier.height(16.dp))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)

            ) {

                ReelyButton(
                    text = "Importuj dane",
                    onClick = {},
                    leadingIcon = {
                        Icon(
                            imageVector = Lucide.Import,
                            contentDescription = "",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    },
                    overrideContainerColor = MaterialTheme.colorScheme.surface,
                    modifier = Modifier.width(256.dp)
                )
                ReelyButton(
                    text = "Eksportuj dane",
                    onClick = {},
                    leadingIcon = {
                        Icon(
                            imageVector = Lucide.SquareArrowUpRight,
                            contentDescription = "",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    },
                    overrideContainerColor = MaterialTheme.colorScheme.surface,
                    modifier = Modifier.width(256.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            SmallSectionHeader(
                "O aplikacji"
            )

            Text(
                text = "",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
            )

        }
        state.userDialog?.let { dialog ->
            UserFormDialog(
                mode = dialog.mode,
                nickname = dialog.nickname,
                avatarPath = dialog.avatarPath,
                error = dialog.error,
                isLoading = dialog.isSaving,
                onNicknameChange = viewModel::onDialogNicknameChange,
                onAvatarClick = {},
                onDismiss = viewModel::dismissUserDialog,
                onConfirm = viewModel::confirmUserDialog
            )
        }
    }
}