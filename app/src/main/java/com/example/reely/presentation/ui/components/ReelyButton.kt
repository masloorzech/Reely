package com.example.reely.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp

@Composable
fun ReelyButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    overrideContainerColor: Color = MaterialTheme.colorScheme.primary,
    leadingIcon: (@Composable () -> Unit)? = null,
) {
    val shape = RoundedCornerShape(8.dp)

    Box(
        modifier = modifier
            .defaultMinSize(minHeight = 32.dp)
            .clip(shape)
            .background(
                color = overrideContainerColor,
                shape = shape
            )
            .clickable(
                enabled = enabled,
                role = Role.Button,
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(
                    bounded = true,
                    color = Color.White.copy(alpha = 0.25f)
                ),
                onClick = onClick
            )
            .padding(horizontal = 16.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (leadingIcon != null) {
                leadingIcon()

                Spacer(
                    modifier = Modifier.width(8.dp)
                )
            }

            Text(
                text = text,
                color = if (enabled) {
                    Color.White
                } else {
                    MaterialTheme.colorScheme.onBackground.copy(alpha = 0.35f)
                },
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}