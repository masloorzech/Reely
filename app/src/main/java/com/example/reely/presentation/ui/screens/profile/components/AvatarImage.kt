package com.example.reely.presentation.ui.screens.profile.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.User


@Composable
fun AvatarImage(
    imagePath: String?,
    modifier: Modifier = Modifier,
    size: Dp = 40.dp,
    contentDescription: String? = "User avatar"
) {
    var imageLoadFailed by remember(imagePath) {
        mutableStateOf(false)
    }

    LaunchedEffect(imagePath) {
        imageLoadFailed = false
    }

    val shouldShowImage = !imagePath.isNullOrBlank() && !imageLoadFailed

    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape),
        contentAlignment = Alignment.Center
    ) {
        if (shouldShowImage) {
            AsyncImage(
                model = imagePath,
                contentDescription = contentDescription,
                modifier = Modifier
                    .size(size)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop,
                onError = {
                    imageLoadFailed = true
                }
            )
        } else {
            AvatarFallback(
                size = size,
                contentDescription = contentDescription
            )
        }
    }
}

@Composable
private fun AvatarFallback(
    size: Dp,
    contentDescription: String?
) {
    Box(
        modifier = Modifier
            .size(size)
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Lucide.User,
            contentDescription = contentDescription,
            modifier = Modifier.size(size * 0.5f),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}