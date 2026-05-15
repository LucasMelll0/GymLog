package com.devmello.gymlog.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.devmello.gymlog.R
import com.devmello.gymlog.core.ui.MessageManager
import com.devmello.gymlog.core.ui.ScaffoldManager
import com.devmello.gymlog.navigation.AppNavHost
import com.devmello.gymlog.navigation.Auth
import com.devmello.gymlog.navigation.Home
import com.devmello.gymlog.navigation.navigateInclusive
import com.devmello.gymlog.navigation.navigateSingleTopTo
import com.devmello.gymlog.navigation.viewmodel.MainViewModel
import com.devmello.gymlog.navigation.viewmodel.MainViewModelImpl
import com.devmello.gymlog.ui.auth.viewmodel.AuthViewModel
import com.devmello.gymlog.ui.auth.viewmodel.AuthViewModelImpl
import com.devmello.gymlog.ui.components.AppNavigationDrawer
import com.devmello.gymlog.ui.components.DefaultAlertDialog
import com.devmello.gymlog.ui.components.LoadingDialog
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    scaffoldManager: ScaffoldManager,
    messageManager: MessageManager
) {
    val navController = rememberNavController()

    // ViewModels
    val viewModel: MainViewModel = koinViewModel<MainViewModelImpl>()
    val authViewModel: AuthViewModel = koinViewModel<AuthViewModelImpl>()

    // State
    val config by scaffoldManager.config.collectAsStateWithLifecycle()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val snackBarHostState = remember { SnackbarHostState() }
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val showExitConfirmationDialog by viewModel.showExitConfirmationDialog.collectAsStateWithLifecycle()

    // Messages
    val messages by messageManager.messages.collectAsStateWithLifecycle()

    // Coroutines
    val scope = rememberCoroutineScope()

    // User
    var currentUserdata by remember { mutableStateOf(authViewModel.currentUser) }

    // Route
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val signInState by authViewModel.state.collectAsStateWithLifecycle()


    LaunchedEffect(messages) {
        if (messages.isNotEmpty()) {
            val message = messages.first()
            snackBarHostState.showSnackbar(
                message.text,
                withDismissAction = true,
                duration = message.duration.snackBarDuration
            )
            messageManager.removeMessage(message.id)
        }
    }
    // TODO Pensar num navigation Manager e manter a lógica de postagem de mensagem dentro do authViewModel
    LaunchedEffect(signInState.signInError) { // TODO talvez dê para refatorar melhor
        signInState.signInError?.let { error ->
            messageManager.postMessage(error)
        }
    }

    LaunchedEffect(key1 = signInState.isSignInSuccessful) {
        if (signInState.isSignInSuccessful) {
            currentUserdata = authViewModel.currentUser
            navController.navigateSingleTopTo(Home.route)
        }
    }
    // TODO

    DisposableEffect(currentRoute) {
        onDispose {
            scope.launch {
                drawerState.close()
            }
        }
    }

    AppNavigationDrawer(
        gesturesEnabled = config.drawerGesturesEnabled,
        currentDestinationRoute = currentRoute ?: Home.route,
        drawerState = drawerState,
        onItemClick = {
            if (currentRoute != it.route) {
                scope.launch {
                    navController.navigateSingleTopTo(it.route)
                }
            }
        },
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
        Scaffold(
            snackbarHost = { SnackbarHost(hostState = snackBarHostState) },
            topBar = {
                if (config.showTopBar) {
                    CenterAlignedTopAppBar(
                        title = { Text(config.title) },
                        navigationIcon = {
                            IconButton(onClick = {
                                scope.launch { drawerState.open() }
                            }) {
                                Icon(Icons.Default.Menu, contentDescription = null)
                            }
                        },
                        actions = config.tobBarActions
                    )
                }
            },
            floatingActionButton = config.fab,
        ) { paddingValues ->
            Box(modifier = Modifier.padding(paddingValues)) {
                AppNavHost(navController, scaffoldManager)
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
            authViewModel.signOut()
            viewModel.setExitConfirmationDialogVisibility(false)
            navController.navigateInclusive(Auth.route)
        }
    )
}