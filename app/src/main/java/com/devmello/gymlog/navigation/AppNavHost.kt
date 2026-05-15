package com.devmello.gymlog.navigation

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.devmello.gymlog.core.ui.MessageManager
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
import org.koin.androidx.compose.get
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AppNavHost(
    navController: NavHostController,
    scaffoldManager: ScaffoldManager,
    modifier: Modifier = Modifier
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val authViewModel: AuthViewModel = koinViewModel<AuthViewModelImpl>()

    val startDestination = authViewModel.currentUser?.let { Home.route } ?: Auth.route
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(Login.route) {
            LoginScreen(
                scaffoldManager = scaffoldManager,
                onGoogleSignInClick = {
                    authViewModel.signInWithGoogle(alreadyRegistered = true)
                },
                onClickRegister = { navController.navigateInclusive(Register.route) },
                onConventionalSignInClick = { userCredentials ->
                    authViewModel.signInWithEmailAndPassword(userCredentials)
                },
                onSendResetPasswordEmailClick = {
                    authViewModel.sendPasswordResetEmail(it)
                }

            )
        }

        composable(Auth.route) {
            AuthenticationScreen(
                scaffoldManager = scaffoldManager,
                onClickLogin = { navController.navigateSingleTopTo(Login.route) },
                onClickRegister = { navController.navigateSingleTopTo(Register.route) }
            )
        }

        composable(Register.route) {
            RegisterScreen(
                scaffoldManager = scaffoldManager,
                onClickLogin = { navController.navigateSingleTopTo(Login.route) },
                onGoogleSignInClick = {
                    authViewModel.signInWithGoogle(alreadyRegistered = false)
                },
                onConventionalRegisterClick = { credentials ->
                    authViewModel.registerWithEmailAndPassword(credentials)
                }
            )
        }
        composable(route = Home.route) {
            HomeScreen(
                scaffoldManager = scaffoldManager,
                onButtonAddClick = { navController.navigateToTrainingForm(null) },
                onItemClickListener = { navController.navigateToTrainingLog(it) },
                onClickEdit = { navController.navigateToTrainingForm(it) }
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
                    onNavIconClick = { navController.popBackStack() }, // TODO Scaffold
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
        composable(route = Bmi.route) {
            if (currentRoute == Bmi.route) {
                BackPressHandler {
                    navController.navigateSingleTopTo(Home.route)
                }
            }
            BmiHistoricScreen(
                onNavIconClick = {
                    // drawerState.open() // TODO scaffold

                },
                onError = { navController.popBackStack() }
            )
        }
        composable(
            DropdownTimer.route,
            deepLinks = DropdownTimer.deepLinks
        ) {
            DropdownTimerScreen(onNavIconClick = {
                // drawerState.open() // TODO scaffold

            })
        }
        composable(route = Stopwatch.route) {
            StopwatchScreen(onNavIconClick = {
                // drawerState.open() // TODO scaffold

            })
        }
        composable(route = UserProfile.route) {
            UserProfileScreen(
                onNavIconClick = {
                    //  drawerState.open() // TODO scaffold

                }, onInvalidUser = {
                    navController.popBackStack()
                },
                onDeleteUser = {
                    authViewModel.resetState()
                    navController.navigateInclusive(Auth.route)
                })
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

internal fun NavHostController.navigateInclusive(route: String) = this.navigate(route) {
    popUpTo(0)
}