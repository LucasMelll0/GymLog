package com.devmello.gymlog.ui.user

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.devmello.gymlog.R
import com.devmello.gymlog.core.model.UserData
import com.devmello.gymlog.core.ui.MessageManager
import com.devmello.gymlog.core.ui.ScaffoldConfig
import com.devmello.gymlog.core.ui.ScaffoldManager
import com.devmello.gymlog.extensions.capitalizeAllWords
import com.devmello.gymlog.ui.components.DefaultAsyncImage
import com.devmello.gymlog.ui.components.DefaultPasswordTextField
import com.devmello.gymlog.ui.components.DefaultTextField
import com.devmello.gymlog.ui.theme.GymLogTheme
import com.devmello.gymlog.ui.user.viewmodel.UserProfileViewModel
import com.devmello.gymlog.ui.user.viewmodel.UserProfileViewModelImpl
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.firebase.auth.EmailAuthProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.koin.androidx.compose.get
import org.koin.androidx.compose.koinViewModel
import java.util.Date
import java.util.UUID

@Suppress("AssignedValueIsNeverRead")
@Composable
fun UserProfileScreen(
    scaffoldManager: ScaffoldManager,
    modifier: Modifier = Modifier,
    messageManager: MessageManager = get<MessageManager>(),
    viewModel: UserProfileViewModel = koinViewModel<UserProfileViewModelImpl>(),
    onInvalidUser: () -> Unit,
    onDeleteUser: () -> Unit
) {
    val user by viewModel.user.collectAsStateWithLifecycle()
    LaunchedEffect(key1 = user) {
        user ?: onInvalidUser()
    }
    // Initial Config
    scaffoldManager.updateConfig(ScaffoldConfig())

    // Dialogs & BottomSheet flags
    var showChangeUsernameBottomSheet by rememberSaveable { mutableStateOf(false) }
    var showChangePasswordBottomSheet by rememberSaveable { mutableStateOf(false) }
    var showDeleteAccountBottomSheet by rememberSaveable { mutableStateOf(false) }

    val isEmailAuthProvider = viewModel.userProvider == EmailAuthProvider.PROVIDER_ID

    if (showChangeUsernameBottomSheet) ChangeUsernameBottomSheet(onConfirm = {
        showChangeUsernameBottomSheet = false
        viewModel.changeUsername(it) {
            messageManager.postMessage(R.string.user_profile_change_username_error)
        }


    }, onDismissRequest = { showChangeUsernameBottomSheet = false })

    if (showChangePasswordBottomSheet) ChangePasswordBottomSheet(
        needPasswordToReauthenticate = isEmailAuthProvider,
        onConfirm = { oldPassword, newPassword ->
            showChangePasswordBottomSheet = false
            viewModel.changePassword(
                oldPassword,
                newPassword,
                onSuccess = { messageManager.postMessage(textId = R.string.user_profile_change_password_success_message) },
                onFailed = { messageManager.postMessage(textId = R.string.user_profile_change_password_error) })
        },
        onDismissRequest = { showChangePasswordBottomSheet = false })

    if (showDeleteAccountBottomSheet) DeleteAccountBottomSheet(
        needPasswordToReauthenticate = isEmailAuthProvider,
        onConfirm = {
            showDeleteAccountBottomSheet = false
            viewModel.deleteUser(
                it,
                onSuccess = onDeleteUser,
                onFailed = { messageManager.postMessage(textId = R.string.user_profile_delete_user_error) })

        },
        onDismissRequest = { showDeleteAccountBottomSheet = false })
    UserProfileContent(
        user = user,
        isEmailAuthProvider = isEmailAuthProvider,
        onChangeUsernameClick = { showChangeUsernameBottomSheet = true },
        onChangePasswordClick = { showChangePasswordBottomSheet = true },
        onDeleteUserClick = { showDeleteAccountBottomSheet = true },
        onInvalidUser = onInvalidUser,
        modifier = modifier
    )
}

