package com.example.monarch.ui.theme

import androidx.compose.material.lightColors
import androidx.compose.ui.graphics.Color

val Purple = Color(0xFF49485E)
val Orange = Color(0xFFF7D5B7)
val Green = Color(0xFF1FA946)
val Red = Color(0xFFCD4141)

val Success = Green
val Error = Red

val onPrimaryMedium = Color(0x80FFFFFF)

val LightColorPalette = lightColors(
    primary = Purple,
    onPrimary = Color.White,
    secondary = Orange,
    surface = Color.White, // date time picker background
    onSurface = Purple, // date time picker background
    error = Red
)