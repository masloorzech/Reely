package com.example.reely.presentation.ui.screens.homeScreen.components
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import java.io.File

@Composable
fun HelloSection(
    firstAvatarPath: String?,
    secondAvatarPath: String?,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Column {
            Text(
                text = "Cześć! 👋",
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Gotowi na dobry film?",
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                style = MaterialTheme.typography.bodyMedium
            )
        }

        CoupleAvatars(
            firstAvatarPath = firstAvatarPath,
            secondAvatarPath = secondAvatarPath
        )
    }
}

@Composable
private fun CoupleAvatars(
    firstAvatarPath: String?,
    secondAvatarPath: String?,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .width(80.dp)
            .height(46.dp)
    ) {
        AvatarImage(
            imagePath = firstAvatarPath,
            modifier = Modifier.align(Alignment.CenterStart)
        )

        AvatarImage(
            imagePath = secondAvatarPath,
            modifier = Modifier.align(Alignment.CenterEnd)
        )
    }
}

@Composable
private fun AvatarImage(
    imagePath: String?,
    modifier: Modifier = Modifier,
    borderColor: Color = MaterialTheme.colorScheme.background,
    fallbackColor: Color = Color(0xFF1A1F2E),
) {
    Box(
        modifier = modifier
            .size(46.dp)
            .clip(CircleShape)
            .background(borderColor)
            .padding(2.dp)
            .clip(CircleShape)
            .background(fallbackColor),
        contentAlignment = Alignment.Center
    ) {
        if (!imagePath.isNullOrBlank()) {
            AsyncImage(
                model = File(imagePath),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}