package com.example.reely.presentation.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.example.reely.presentation.theme.InputBackground
import com.example.reely.presentation.theme.InputBorder
import com.example.reely.presentation.ui.screens.list.MovieListTab

interface ReelyTab {
    val tabTitle: String
    val tabIcon: ImageVector?
}
@Composable
fun ReelyListTabs(
    selectedTab: ReelyTab,
    onTabSelected: (ReelyTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(
                color = InputBackground,
                shape = RoundedCornerShape(16.dp)
            ),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        MovieListTab.entries.forEach { tab ->
            ReelyTabItem(
                title = tab.tabTitle,
                icon = tab.tabIcon,
                selected = selectedTab == tab,
                onClick = { onTabSelected(tab) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun ReelyTabItem(
    title: String,
    icon: ImageVector?,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val textColor by animateColorAsState(
        targetValue = if (selected) {
            MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.onBackground.copy(alpha = 0.55f)
        },
        label = "TabTextColor"
    )

    val shape = RoundedCornerShape(32.dp)
    val color = if (selected){
        InputBorder
    }else{
        Color.Transparent
    }

    Box(
        modifier = modifier
            .height(32.dp)
            .padding(vertical = 4.dp, horizontal = 4.dp)
            .shadow(
                elevation = 4.dp,
                shape = shape
            )
            .background(
                color = color,
                shape = shape
            )
            .clip(shape)
            .clickable(
                role = Role.Tab,
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(
                    bounded = true,
                ),
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {

            if (icon!= null){
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    modifier = Modifier
                        .padding(end = 2.dp)
                        .size(16.dp),
                    tint = textColor
                )
            }

            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                color = textColor
            )
        }
    }
}