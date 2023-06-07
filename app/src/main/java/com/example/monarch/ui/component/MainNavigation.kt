package com.example.monarch.ui.component

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.monarch.ui.Title2
import com.example.monarch.ui.screen.ExperimentsScreen
import com.example.monarch.ui.screen.TimeUsedScreen
import com.example.monarch.ui.theme.onPrimaryLight


sealed class NavMainItem(
    val route: String,
) {
    object NavigationScreen: NavMainItem("navigationScreen")
    object ShowExperimentScreen: NavMainItem("showExperimentScreen")
    object AddExperiment: NavMainItem("addExperiment")
}

/* Контроллер навигации */
@Composable
fun NavHostMain(
    startDestination: String
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(NavMainItem.NavigationScreen.route) {
            NavigationScreen(navController)
        }
        composable(NavMainItem.ShowExperimentScreen.route) {

        }
        composable(NavMainItem.AddExperiment.route) {

        }
    }
}