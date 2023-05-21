package com.example.gymlog.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.gymlog.ui.form.TrainingFormScreen
import com.example.gymlog.ui.home.HomeScreen
import com.example.gymlog.ui.log.TrainingLogScreen

interface Destination {
    val route: String
}

object Home : Destination {
    override val route: String = "home"
}

object Form : Destination {
    override val route: String = "form"
    const val trainingIdArg = "training_id"
    val routeWithArgs = "$route/{$trainingIdArg}"
    val arguments = listOf(navArgument(trainingIdArg) {
        type = NavType.StringType
    })
}

object Log : Destination {
    override val route: String = "training_log"
    const val trainingIdArg = "training_id"
    val routeWithArgs = "$route/{$trainingIdArg}"
    val arguments = listOf(navArgument(trainingIdArg) {
        type = NavType.StringType
    })
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
            HomeScreen(
                onButtonAddClick = { navController.navigateToTrainingForm(null) },
                onItemClickListener = { navController.navigateToTrainingLog(it) })
        }
        composable(
            route = Form.routeWithArgs,
            arguments = Form.arguments
        ) { navBackStackEntry ->
            val trainingId = navBackStackEntry.arguments?.getString(Form.trainingIdArg)
            TrainingFormScreen(
                trainingId = trainingId,
                onSaveTraining = { navController.popBackStack() },
                onDismissClick = { navController.popBackStack() })
        }
        composable(
            route = Log.routeWithArgs,
            arguments = Log.arguments
        ) { navBackStackEntry ->
            val trainingId = navBackStackEntry.arguments?.getString(Log.trainingIdArg)
            trainingId?.let {
                TrainingLogScreen(
                    onBackPressed = { navController.popBackStack() },
                    onNavIconClick = { navController.popBackStack() },
                    onError = { navController.popBackStack() },
                    trainingId = trainingId,
                    onClickDelete = { navController.popBackStack() },
                    onClickEdit = { trainingId -> navController.navigateToTrainingForm(trainingId) }
                )
            }
        }
    }
}

fun NavHostController.navigateSingleTopTo(route: String) = this.navigate(route) {
    launchSingleTop = true
    restoreState = true
}

fun NavHostController.navigateToTrainingForm(trainingId: String?) =
    this.navigateSingleTopTo("${Form.route}/$trainingId")

private fun NavHostController.navigateToTrainingLog(trainingId: String) =
    this.navigateSingleTopTo("${Log.route}/$trainingId")