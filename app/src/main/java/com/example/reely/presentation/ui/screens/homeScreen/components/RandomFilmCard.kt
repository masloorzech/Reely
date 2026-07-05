package com.example.reely.presentation.ui.screens.homeScreen.components
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.composables.icons.lucide.Dices
import com.composables.icons.lucide.Lucide
import com.example.reely.R
import com.example.reely.presentation.ui.components.ReelyButton

@Composable
fun RandomFilmCard(
    onClick: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(156.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF1B1230),
                        Color(0xFF120D22),
                        Color(0xFF0B1020)
                    )
                )
            )
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .offset(x = 28.dp)
                .size(156.dp)
                .blur(28.dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFF7C3AED).copy(alpha = 0.45f),
                            Color(0xFF7C3AED).copy(alpha = 0f)
                        )
                    ),
                    shape = CircleShape
                )
        )

        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(
                    start = 12.dp,
                    top = 8.dp,
                    bottom = 8.dp
                )
                .width(210.dp)
        ) {
            Text(
                text = "Co obejrzymy dziś?",
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Wylosuj film z Waszej listy do obejrzenia",
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.62f),
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.weight(1f))

            ReelyButton(
                text = "Losuj film",
                onClick = onClick,
                leadingIcon = {
                    Icon(
                        imageVector = Lucide.Dices,
                        tint = Color.White,
                        contentDescription = null
                    )
                }
            )
        }

        Image(
            painter = painterResource(id = R.drawable.popcorn),
            contentDescription = "Popcorn",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .offset(x = 8.dp, y = 4.dp)
                .size(156.dp)
        )
    }
}