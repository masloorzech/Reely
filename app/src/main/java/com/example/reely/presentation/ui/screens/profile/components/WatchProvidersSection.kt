package com.example.reely.presentation.ui.screens.profile.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.reely.domain.model.WatchMonetizationType
import com.example.reely.domain.model.WatchProvider
import com.example.reely.presentation.ui.components.ReelyButton
import com.example.reely.presentation.ui.components.ReelyChip
import com.example.reely.presentation.ui.components.SmallSectionHeader

@Composable
fun WatchProvidersSection(
    providers: List<WatchProvider>,
    selectedProviderIds: Set<Int>,
    selectedTypes: Set<WatchMonetizationType>,
    isLoading: Boolean,
    isSaving: Boolean,
    isRefreshing: Boolean,
    message: String?,
    error: String?,
    onProviderToggle: (Int) -> Unit,
    onTypeToggle: (WatchMonetizationType) -> Unit,
    onRefresh: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
    ) {
        SmallSectionHeader(
            sectionName = "Wasze serwisy",
        )

        Spacer(Modifier.size(8.dp))

        Text(
            text = "Wybierz platformy, z których korzystacie. " +
                    "Będą używane w filtrach i losowaniu filmu.",
            style =
                MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme
                .onBackground
                .copy(alpha = 0.6f),
        )

        Spacer(Modifier.size(12.dp))

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
            ) {
                when {
                    isLoading -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 24.dp),
                            contentAlignment =
                                Alignment.Center,
                        ) {
                            CircularProgressIndicator()
                        }
                    }

                    providers.isEmpty() -> {
                        Text(
                            text = "Nie udało się jeszcze pobrać katalogu serwisów.",
                            style =
                                MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme
                                .onSurface
                                .copy(alpha = 0.7f),
                        )
                    }

                    else -> {
                        LazyRow(
                            modifier =
                                Modifier.fillMaxWidth(),
                            horizontalArrangement =
                                Arrangement.spacedBy(10.dp),
                        ) {
                            items(
                                items = providers,
                                key = { provider ->
                                    provider.providerId
                                },
                            ) { provider ->
                                WatchProviderItem(
                                    provider = provider,
                                    selected =
                                        provider.providerId in
                                                selectedProviderIds,
                                    onClick = {
                                        onProviderToggle(
                                            provider.providerId
                                        )
                                    },
                                )
                            }
                        }
                    }
                }

                Spacer(Modifier.height(20.dp))

                Text(
                    text = "Domyślny rodzaj dostępu",
                    style =
                        MaterialTheme.typography.labelLarge,
                    color =
                        MaterialTheme.colorScheme.onSurface,
                )

                Spacer(Modifier.height(8.dp))

                LazyRow(
                    horizontalArrangement =
                        Arrangement.spacedBy(8.dp),
                ) {
                    items(
                        items =
                            WatchMonetizationType.entries,
                        key = { type ->
                            type.name
                        },
                    ) { type ->
                        ReelyChip(
                            text = type.displayName,
                            selected =
                                type in selectedTypes,
                            onClick = {
                                onTypeToggle(type)
                            },
                        )
                    }
                }

                Spacer(Modifier.height(20.dp))

                HorizontalDivider(
                    color = MaterialTheme.colorScheme
                        .onSurface
                        .copy(alpha = 0.08f),
                )

                Spacer(Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment =
                        Alignment.CenterVertically,
                ) {
                    ReelyButton(
                        text = if (isRefreshing) {
                            "Odświeżanie..."
                        } else {
                            "Odśwież dostępność filmów"
                        },
                        overrideContainerColor =
                            MaterialTheme.colorScheme
                                .background
                                .copy(alpha = 0.6f),
                        onClick = {
                            if (!isRefreshing) {
                                onRefresh()
                            }
                        },
                    )

                    if (isRefreshing || isSaving) {
                        Spacer(Modifier.size(12.dp))

                        CircularProgressIndicator(
                            modifier =
                                Modifier.size(20.dp),
                            strokeWidth = 2.dp,
                        )
                    }
                }

                message?.let {
                    Spacer(Modifier.height(10.dp))

                    Text(
                        text = it,
                        style =
                            MaterialTheme.typography.bodySmall,
                        color =
                            MaterialTheme.colorScheme.primary,
                    )
                }

                error?.let {
                    Spacer(Modifier.height(10.dp))

                    Text(
                        text = it,
                        style =
                            MaterialTheme.typography.bodySmall,
                        color =
                            MaterialTheme.colorScheme.error,
                    )
                }
            }
        }
    }
}

@Composable
private fun WatchProviderItem(
    provider: WatchProvider,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val borderColor = if (selected) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme
            .onSurface
            .copy(alpha = 0f)
    }

    Surface(
        modifier = Modifier
            .width(92.dp)
            .clickable(onClick = onClick)
            .border(
                width = if (selected) {
                    2.dp
                }else{
                    0.dp
                },
                color = borderColor,
                shape = RoundedCornerShape(14.dp),
            ),
        shape = RoundedCornerShape(14.dp),
        color = if (selected) {
            MaterialTheme.colorScheme
                .primary
                .copy(alpha = 0.1f)
        } else {
            MaterialTheme.colorScheme.surface
        },
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment =
                Alignment.CenterHorizontally,
            verticalArrangement =
                Arrangement.spacedBy(8.dp),
        ) {
            Surface(
                modifier = Modifier.size(52.dp),
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surface,
            ) {
                if (provider.logoPath != null) {
                    AsyncImage(
                        model = provider.logoPath,
                        contentDescription =
                            provider.providerName,
                        contentScale =
                            ContentScale.Crop,
                        modifier =
                            Modifier.fillMaxSize(),
                    )
                }
            }

            Text(
                text = provider.providerName,
                style =
                    MaterialTheme.typography.labelSmall,
                color =
                    MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.height(32.dp),
            )
        }
    }
}