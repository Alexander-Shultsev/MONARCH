package com.example.monarch.view.theme

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable

@Composable
fun MonarchTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colors = LightColorPalette,
        shapes = ShapesMain,
        content = content
    )
}