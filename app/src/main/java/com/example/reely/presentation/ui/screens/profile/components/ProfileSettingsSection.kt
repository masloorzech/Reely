package com.example.reely.presentation.ui.screens.profile.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.composables.icons.lucide.Import
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.SquareArrowUpRight
import com.example.reely.presentation.ui.components.SmallSectionHeader

@Composable
fun ProfileSettingsSection(
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