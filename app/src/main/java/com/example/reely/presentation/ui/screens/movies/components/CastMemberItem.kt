package com.example.reely.presentation.ui.screens.movies.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.example.reely.domain.model.CastMember

@Composable
fun CastMemberItem(
    castMember: CastMember,
    modifier: Modifier = Modifier
) {
    val profilePath = castMember.profilePaths
        ?.localProfilePath
        ?.takeIf { path -> path.isNotBlank() }
        ?: castMember.profilePaths
            ?.remoteProfilePath
            ?.takeIf { path -> path.isNotBlank() }

    Column(
        modifier = modifier.width(104.dp)
    ) {
        Box(
            modifier = Modifier
                .width(104.dp)
                .height(140.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surface),
            contentAlignment = Alignment.Center
        ) {
            if (profilePath != null) {
                AsyncImage(
                    model = profilePath,
                    contentDescription = castMember.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Text(
                    text = castMember.name
                        .firstOrNull()
                        ?.uppercase()
                        .orEmpty(),
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(
                        alpha = 0.5f
                    )
                )
            }
        }

        Spacer(Modifier.height(6.dp))

        Text(
            text = castMember.name,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onBackground,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )

        castMember.character
            ?.takeIf { character -> character.isNotBlank() }
            ?.let { character ->
                Spacer(Modifier.height(2.dp))

                Text(
                    text = character,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onBackground.copy(
                        alpha = 0.55f
                    ),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
    }
}