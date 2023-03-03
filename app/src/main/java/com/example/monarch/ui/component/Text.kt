package com.example.monarch.ui

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.monarch.ui.theme.manrope

/* h1 h2 h3 h4 h5 h6 subtitle1 subtitle2 body1 body2 button caption */

@Composable
fun Subtitle1(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = Color.White
) {
    Text(
        text = text,
        style = TextStyle(
            fontFamily = manrope,
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp,
            color = color),
        modifier = modifier)
}

@Composable
fun ButtonText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = Color.White
) {
    Text(
        text = text,
        style = TextStyle(
            fontFamily = manrope,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            color = color),
        modifier = modifier)
}

@Composable
fun H2(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = Color.White
) {
    Text(
        text = text,
        style = TextStyle(
            fontFamily = manrope,
            fontWeight = FontWeight.Medium,
            fontSize = 24.sp,
            color = color,
        ),
        modifier = modifier)
}

@Composable
fun H4(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = Color.White
) {
    Text(
        text = text,
        style = TextStyle(
            fontFamily = manrope,
            fontWeight = FontWeight.Medium,
            fontSize = 16.sp,
            color = color),
        modifier = modifier)
}