package com.devmello.gymlog.navigation

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.devmello.gymlog.core.navigation.NavMethod
import com.devmello.gymlog.core.navigation.NavigationManager
import com.devmello.gymlog.core.ui.ScaffoldManager
import com.devmello.gymlog.ui.auth.AuthenticationScreen
import com.devmello.gymlog.ui.auth.LoginScreen
import com.devmello.gymlog.ui.auth.RegisterScreen
import com.devmello.gymlog.ui.auth.viewmodel.AuthViewModel
import com.devmello.gymlog.ui.auth.viewmodel.AuthViewModelImpl
import com.devmello.gymlog.ui.bmi.BmiHistoricScreen
import com.devmello.gymlog.ui.dropdown_timer.DropdownTimerScreen
import com.devmello.gymlog.ui.form.TrainingFormScreen
import com.devmello.gymlog.ui.home.HomeScreen
import com.devmello.gymlog.ui.log.TrainingLogScreen
import com.devmello.gymlog.ui.stopwatch.StopwatchScreen
import com.devmello.gymlog.ui.user.UserProfileScreen
import com.devmello.gymlog.utils.BackPressHandler
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AppNavHost(
    navController: NavHostController,
    scaffoldManager: ScaffoldManager,
    navigationManager: NavigationManager,
    modifier: Modifier = Modifier
) {
    val authViewModel: AuthViewModel = koinViewModel<AuthViewModelImpl>()
    val navigationQueue by navigationManager.destinations.collectAsStateWithLifecycle()
    val currentUser by authViewModel.currentUser.collectAsStateWithLifecycle()
    val startDestination = if (currentUser != null) NavRoute.Home else NavRoute.Auth

    LaunchedEffect(navigationQueue) {

        if (navigationQueue.isNotEmpty()) {
            val destination = navigationQueue.first()
            when (destination.navMethod) {
                NavMethod.SINGLE_TOP -> navController.navigateSingleTopTo(destination.route)
                NavMethod.INCLUSIVE -> navController.navigateInclusive(destination.route)
                NavMethod.DEFAULT -> navController.navigate(
                    destination.route,
                    navOptions = destination.navOptions
                )
            }
            navigationManager.removeDestination(destination)
        }
    }

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable<NavRoute.Login> {
            LoginScreen(
                scaffoldManager = scaffoldManager,
                onGoogleSignInClick = {
                    authViewModel.signInWithGoogle(alreadyRegistered = false)
                },
                onClickRegister = { navigationManager.navigate(route = NavRoute.Register) },
                onConventionalSignInClick = { userCredentials ->
                    authViewModel.signInWithEmailAndPassword(userCredentials)
                },
                onSendResetPasswordEmailClick = {
                    authViewModel.sendPasswordResetEmail(it)
                }

            )
        }

        composable<NavRoute.Auth> {
            AuthenticationScreen(
                scaffoldManager = scaffoldManager,
                onClickLogin = { navigationManager.navigate(route = NavRoute.Login) },
                onClickRegister = { navigationManager.navigate(route = NavRoute.Register) }
            )
        }

        composable<NavRoute.Register> {
            RegisterScreen(
                scaffoldManager = scaffoldManager,
                onClickLogin = { navigationManager.navigate(route = NavRoute.Login) },
                onGoogleSignInClick = {
                    authViewModel.signInWithGoogle(alreadyRegistered = false)
                },
                onConventionalRegisterClick = { credentials ->
                    authViewModel.registerWithEmailAndPassword(credentials)
                }
            )
        }
        composable<NavRoute.Home> {
            HomeScreen(
                scaffoldManager = scaffoldManager,
                onButtonAddClick = { navigationManager.navigate(NavRoute.Form()) },
                onItemClickListener = { navigationManager.navigate(NavRoute.Log(it)) },
                onClickEdit = { navigationManager.navigate(NavRoute.Form(it)) }
            )
        }
        composable<NavRoute.Form> { navBackStackEntry ->
            val training: NavRoute.Form = navBackStackEntry.toRoute()
            TrainingFormScreen(
                trainingId = training.trainingId,
                onSaveTraining = { navController.popBackStack() },
                scaffoldManager = scaffoldManager,
                onCancel = { navController.popBackStack() }
            )
        }
        composable<NavRoute.Log> { navBackStackEntry ->
            val log: NavRoute.Log = navBackStackEntry.toRoute()
            log.trainingId?.let {
                TrainingLogScreen(
                    scaffoldManager = scaffoldManager,
                    onBackPressed = { navController.popBackStack() },
                    onError = { navController.popBackStack() },
                    trainingId = log.trainingId,
                    onClickDelete = { navController.popBackStack() },
                    onClickEdit = { trainingId ->
                        navigationManager.navigate(NavRoute.Form(trainingId))
                    }
                )
            }
        }
        composable<NavRoute.Bmi> {
            BackPressHandler {
                navigationManager.navigate(NavRoute.Home, NavMethod.INCLUSIVE)
            }
            BmiHistoricScreen(
                onError = { navController.popBackStack() },
                scaffoldManager = scaffoldManager
            )
        }
        composable<NavRoute.DropdownTimer>(
            deepLinks = DropdownTimerDestination.deepLinks
        ) {
            DropdownTimerScreen(scaffoldManager = scaffoldManager)
        }
        composable<NavRoute.Stopwatch>(
            deepLinks = StopwatchDestination.deepLinks
        ) {
            StopwatchScreen(scaffoldManager = scaffoldManager)
        }
        composable<NavRoute.UserProfile> {
            UserProfileScreen(
                scaffoldManager = scaffoldManager,
                onInvalidUser = {
                    navController.popBackStack()
                },
                onDeleteUser = {
                    authViewModel.resetState()
                    navigationManager.navigate(NavRoute.Auth, NavMethod.INCLUSIVE)
                })
        }
    }

}


fun NavHostController.navigateSingleTopTo(route: String) = this.navigate(route) {
    launchSingleTop = true
    restoreState = true
}

fun NavHostController.navigateSingleTopTo(route: NavRoute) = this.navigate(route) {
    launchSingleTop = true
    restoreState = true
}

internal fun NavHostController.navigateInclusive(route: String) = this.navigate(route) {
    popUpTo(0)
}

internal fun NavHostController.navigateInclusive(route: NavRoute) = this.navigate(route) {
    popUpTo(0)
}