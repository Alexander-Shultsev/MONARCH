package com.example.monarch.view.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.monarch.view.screen.ExperimentsScreen.ExperimentsScreen
import com.example.monarch.view.screen.TimeUsedScreen.TimeUsedScreen

sealed class MenuItem(
    val route: String,
    val text: String,
) {
    object Time : MenuItem("time", "Время")
    object Experiments : MenuItem("experiments", "Эксперименты")
}


/* Контроллер навигации */
@Composable
fun NavHostButtonNavigation(
    navControllerMain: NavHostController,
    navControllerBottomMenu: NavHostController,
    startDestination: String,
    innerPadding: PaddingValues
) {
    NavHost(
        navController = navControllerBottomMenu,
        startDestination = startDestination,
        modifier = Modifier.padding(innerPadding)
    ) {
        composable(MenuItem.Time.route) {
            TimeUsedScreen(navControllerMain)
        }
        composable(MenuItem.Experiments.route) {
            ExperimentsScreen(navControllerMain)
        }
    }
}