package com.example.gymlog.navigation

import android.app.Activity
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.example.gymlog.R
import com.example.gymlog.ui.bmi.BmiHistoricScreen
import com.example.gymlog.ui.components.AppNavigationDrawer
import com.example.gymlog.ui.form.TrainingFormScreen
import com.example.gymlog.ui.home.HomeScreen
import com.example.gymlog.ui.log.TrainingLogScreen
import com.example.gymlog.utils.BackPressHandler
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import kotlinx.coroutines.launch

interface Destination {
    val route: String
    val title: Int
}

object Home : Destination {
    override val route: String = "home"
    override val title: Int = R.string.home_destination
}

object Form : Destination {
    override val route: String = "form"
    override val title: Int = R.string.form_destination
    const val trainingIdArg = "training_id"
    val routeWithArgs = "$route/{$trainingIdArg}"
    val arguments = listOf(navArgument(trainingIdArg) {
        type = NavType.StringType
    })
}

object Log : Destination {
    override val route: String = "training_log"
    override val title: Int = R.string.log_destination
    const val trainingIdArg = "training_id"
    val routeWithArgs = "$route/{$trainingIdArg}"
    val arguments = listOf(navArgument(trainingIdArg) {
        type = NavType.StringType
    })
}

object Bmi : Destination {
    override val route: String = "bmi_historic"
    override val title: Int = R.string.bmi_destination
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val currentActivity = LocalContext.current as Activity
    val scope = rememberCoroutineScope()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    DisposableEffect(currentRoute) {
        onDispose {
            scope.launch {
                drawerState.close()
            }
        }
    }
    if (currentRoute == Home.route) {
        BackPressHandler {
            currentActivity.finish()
        }
    }
    AppNavigationDrawer(
        gesturesEnabled = drawerState.isOpen,
        currentDestinationRoute = currentRoute ?: Home.route,
        onItemClick = {
            if (currentRoute != it.route) {
                scope.launch {
                    navController.navigateSingleTopTo(it.route)
                }
            }
        },
        drawerState = drawerState
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
            composable(
                route = Home.route,
                enterTransition = { fadeIn() },
                exitTransition = { fadeOut() }) {
                HomeScreen(
                    onButtonAddClick = { navController.navigateToTrainingForm(null) },
                    onItemClickListener = { navController.navigateToTrainingLog(it) },
                    onClickEdit = { navController.navigateToTrainingForm(it) },
                    onNavIconClick = {
                        scope.launch {
                            drawerState.open()
                        }
                    }
                )
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
            composable(
                route = Bmi.route,
                enterTransition = { fadeIn() },
                exitTransition = { fadeOut() }) {
                if (currentRoute == Bmi.route) {
                    BackPressHandler {
                        navController.navigateSingleTopTo(Home.route)
                    }
                }
                BmiHistoricScreen(
                    onNavIconClick = {
                        scope.launch {
                            drawerState.open()
                        }
                    },
                    onError = { navController.popBackStack() }
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