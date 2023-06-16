import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.gymlog.navigation.Auth
import com.example.gymlog.navigation.Bmi
import com.example.gymlog.navigation.Form
import com.example.gymlog.navigation.Home
import com.example.gymlog.navigation.Log
import com.example.gymlog.navigation.Login
import com.example.gymlog.navigation.Register
import com.example.gymlog.ui.auth.AuthenticationScreen
import com.example.gymlog.ui.auth.authclient.AuthUiClient
import com.example.gymlog.ui.auth.LoginScreen
import com.example.gymlog.ui.auth.RegisterScreen
import com.example.gymlog.ui.auth.viewmodel.AuthViewModel
import com.example.gymlog.ui.bmi.BmiHistoricScreen
import com.example.gymlog.ui.components.AppNavigationDrawer
import com.example.gymlog.ui.form.TrainingFormScreen
import com.example.gymlog.ui.home.HomeScreen
import com.example.gymlog.ui.log.TrainingLogScreen
import com.example.gymlog.utils.BackPressHandler
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.android.gms.auth.api.identity.Identity
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val currentActivity = LocalContext.current as Activity
    val authViewModel: AuthViewModel = koinViewModel()
    val signInState by authViewModel.state.collectAsStateWithLifecycle()
    val authUiClient by lazy {
        AuthUiClient(
            context = currentActivity,
            oneTapClient = Identity.getSignInClient(currentActivity)
        )
    }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult(),
        onResult = { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                scope.launch {
                    val signInResult = authUiClient.signInWithIntent(
                        intent = result.data ?: return@launch
                    )
                    authViewModel.onSignInResult(signInResult)
                }
            }
        }
    )
    val signInWithGoogle = {
        scope.launch {
            val signInIntentSender = authUiClient.signInWithGoogle()
            launcher.launch(
                IntentSenderRequest.Builder(
                    signInIntentSender ?: return@launch
                ).build()
            )
        }
    }

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
                    onGoogleSignInClick = { signInWithGoogle() },
                    onClickRegister = { navController.navigateSingleTopTo(Register.route) },
                    onConventionalSignInClick = {
                        scope.launch {
                            val signInResult = authUiClient.signInWithEmailAndPassword(it)
                            authViewModel.onSignInResult(signInResult)
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
                    onGoogleSignInClick = { signInWithGoogle() },
                    onConventionalRegisterClick = {
                        scope.launch {
                            val signInResult = authUiClient.registerWithEmailAndPassword(it)
                            authViewModel.onSignInResult(signInResult)
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