package com.example.reely.presentation.ui.screens.home.components

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.reely.domain.model.User
import androidx.compose.material.icons.filled.Star
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import coil3.compose.AsyncImage
import com.example.reely.domain.model.UserId
import com.example.reely.presentation.ui.components.SmallSectionHeader
import java.io.File

@Composable
fun OurStatsSection(
    textOverride : String? = "Wasze statystyki"
){
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        if (!textOverride.isNullOrEmpty()) {
            SmallSectionHeader(textOverride)
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth()
        ){

            SmallStatsCard(
                user = User(
                    id = UserId(1),
                    nickname = "Antek",
                    avatarPath = null
                ),
                averageRating = 9.2f,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.weight(1f)
            )

            Spacer(
                modifier = Modifier.width(8.dp)
            )

            SmallStatsCard(
                user = User(
                    id = UserId(2),
                    nickname = "Daria",
                    avatarPath = null
                ),
                averageRating = 8.1f,
                tint = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.weight(1f)
            )

        }

    }

}

@Composable
fun SmallStatsCard(
    modifier: Modifier = Modifier,
    user: User,
    averageRating: Float,
    tint: Color
){
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.surfaceContainer,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(16.dp)
    ){
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(
                    color = tint
                )

        ) {
            if (user.avatarPath != null){

                AsyncImage(
                    model = File(user.avatarPath),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

            }else{
                Text(
                    text = user.nickname.first().toString(),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center
                )
            }

        }

        Spacer(modifier = Modifier.width(8.dp))

        Column(){

            Text(
                text = user.nickname,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onBackground
            )

            Text(
                text = "Średnia ocena",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
            )

            Row(
                verticalAlignment = Alignment.CenterVertically
            ){
                Icon(
                    imageVector = Icons.Filled.Star,
                    contentDescription = null,
                    tint = tint,
                    modifier = Modifier.size(14.dp)
                )

                Spacer(modifier = Modifier.width(4.dp))

                Text(
                    text = averageRating.toString(),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )

            }

        }
    }



}