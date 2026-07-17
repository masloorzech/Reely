package com.example.reely.presentation.ui.screens.profile.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.reely.presentation.ui.components.SmallSectionHeader

@Composable
fun AboutApplicationSection(
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