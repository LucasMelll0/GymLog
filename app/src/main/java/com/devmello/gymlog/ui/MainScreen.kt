package com.devmello.gymlog.ui

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
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
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.devmello.gymlog.R
import com.devmello.gymlog.core.navigation.NavDestination
import com.devmello.gymlog.core.navigation.NavMethod
import com.devmello.gymlog.core.navigation.NavigationManager
import com.devmello.gymlog.core.ui.MessageManager
import com.devmello.gymlog.core.ui.ScaffoldManager
import com.devmello.gymlog.extensions.toNavRoute
import com.devmello.gymlog.navigation.AppNavHost
import com.devmello.gymlog.navigation.HomeDestination
import com.devmello.gymlog.navigation.NavRoute
import com.devmello.gymlog.navigation.navigateInclusive
import com.devmello.gymlog.navigation.viewmodel.MainViewModel
import com.devmello.gymlog.navigation.viewmodel.MainViewModelImpl
import com.devmello.gymlog.ui.auth.viewmodel.AuthViewModel
import com.devmello.gymlog.ui.auth.viewmodel.AuthViewModelImpl
import com.devmello.gymlog.ui.components.AppNavigationDrawer
import com.devmello.gymlog.ui.components.DefaultAlertDialog
import com.devmello.gymlog.ui.components.LoadingDialog
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun MainScreen(
    scaffoldManager: ScaffoldManager,
    messageManager: MessageManager,
    navigationManager: NavigationManager,
) {
    val navController = rememberNavController()

    // Context
    val context = LocalContext.current

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
    val currentUserdata by authViewModel.currentUser.collectAsStateWithLifecycle()

    // Route
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val canGoBack = navController.previousBackStackEntry != null
    val currentRoute = navBackStackEntry?.destination

    LaunchedEffect(key1 = messages) {
        if (messages.isNotEmpty()) {
            val message = messages.first()
            val messageText = message.text ?: message.textId?.let { context.getString(it) }
            if (messageText == null) {
                messageManager.removeMessage(message.id)
                return@LaunchedEffect
            }
            snackBarHostState.showSnackbar(
                messageText,
                withDismissAction = true,
                duration = message.duration.snackBarDuration
            )
            messageManager.removeMessage(message.id)
        }
    }

    DisposableEffect(currentRoute) {
        onDispose {
            scope.launch {
                drawerState.close()
            }
        }
    }

    AppNavigationDrawer(
        gesturesEnabled = config.drawerGesturesEnabled,
        currentDestinationRoute = navBackStackEntry?.toNavRoute() ?: NavRoute.Home,
        drawerState = drawerState,
        onItemClick = {
            val routeObj = it.navRoute::class
            if (currentRoute?.hasRoute(routeObj) != true) {
                navigationManager.navigate(route = it.navRoute, method = NavMethod.SINGLE_TOP)
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
            Scaffold(
                snackbarHost = { SnackbarHost(hostState = snackBarHostState) },
                topBar = {
                    if (config.showTopBar) {
                        CenterAlignedTopAppBar(
                            title = { Text(config.title) },
                            navigationIcon = {
                                val icon =
                                    if (canGoBack) Icons.AutoMirrored.Default.KeyboardArrowLeft else Icons.Default.Menu
                                val description = stringResource(
                                    if (canGoBack) R.string.common_go_to_back else R.string.common_open_navigation_drawer
                                )
                                IconButton(onClick = {
                                    if (canGoBack) {
                                        if (config.onNavigateBack()) {
                                            navController.popBackStack()
                                        }
                                    } else {
                                        scope.launch { drawerState.open() }
                                    }
                                }) {
                                    Icon(icon, contentDescription = description)
                                }

                            },
                            actions = config.tobBarActions
                        )
                    }
                },
                bottomBar = { if (config.showBottomBar) config.bottomBar },
                floatingActionButton = config.fab,
            ) { paddingValues ->
                Box(modifier = Modifier.padding(paddingValues)) {
                    AppNavHost(navController, scaffoldManager, navigationManager)
                }
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
            navController.navigateInclusive(NavRoute.Auth)
        }
    )
}