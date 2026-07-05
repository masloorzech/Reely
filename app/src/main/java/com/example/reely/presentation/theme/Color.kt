package com.example.reely.presentation.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

val Background = Color(0xFF090B10)
val Surface = Color(0xFF12151D)
val Surface2 = Color(0xFF1A1F2B)
val Surface3 = Color(0xFF222838)

val CardMovie = Color(0xFF171B25)
val CardStats = Color(0xFF1C2130)
val CardElevated = Color(0xFF242B3D)

val Primary = Color(0xFF7C4DFF)
val PrimaryLight = Color(0xFF9575FF)
val PrimaryDark = Color(0xFF6338E0)

val AccentPink = Color(0xFFFF5C9F)
val AccentOrange = Color(0xFFFFB454)
val AccentBlue = Color(0xFF63B3FF)
val AccentGreen = Color(0xFF4ADE80)
val AccentRed = Color(0xFFFF6B6B)

val TextPrimary = Color(0xFFF7F8FA)
val TextSecondary = Color(0xFFA9AFBD)
val TextMuted = Color(0xFF71798C)
val TextDisabled = Color(0xFF525866)

val Border = Color(0xFF2A3142)
val BorderStrong = Color(0xFF2E3548)
val Divider = Color(0xFF202636)

val InputBackground = Color(0xFF161A24)
val InputBorder = Color(0xFF2A3142)
val InputFocused = Primary

val BottomNavBackground = Color(0xFF0F1219)
val BottomNavSelected = Primary
val BottomNavUnselected = Color(0xFF72798D)

val ButtonPrimary = Primary
val ButtonPrimaryPressed = Color(0xFF6C3EF2)
val ButtonDisabled = Color(0xFF3A4050)

val ButtonSecondary = Surface2
val ButtonSecondaryBorder = BorderStrong

val StarFilled = Color(0xFF8B5CF6)
val StarEmpty = Color(0xFF3A4050)

val RatingYou = Color(0xFF8B5CF6)
val RatingHer = AccentPink
val RatingAverage = TextPrimary

val Favorite = AccentPink
val Success = AccentGreen
val Warning = AccentOrange
val Error = AccentRed

val PrimaryGradient = Brush.verticalGradient(
    colors = listOf(
        Color(0xFF6D3DF7),
        Color(0xFF8F5BFF)
    )
)

val PrimaryGradientSoft = Brush.linearGradient(
    colors = listOf(
        Color(0xFF8B5CF6).copy(alpha = 0.16f),
        Color(0xFFFF5CA8).copy(alpha = 0.10f)
    )
)

val ButtonGradient = Brush.horizontalGradient(
    colors = listOf(
        Color(0xFF7C4DFF),
        Color(0xFF6D28D9)
    )
)

val MovieCardGradient = Brush.verticalGradient(
    colors = listOf(
        Color(0xFF1A1F2B),
        Color(0xFF10131A)
    )
)

val PurplePinkGradient = Brush.horizontalGradient(
    colors = listOf(
        RatingYou,
        RatingHer
    )
)