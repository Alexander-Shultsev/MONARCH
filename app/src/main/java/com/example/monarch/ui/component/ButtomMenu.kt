package com.example.monarch.ui.component

import android.content.ContentValues.TAG
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.monarch.ui.Title2
import com.example.monarch.ui.screen.ExperimentsScreen
import com.example.monarch.ui.screen.TimeUsedScreen
import com.example.monarch.ui.theme.onPrimaryLight
import com.google.android.material.internal.ViewUtils.RelativePadding

@Preview
@Composable
fun ButtonMenuP() {
    NavigationScreen(rememberNavController())
}

sealed class MenuItem(
    val route: String,
    val text: String,
) {
    object Time : MenuItem("time", "Время")
    object Experiments : MenuItem("experiments", "Эксперименты")
}


@Composable
fun NavigationScreen(
    mainNavController: NavHostController
) {
    val items = listOf(MenuItem.Time, MenuItem.Experiments)
    val activeMenuItem: MutableState<MenuItem> = remember { mutableStateOf(MenuItem.Time) }
    val navController = rememberNavController()

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
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.startDestinationId) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                                activeMenuItem.value = screen
                            },
                        color = if (activeMenuItem.value.route == screen.route) Color.White else onPrimaryLight,
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHostButtonNavigation(navController, MenuItem.Time.route, innerPadding)
    }
}


/* Контроллер навигации */
@Composable
fun NavHostButtonNavigation(
    navController: NavHostController,
    startDestination: String,
    innerPadding: PaddingValues
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = Modifier.padding(innerPadding)
    ) {
        composable(MenuItem.Time.route) {
            TimeUsedScreen(navController)
        }
        composable(MenuItem.Experiments.route) {
            ExperimentsScreen(navController)
        }
    }
}