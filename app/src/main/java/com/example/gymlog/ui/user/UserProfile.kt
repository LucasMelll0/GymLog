package com.example.gymlog.ui.user

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.gymlog.R
import com.example.gymlog.data.datastore.UserStore
import com.example.gymlog.extensions.capitalizeAllWords
import com.example.gymlog.extensions.checkConnection
import com.example.gymlog.ui.components.DefaultPasswordTextField
import com.example.gymlog.ui.components.DefaultTextField
import com.example.gymlog.ui.components.LoadingDialog
import com.example.gymlog.ui.theme.GymLogTheme
import com.example.gymlog.ui.user.viewmodel.UserProfileViewModel
import com.google.firebase.auth.EmailAuthProvider
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun UserProfileScreen(
    viewModel: UserProfileViewModel = koinViewModel(),
    onNavIconClick: () -> Unit,
    onInvalidUser: () -> Unit,
    onDeleteUser: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val googleIdToken by UserStore(context).getAccessToken.collectAsStateWithLifecycle(null)
    var isLoading by rememberSaveable { mutableStateOf(false) }
    val snackBarHostState = remember { SnackbarHostState() }
    val user by viewModel.user.collectAsStateWithLifecycle()
    user ?: onInvalidUser()
    var showChangeUsernameBottomSheet by rememberSaveable { mutableStateOf(false) }
    var showChangePasswordBottomSheet by rememberSaveable { mutableStateOf(false) }
    var showDeleteAccountBottomSheet by rememberSaveable { mutableStateOf(false) }
    val isEmailAuthProvider = viewModel.userProvider == EmailAuthProvider.PROVIDER_ID
    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackBarHostState) },
        bottomBar = { UserProfileBottomBar(onNavIconClick = onNavIconClick) }) { paddingValues ->
        if (isLoading) Box(modifier = Modifier.fillMaxSize()) {
            LoadingDialog()
        }
        if (showChangeUsernameBottomSheet) ChangeUsernameBottomSheet(
            onConfirm = {
                showChangeUsernameBottomSheet = false
                scope.launch {
                    context.checkConnection(onNotConnected = {
                        snackBarHostState.showSnackbar(
                            message = context.getString(R.string.common_offline_message),
                            withDismissAction = true
                        )
                    }) {
                        isLoading = true
                        viewModel.changeUsername(it) {
                            snackBarHostState.showSnackbar(
                                message = context.getString(R.string.user_profile_change_username_error),
                                withDismissAction = true
                            )
                        }
                        isLoading = false
                    }
                }
            }, onDismissRequest = { showChangeUsernameBottomSheet = false })

        if (showChangePasswordBottomSheet) ChangePasswordBottomSheet(
            needPasswordToReauthenticate = isEmailAuthProvider,
            onConfirm = { oldPassword, newPassword ->
                showChangePasswordBottomSheet = false
                scope.launch {
                    context.checkConnection(onNotConnected = {
                        snackBarHostState.showSnackbar(
                            message = context.getString(R.string.common_offline_message),
                            withDismissAction = true
                        )
                    }) {
                        isLoading = true
                        val response =
                            viewModel.changePassword(oldPassword, newPassword, googleIdToken)
                        isLoading = false
                        if (response.isSuccess) {
                            snackBarHostState.showSnackbar(
                                context.getString(R.string.user_profile_change_password_success_message),
                                withDismissAction = true
                            )
                        } else {
                            snackBarHostState.showSnackbar(
                                context.getString(R.string.user_profile_change_password_error),
                                withDismissAction = true
                            )
                        }
                    }
                }
            }) {
            showChangePasswordBottomSheet = false
        }

        if (showDeleteAccountBottomSheet) DeleteAccountBottomSheet(
            needPasswordToReauthenticate = isEmailAuthProvider,
            onConfirm = {
                showDeleteAccountBottomSheet = false
                scope.launch {
                    isLoading = true
                    val response = viewModel.deleteUser(it, googleIdToken)
                    if (response.isSuccess) {
                        onDeleteUser()
                    } else {
                        snackBarHostState.showSnackbar(
                            context.getString(R.string.user_profile_delete_user_error),
                            withDismissAction = true
                        )
                    }
                    isLoading = false
                }
            }, onDismissRequest = { showDeleteAccountBottomSheet = false })
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            user?.let { user ->
                user.displayName?.let {
                    Text(
                        text = it.capitalizeAllWords(),
                        style = MaterialTheme.typography.displaySmall,
                        modifier = Modifier.padding(
                            dimensionResource(id = R.dimen.large_padding)
                        )
                    )
                }
                Column(
                    verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.default_padding)),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(dimensionResource(id = R.dimen.extra_large_padding))
                ) {
                    Button(
                        onClick = {
                            showChangeUsernameBottomSheet = true
                        }, Modifier.fillMaxWidth()
                    ) {
                        Text(text = stringResource(id = R.string.user_profile_change_username_button))
                    }
                    Button(
                        onClick = { showChangePasswordBottomSheet = true }, Modifier.fillMaxWidth()
                    ) {
                        Text(text = stringResource(id = R.string.user_profile_change_password_button))
                    }
                    OutlinedButton(
                        onClick = { showDeleteAccountBottomSheet = true },
                        Modifier
                            .fillMaxWidth()
                            .padding(
                                top = dimensionResource(
                                    id = R.dimen.large_padding
                                )
                            )
                    ) {
                        Text(text = stringResource(id = R.string.user_profile_delete_account_button))
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ChangePasswordBottomSheet(
    needPasswordToReauthenticate: Boolean,
    onConfirm: (oldPassword: String, newPassword: String) -> Unit,
    onDismissRequest: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ) {
        var oldPassword by rememberSaveable { mutableStateOf("") }
        var newPassword by rememberSaveable { mutableStateOf("") }
        var newPasswordConfirmation by rememberSaveable { mutableStateOf("") }
        var oldPasswordHasError by remember { mutableStateOf(false) }
        var newPasswordHasError: Boolean by remember { mutableStateOf(false) }
        var newPasswordConfirmationHasError: Boolean by remember { mutableStateOf(false) }
        val isSamePasswords = newPassword == newPasswordConfirmation
        val oldPasswordErrorMessage: @Composable () -> String = {
            if (oldPassword.isEmpty()) {
                stringResource(
                    id = R.string.common_text_field_error_message
                )
            } else {
                ""
            }
        }
        val newPasswordErrorMessage: @Composable () -> String = {
            if (newPassword.isEmpty() || !isSamePasswords) {
                stringResource(
                    id = if (newPassword.isEmpty()) R.string.common_text_field_error_message else R.string.auth_passwords_do_not_match
                )
            } else {
                ""
            }
        }
        Column(
            Modifier
                .fillMaxWidth()
                .padding(dimensionResource(id = R.dimen.large_padding)),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.default_padding))
        ) {
            if (needPasswordToReauthenticate) {
                DefaultPasswordTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = oldPassword,
                    onValueChange = { oldPassword = it },
                    label = {
                        Text(
                            text = stringResource(id = R.string.user_profile_change_password_old_password_label)
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Rounded.Lock,
                            contentDescription = null
                        )
                    },
                    isError = oldPasswordHasError,
                    errorMessage = oldPasswordErrorMessage()
                )
            }
            DefaultPasswordTextField(
                modifier = Modifier.fillMaxWidth(),
                value = newPassword,
                onValueChange = { newPassword = it },
                label = {
                    Text(
                        text = stringResource(id = R.string.user_profile_change_password_new_password_label)
                    )
                },
                leadingIcon = { Icon(imageVector = Icons.Rounded.Lock, contentDescription = null) },
                isError = newPasswordHasError,
                errorMessage = newPasswordErrorMessage()
            )
            DefaultPasswordTextField(
                modifier = Modifier.fillMaxWidth(),
                value = newPasswordConfirmation,
                onValueChange = { newPasswordConfirmation = it },
                label = {
                    Text(
                        text = stringResource(id = R.string.auth_password_confirmation)
                    )
                },
                leadingIcon = { Icon(imageVector = Icons.Rounded.Lock, contentDescription = null) },
                isError = newPasswordConfirmationHasError,
                errorMessage = newPasswordErrorMessage()
            )
            Button(
                onClick = {
                    oldPasswordHasError = oldPassword.isEmpty()
                    newPasswordHasError = newPassword.isEmpty() || !isSamePasswords
                    newPasswordConfirmationHasError =
                        newPasswordConfirmation.isEmpty() || !isSamePasswords
                    val hasError =
                        (oldPasswordHasError && needPasswordToReauthenticate) || newPasswordHasError || newPasswordConfirmationHasError
                    if (!hasError) {
                        onConfirm(oldPassword, newPassword)
                    }
                }, modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = stringResource(id = R.string.common_confirm))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DeleteAccountBottomSheet(
    onConfirm: (password: String) -> Unit,
    onDismissRequest: () -> Unit,
    needPasswordToReauthenticate: Boolean
) {
    var password by rememberSaveable { mutableStateOf("") }
    var hasError by remember { mutableStateOf(false) }
    val passwordErrorMessage: @Composable () -> String = {
        if (password.isEmpty()) {
            stringResource(id = R.string.common_text_field_error_message)
        } else {
            ""
        }
    }
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensionResource(id = R.dimen.large_padding)),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.default_padding))
        ) {
            Text(
                text = stringResource(id = R.string.user_profile_delete_account_warning),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.fillMaxWidth()

            )
            if (needPasswordToReauthenticate) {
                Text(
                    text = stringResource(id = R.string.user_profile_delete_account_password_request),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.fillMaxWidth()
                )
                DefaultPasswordTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = {
                        Text(
                            text = stringResource(id = R.string.common_password)
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Rounded.Lock,
                            contentDescription = null
                        )
                    },
                    isError = hasError,
                    errorMessage = passwordErrorMessage(),
                    modifier = Modifier.fillMaxWidth()
                )
            }
            Button(onClick = {
                hasError = password.isEmpty() && needPasswordToReauthenticate
                if (!hasError) {
                    onConfirm(password)
                }
            }, modifier = Modifier.fillMaxWidth()) {
                Text(text = stringResource(id = R.string.common_confirm))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ChangeUsernameBottomSheet(onConfirm: (String) -> Unit, onDismissRequest: () -> Unit) {
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ) {
        var username by rememberSaveable { mutableStateOf("") }
        var usernameHasError by remember { mutableStateOf(false) }
        val usernameErrorMessage: @Composable () -> String = {
            if (username.isEmpty()) {
                stringResource(id = R.string.common_text_field_error_message)
            } else {
                ""
            }
        }
        Column(
            Modifier
                .fillMaxWidth()
                .padding(dimensionResource(id = R.dimen.large_padding)),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.default_padding))
        ) {
            Text(text = stringResource(id = R.string.user_profile_change_username_text_bottom_sheet))
            DefaultTextField(
                value = username,
                onValueChange = { username = it },
                isError = usernameHasError,
                errorMessage = usernameErrorMessage(),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                charLimit = 30
            )
            Button(
                onClick = {
                    usernameHasError = username.isEmpty()
                    if (!usernameHasError) {
                        onConfirm(username)
                    }
                }, modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = stringResource(id = R.string.common_confirm))
            }
        }
    }
}

@Composable
fun UserProfileBottomBar(onNavIconClick: () -> Unit) {
    BottomAppBar(actions = {
        IconButton(onClick = onNavIconClick) {
            Icon(
                imageVector = Icons.Rounded.Menu,
                contentDescription = stringResource(id = R.string.common_open_navigation_drawer)
            )
        }
    })
}

@Preview(uiMode = UI_MODE_NIGHT_YES)
@Preview
@Composable
fun UserProfileScreenPreview() {
    GymLogTheme {
        UserProfileScreen(onNavIconClick = {}, onInvalidUser = {}, onDeleteUser = {})
    }
}