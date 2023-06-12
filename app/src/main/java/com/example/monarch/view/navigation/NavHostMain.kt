package com.example.monarch.view.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.monarch.view.screen.AddExperiment.AddExperimentScreen
import com.example.monarch.view.screen.NavigationScreen.NavigationScreen
import com.example.monarch.view.screen.TimeUsedScreen.ExperimentInfoScreen


sealed class NavMainItem(
    val route: String,
) {
    object NavigationScreen: NavMainItem("navigationScreen")
    object ExperimentInfoScreen: NavMainItem("experimentInfoScreen")
    object AddExperiment: NavMainItem("addExperiment")
}

/* Контроллер навигации */
@Composable
fun NavHostMain(
    startDestination: String
) {
    val navControllerMain = rememberNavController()

    NavHost(
        navController = navControllerMain,
        startDestination = startDestination
    ) {
        composable(NavMainItem.NavigationScreen.route) {
            NavigationScreen(navControllerMain)
        }
        composable(
            route = "${NavMainItem.ExperimentInfoScreen.route}/{idExperiment}/{nameExperiment}/{dateStart}/{dateEnd}/{timeLimit}",
            arguments = listOf(
                navArgument("idExperiment") { type = NavType.LongType },
                navArgument("nameExperiment") { type = NavType.StringType },
                navArgument("dateStart") { type = NavType.StringType },
                navArgument("dateEnd") { type = NavType.StringType },
                navArgument("timeLimit") { type = NavType.IntType },
            )
        ) {
            ExperimentInfoScreen(navControllerMain)
        }
        composable(NavMainItem.AddExperiment.route) {
            AddExperimentScreen(navControllerMain)
        }
    }
}

//data class Experiments(
//    val idExperiment: Long,
//    val name: String,
//    val dateStart: String,
//    val dateEnd: String,
//    val timeLimit: Int,
//)

fun navigateTo(
    navController: NavController,
    screen: String
) {
    navController.navigate(screen) {
        popUpTo(navController.graph.startDestinationId) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}