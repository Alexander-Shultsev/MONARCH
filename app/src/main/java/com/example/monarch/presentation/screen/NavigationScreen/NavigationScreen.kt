package com.example.monarch.presentation.screen.NavigationScreen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.monarch.presentation.Title2
import com.example.monarch.presentation.navigation.MenuItem
import com.example.monarch.presentation.navigation.NavHostButtonNavigation
import com.example.monarch.presentation.navigation.navigateTo
import com.example.monarch.presentation.theme.onPrimaryLight

@Composable
fun NavigationScreen(
    navControllerMain: NavHostController
) {
    val items = listOf(MenuItem.Time, MenuItem.Experiments)
    val activeMenuItem: MutableState<MenuItem> = remember { mutableStateOf(MenuItem.Time) }
    val navControllerBottomMenu = rememberNavController()

    Scaffold(
        backgroundColor = MaterialTheme.colors.primary,
        bottomBar = {
            Row(
                modifier = Modifier
                    .padding(vertical = 18.dp)
                    .padding(start = 19.dp)
            ) {
                items.forEach { screen ->
                    Title2(
                        text = screen.text,
                        modifier = Modifier
                            .padding(end = 17.dp)
                            .clickable {
                                navigateTo(navControllerBottomMenu, screen.route)
                                activeMenuItem.value = screen
                            },
                        color = if (activeMenuItem.value.route == screen.route) Color.White else onPrimaryLight,
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHostButtonNavigation(
            navControllerMain,
            navControllerBottomMenu,
            MenuItem.Time.route,
            innerPadding
        )
    }
}