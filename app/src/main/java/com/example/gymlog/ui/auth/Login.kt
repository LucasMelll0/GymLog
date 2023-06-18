package com.example.gymlog.ui.auth

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.gymlog.R
import com.example.gymlog.ui.auth.data.UserCredentials
import com.example.gymlog.ui.components.DefaultPasswordTextField
import com.example.gymlog.ui.components.DefaultTextField
import com.example.gymlog.ui.components.GoogleSignInButton
import com.example.gymlog.ui.theme.GymLogTheme
import com.example.gymlog.utils.isValidEmail

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onGoogleSignInClick: () -> Unit,
    onClickRegister: () -> Unit,
    onConventionalSignInClick: (UserCredentials) -> Unit,
    onSendResetPasswordEmailClick: (email: String) -> Unit
) {
    val context = LocalContext.current
    var email: String by rememberSaveable { mutableStateOf("") }
    var password: String by rememberSaveable { mutableStateOf("") }
    var emailHasError: Boolean by remember { mutableStateOf(false) }
    val emailErrorMessage: @Composable () -> String = {
        if (email.isEmpty() || !isValidEmail(email)) {
            stringResource(
                id = if (email.isEmpty()) R.string.common_text_field_error_message else R.string.auth_invalid_email_message
            )
        } else {
            ""
        }
    }
    var passwordHasError: Boolean by remember { mutableStateOf(false) }
    var showPasswordResetEmailBottomSheet: Boolean by rememberSaveable { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(
                dimensionResource(id = R.dimen.default_padding)
            ),
            modifier = Modifier.padding(
                horizontal = dimensionResource(id = R.dimen.extra_large_padding),
                vertical = dimensionResource(
                    id = R.dimen.large_padding
                )
            )
        ) {
            DefaultTextField(
                singleLine = true,
                value = email,
                onValueChange = { email = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(text = stringResource(id = R.string.common_email)) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Rounded.Email, contentDescription = null
                    )
                },
                isError = emailHasError,
                errorMessage = emailErrorMessage(),
            )
            DefaultPasswordTextField(
                modifier = Modifier.fillMaxWidth(),
                value = password,
                onValueChange = { password = it },
                label = { Text(text = stringResource(id = R.string.common_password)) },
                leadingIcon = { Icon(imageVector = Icons.Rounded.Lock, contentDescription = null) },
                isError = passwordHasError,
            )
            Button(onClick = {
                emailHasError = email.isEmpty() || !isValidEmail(email)
                passwordHasError = password.isEmpty()
                if (!emailHasError && !passwordHasError) {
                    val userCredentials = UserCredentials(email = email, password = password)
                    onConventionalSignInClick(userCredentials)
                } else {
                    Toast.makeText(
                        context,
                        context.getString(R.string.auth_invalid_credentials_message),
                        Toast.LENGTH_SHORT
                    ).show()
                }

            }, modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = stringResource(id = R.string.common_login),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(vertical = dimensionResource(id = R.dimen.default_padding))
                )
            }
            TextButton(onClick = { showPasswordResetEmailBottomSheet = true }) {
                Text(
                    text = stringResource(id = R.string.auth_forgot_password_ask),
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
        GoogleSignInButton(onClick = onGoogleSignInClick)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = stringResource(id = R.string.auth_no_account_ask),
                color = MaterialTheme.colorScheme.onBackground
            )
            TextButton(onClick = onClickRegister) {
                Text(text = stringResource(id = R.string.common_register))
            }
        }
    }
    if (showPasswordResetEmailBottomSheet) {
        SendPasswordResetEmailBottomSheet(
            onDismissRequest = {
                showPasswordResetEmailBottomSheet = false
            },
            onConfirm = {
                onSendResetPasswordEmailClick(it)
            }
        )
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SendPasswordResetEmailBottomSheet(
    onDismissRequest: () -> Unit,
    sheetState: SheetState = rememberModalBottomSheetState(),
    onConfirm: (email: String) -> Unit
) {
    var email: String by rememberSaveable { mutableStateOf("") }
    var emailHasError: Boolean by remember { mutableStateOf(false) }
    val emailErrorMessage: @Composable () -> String = {
        if (email.isEmpty() || !isValidEmail(email)) {
            stringResource(
                id = if (email.isEmpty()) R.string.common_text_field_error_message else R.string.auth_invalid_email_message
            )
        } else {
            ""
        }
    }
    ModalBottomSheet(onDismissRequest = onDismissRequest, sheetState = sheetState) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(
                dimensionResource(id = R.dimen.large_padding)
            ),
            modifier = Modifier.padding(dimensionResource(id = R.dimen.large_padding))
        ) {
            Text(text = stringResource(id = R.string.auth_send_password_reset_email_bottom_sheet_message))
            DefaultTextField(
                value = email,
                onValueChange = { email = it },
                isError = emailHasError,
                errorMessage = emailErrorMessage(),
                label = { Text(text = stringResource(id = R.string.common_email)) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Rounded.Email, contentDescription = null
                    )
                },
                modifier = Modifier.fillMaxWidth()
            )
            Button(
                onClick = {
                    emailHasError = email.isEmpty() || !isValidEmail(email)
                    if (!emailHasError) {
                        onConfirm(email)
                    }
                }, modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(id = R.string.common_confirm),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(
                        vertical = dimensionResource(
                            id = R.dimen.default_padding
                        )
                    )
                )
            }

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun SendPasswordResetEmailBottomSheetPreview() {
    GymLogTheme {
        SendPasswordResetEmailBottomSheet(onDismissRequest = {},
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            onConfirm = {})
    }
}

@Preview(showSystemUi = true, uiMode = UI_MODE_NIGHT_YES, name = "Night")
@Preview(showSystemUi = true)
@Composable
fun LoginScreenPreview() {
    GymLogTheme {
        LoginScreen(
            onGoogleSignInClick = {},
            onClickRegister = {},
            onConventionalSignInClick = {},
            onSendResetPasswordEmailClick = {})
    }
}