@Composable
@OptIn(ExperimentalPermissionsApi::class)
private fun UserProfileContent(
    user: UserData?,
    onChangeUsernameClick: () -> Unit,
    onChangePasswordClick: () -> Unit,
    onDeleteUserClick: () -> Unit,
    onInvalidUser: () -> Unit,
    modifier: Modifier = Modifier,
    isEmailAuthProvider: Boolean
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        user?.let { user ->
            user.userName?.let {
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
                Button(onClick = onChangeUsernameClick, Modifier.fillMaxWidth()) {
                    Text(text = stringResource(id = R.string.user_profile_change_username_button))
                }
                if (isEmailAuthProvider) {
                    Button(onClick = onChangePasswordClick, Modifier.fillMaxWidth()) {
                        Text(text = stringResource(id = R.string.user_profile_change_password_button))
                    }
                    OutlinedButton(
                        onClick = onDeleteUserClick,
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
        } ?: onInvalidUser()
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
                            imageVector = Icons.Rounded.Lock, contentDescription = null
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
                            imageVector = Icons.Rounded.Lock, contentDescription = null
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangeUserPhotoDialog(
    photoUri: Uri?,
    onConfirm: () -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier
) {
    BasicAlertDialog(
        onDismissRequest = onDismissRequest,
        modifier = modifier,
        properties = DialogProperties(
            dismissOnClickOutside = false,
        ),
        content = {
            Card(modifier = Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.extraLarge) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.default_padding)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(dimensionResource(id = R.dimen.large_padding))
                        .verticalScroll(rememberScrollState())
                ) {
                    DefaultAsyncImage(
                        data = photoUri,
                        diskCacheKey = "user_image_selection_${Date().time}",
                        error = painterResource(id = R.drawable.ic_person),
                        contentDescription = stringResource(id = R.string.user_profile_photo_content_description),
                        modifier = Modifier
                            .size(dimensionResource(id = R.dimen.user_profile_photo_size))
                            .padding(dimensionResource(id = R.dimen.large_padding))
                            .clip(MaterialTheme.shapes.extraLarge)
                    )
                    Text(text = "Deseja usar essa imagem como foto de perfil?")
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedButton(onClick = onDismissRequest) {
                            Text(text = stringResource(id = R.string.common_cancel))
                        }
                        Button(onClick = onConfirm) {
                            Text(text = stringResource(id = R.string.common_confirm))
                        }
                    }
                }
            }
        })
}

@Preview
@Composable
fun ChangeUserPhotoDialogPreview() {
    GymLogTheme {
        ChangeUserPhotoDialog(photoUri = null, onDismissRequest = {}, onConfirm = {})
    }
}

@Preview(uiMode = UI_MODE_NIGHT_YES)
@Preview
@Composable
fun UserProfileScreenPreview() {

    val user = UserData(
        uid = UUID.randomUUID().toString(),
        userName = "Lucas Mello",
        profilePicture = "https://lh3.googleusercontent.com/a/AAcHTtdAXtc4cK3p7aYBgQHfi585k0d7_s2ZkLZwXwsTb-qZRQ=s288-c-no",
    )
    GymLogTheme {
        val viewModel = object : UserProfileViewModel, ViewModel() {
            override val user: StateFlow<UserData?>
                get() = MutableStateFlow(user)
            override val userProvider: String?
                get() = null

            override fun changeUsername(username: String, onFailed: () -> Unit) {
                TODO("Not yet implemented")
            }

            override fun changePassword(
                oldPassword: String,
                newPassword: String,
                onSuccess: () -> Unit,
                onFailed: () -> Unit
            ) {
                TODO("Not yet implemented")
            }

            override fun deleteUser(password: String, onSuccess: () -> Unit, onFailed: () -> Unit) {
                TODO("Not yet implemented")
            }


        }
        UserProfileScreen(
            scaffoldManager = ScaffoldManager(),
            messageManager = MessageManager(),
            onInvalidUser = {},
            onDeleteUser = {},
            viewModel = viewModel
        )
    }
}