package com.example.gymlog.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.gymlog.ui.form.TrainingFormScreen
import com.example.gymlog.ui.home.HomeScreen

interface Destination {
    val route: String
}

object Home : Destination {
    override val route: String = "home"
}

object Form : Destination {
    override val route: String = "form"
}

@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Home.route,
        modifier = modifier
    ) {
        composable(route = Home.route) {
            HomeScreen(onButtonAddClick = { navController.navigateSingleTopTo(Form.route) })
        }
        composable(route = Form.route) {
            TrainingFormScreen(
                onSaveTraining = { navController.popBackStack() },
                onDismissClick = { navController.popBackStack() })
        }
    }
}

fun NavHostController.navigateSingleTopTo(route: String) = this.navigate(route) {
    popUpTo(this@navigateSingleTopTo.graph.findStartDestination().id) {
        saveState = true
    }
    launchSingleTop = true
    restoreState = true
}