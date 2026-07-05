package com.example.reely.presentation.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.example.reely.presentation.ui.theme.ReelyTypography

val ReelyDarkColorScheme = darkColorScheme(
    primary = Primary,
    onPrimary = Color.White,

    primaryContainer = PrimaryDark,
    onPrimaryContainer = Color.White,

    secondary = AccentPink,
    onSecondary = Color.White,

    secondaryContainer = Color(0xFF3A2030),
    onSecondaryContainer = TextPrimary,

    tertiary = AccentOrange,
    onTertiary = Color.Black,

    background = Background,
    onBackground = TextPrimary,

    surface = Surface,
    onSurface = TextPrimary,

    surfaceVariant = Surface2,
    onSurfaceVariant = TextSecondary,

    outline = Border,
    outlineVariant = Divider,

    error = Error,
    onError = Color.White,

    inverseSurface = TextPrimary,
    inverseOnSurface = Background,

    scrim = Color.Black
)
@Composable
fun ReelyTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = ReelyDarkColorScheme,
        typography = ReelyTypography,
        content = content
    )
}