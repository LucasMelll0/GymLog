import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.gymlog.R
import com.example.gymlog.navigation.Auth
import com.example.gymlog.navigation.Bmi
import com.example.gymlog.navigation.DropdownTimer
import com.example.gymlog.navigation.Form
import com.example.gymlog.navigation.Home
import com.example.gymlog.navigation.Log
import com.example.gymlog.navigation.Login
import com.example.gymlog.navigation.Register
import com.example.gymlog.navigation.Stopwatch
import com.example.gymlog.navigation.UserProfile
import com.example.gymlog.navigation.viewmodel.MainViewModel
import com.example.gymlog.navigation.viewmodel.MainViewModelImpl
import com.example.gymlog.ui.auth.AuthenticationScreen
import com.example.gymlog.ui.auth.LoginScreen
import com.example.gymlog.ui.auth.RegisterScreen
import com.example.gymlog.ui.auth.viewmodel.AuthViewModel
import com.example.gymlog.ui.bmi.BmiHistoricScreen
import com.example.gymlog.ui.components.AppNavigationDrawer
import com.example.gymlog.ui.components.DefaultAlertDialog
import com.example.gymlog.ui.components.LoadingDialog
import com.example.gymlog.ui.dropdown_timer.DropdownTimerScreen
import com.example.gymlog.ui.form.TrainingFormScreen
import com.example.gymlog.ui.home.HomeScreen
import com.example.gymlog.ui.log.TrainingLogScreen
import com.example.gymlog.ui.stopwatch.StopwatchScreen
import com.example.gymlog.ui.user.UserProfileScreen
import com.example.gymlog.utils.BackPressHandler
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    val viewModel: MainViewModel = koinViewModel<MainViewModelImpl>()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val showExitConfirmationDialog by viewModel.showExitConfirmationDialog.collectAsStateWithLifecycle()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val currentActivity = LocalContext.current as Activity
    val authViewModel: AuthViewModel = koinViewModel()
    val signInState by authViewModel.state.collectAsStateWithLifecycle()
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult(),
        onResult = { result ->
            viewModel.signInWithIntent(
                result = result,
                authViewModel = authViewModel,
            )
        }
    )
    var currentUserdata by remember { mutableStateOf(viewModel.authClient.getSignedInUser()) }

    LaunchedEffect(key1 = signInState.signInError) {
        signInState.signInError?.let { error ->
            Toast.makeText(
                currentActivity,
                error,
                Toast.LENGTH_LONG
            ).show()
        }
    }
    LaunchedEffect(key1 = signInState.isSignInSuccessful) {
        if (signInState.isSignInSuccessful) {
            currentUserdata = viewModel.authClient.getSignedInUser()
            navController.navigateSingleTopTo(Home.route)
        }
    }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    DisposableEffect(currentRoute) {
        onDispose {
            scope.launch {
                drawerState.close()
            }
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
        drawerState = drawerState,
        onClickExit = {
            scope.launch {
                drawerState.close()
                viewModel.setExitConfirmationDialogVisibility(true)
            }
        },
        user = currentUserdata
    ) {
        Box(
            Modifier
                .fillMaxSize()
                .zIndex(1f)
        ) {
            if (isLoading) LoadingDialog()
            if (showExitConfirmationDialog) ExitConfirmationDialog(
                viewModel = viewModel,
                authViewModel = authViewModel,
                navController = navController
            )
        }
        val startDestination = authViewModel.currentUser?.let { Home.route } ?: Auth.route
        AnimatedNavHost(
            navController = navController,
            startDestination = startDestination,
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
            composable(Login.route) {
                LoginScreen(
                    onGoogleSignInClick = { viewModel.signInWithGoogle(launcher) },
                    onClickRegister = { navController.navigateInclusive(Register.route) },
                    onConventionalSignInClick = { userCredentials ->
                        viewModel.signInWithEmailAndPassword(
                            userCredentials = userCredentials,
                            authViewModel = authViewModel
                        )
                    },
                    onSendResetPasswordEmailClick = {
                        scope.launch {
                            viewModel.setIsLoadingTo(true)
                            val response = viewModel.authClient.sendPasswordResetEmail(it)
                            if (response.isSuccess) {
                                Toast.makeText(
                                    currentActivity,
                                    currentActivity.getString(R.string.auth_send_password_reset_email_success_message),
                                    Toast.LENGTH_LONG
                                ).show()
                            } else {
                                response.errorMessage?.let {
                                    Toast.makeText(
                                        currentActivity,
                                        it,
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }
                            viewModel.setIsLoadingTo(false)
                        }
                    }
                )
            }

            composable(Auth.route) {
                AuthenticationScreen(
                    onClickLogin = { navController.navigateSingleTopTo(Login.route) },
                    onClickRegister = { navController.navigateSingleTopTo(Register.route) }
                )
            }

            composable(Register.route) {
                RegisterScreen(
                    onClickLogin = { navController.navigateSingleTopTo(Login.route) },
                    onGoogleSignInClick = { viewModel.signInWithGoogle(launcher) },
                    onConventionalRegisterClick = {
                        scope.launch {
                            viewModel.setIsLoadingTo(true)
                            val signInResult = viewModel.authClient.registerWithEmailAndPassword(it)
                            authViewModel.onSignInResult(signInResult)
                            viewModel.setIsLoadingTo(false)
                        }
                    }
                )
            }

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
            composable(
                DropdownTimer.route,
                enterTransition = { fadeIn() },
                exitTransition = { fadeOut() },
                deepLinks = DropdownTimer.deepLinks
            ) {
                DropdownTimerScreen(onNavIconClick = {
                    scope.launch {
                        drawerState.open()
                    }
                })
            }
            composable(
                Stopwatch.route,
                enterTransition = { fadeIn() },
                exitTransition = { fadeOut() },
            ) {
                StopwatchScreen(onNavIconClick = {
                    scope.launch {
                        drawerState.open()
                    }
                })
            }
            composable(
                UserProfile.route,
                enterTransition = { fadeIn() },
                exitTransition = { fadeOut() },
            ) {
                UserProfileScreen(onNavIconClick = {
                    scope.launch {
                        drawerState.open()
                    }
                }, onInvalidUser = {
                    navController.popBackStack()
                },
                    onDeleteUser = {
                        scope.launch {
                            viewModel.setIsLoadingTo(true)
                            authViewModel.resetState()
                            viewModel.userStore.cleanToken()
                            navController.navigateInclusive(Auth.route)
                            viewModel.setIsLoadingTo(false)
                        }
                    })
            }
        }
    }
}

@Composable
private fun ExitConfirmationDialog(
    viewModel: MainViewModel,
    authViewModel: AuthViewModel,
    navController: NavHostController
) {
    DefaultAlertDialog(
        title = stringResource(id = R.string.common_dialog_title),
        text = stringResource(id = R.string.auth_exit_confirmation_dialog_text),
        onDismissRequest = { viewModel.setExitConfirmationDialogVisibility(false) },
        onConfirm = {
            viewModel.setIsLoadingTo(true)
            viewModel.authClient.signOutUser()
            authViewModel.resetState()
            viewModel.setIsLoadingTo(false)
            viewModel.setExitConfirmationDialogVisibility(false)
            navController.navigateInclusive(Auth.route)
        }
    )
}

fun NavHostController.navigateSingleTopTo(route: String) = this.navigate(route) {
    launchSingleTop = true
    restoreState = true
}

fun NavHostController.navigateToTrainingForm(trainingId: String?) =
    this.navigateSingleTopTo("${Form.route}/$trainingId")

private fun NavHostController.navigateToTrainingLog(trainingId: String) =
    this.navigateSingleTopTo("${Log.route}/$trainingId")

private fun NavHostController.navigateInclusive(route: String) = this.navigate(route) {
    popUpTo(0)
}