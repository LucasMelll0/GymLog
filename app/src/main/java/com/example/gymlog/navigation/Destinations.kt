package com.example.gymlog.navigation

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.gymlog.ui.form.TrainingFormScreen
import com.example.gymlog.ui.home.HomeScreen
import com.example.gymlog.ui.log.TrainingLogScreen
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable

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

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {

    AnimatedNavHost(
        navController = navController,
        startDestination = Home.route,
        enterTransition = {
            slideInHorizontally(
                initialOffsetX = { width -> width * 2 },
                animationSpec = tween(500)
            )
        },
        exitTransition = {
            slideOutHorizontally(
                targetOffsetX = { width -> -2 * width },
                animationSpec = tween(500)
            )
        },
        popEnterTransition = {
            slideInHorizontally(
                initialOffsetX = { width -> -2 * width },
                animationSpec = tween(700)
            )
        },
        popExitTransition = {
            slideOutHorizontally(
                targetOffsetX = { width -> 2 * width },
                animationSpec = tween(700)
            )
        },
        modifier = modifier
    ) {
        composable(route = Home.route) {
            HomeScreen(
                onButtonAddClick = { navController.navigateToTrainingForm(null) },
                onItemClickListener = { navController.navigateToTrainingLog(it) },
                onClickEdit = { navController.navigateToTrainingForm(it) })
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
                    onClickEdit = { trainingId ->
                        navController.navigateToTrainingForm(
                            trainingId
                        )
                    }
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