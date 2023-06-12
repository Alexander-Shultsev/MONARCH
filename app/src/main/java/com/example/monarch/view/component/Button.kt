package com.example.monarch.view.component

import android.graphics.drawable.Icon
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.monarch.view.H6
import com.example.monarch.view.Title1
import com.example.monarch.view.theme.Orange
import com.example.monarch.view.theme.Purple
import com.monarchcompany.monarchapp.R


@Composable
fun MonarchButtonIcon(
    text: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    enable: Boolean = true
) {
    Button(
        onClick = { onClick() },
        colors = ButtonDefaults.buttonColors(
            backgroundColor = Orange,
            contentColor = Purple
        ),
        shape = RoundedCornerShape(20.dp, 0.dp, 20.dp, 0.dp),
        modifier = modifier,
        enabled = enable
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Title1(text, color = Purple)
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Purple
            )
        }
    }
}

@Preview
@Composable
fun prevasdfasdfasdfasdfasdf() {
    MonarchButtonMain(
        text = "Создать",
        icon = Icons.Default.Add,
        onClick = {  }
    )
}

@Composable
fun MonarchButtonLittle(
    text: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    backgroundColor: Color = Color.White
) {
    Button(
        onClick = { onClick() },
        colors = ButtonDefaults.buttonColors(
            backgroundColor = backgroundColor,
        ),
        shape = RoundedCornerShape(50),
        modifier = modifier
    ) {
        H6(text, color = Purple)
    }
}

@Composable
fun MonarchButtonMain(
    text: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Button(
        onClick = { onClick() },
        colors = ButtonDefaults.buttonColors(
            backgroundColor = Color.White,
        ),
        shape = RoundedCornerShape(0.dp, 20.dp, 0.dp, 20.dp),
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp, horizontal = 6.dp)
        ) {
            Title1(text, color = Purple)
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Purple
            )
        }
    }
}

@Composable
fun ActionButton(
    icon: ImageVector,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.End
    ) {
        Box(
            modifier = Modifier
                .size(70.dp)
                .clickable { onClick() }
                .clip(shape = RoundedCornerShape(topStartPercent = 100))
                .background(MaterialTheme.colors.onPrimary)
                .padding(start = 12.dp, top = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(30.dp),
                tint = MaterialTheme.colors.primary
            )
        }
    }
}