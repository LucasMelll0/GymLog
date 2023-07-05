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
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.example.gymlog.ui.auth.authclient.UserCredentials
import com.example.gymlog.ui.components.DefaultPasswordTextField
import com.example.gymlog.ui.components.DefaultTextField
import com.example.gymlog.ui.components.GoogleSignInButton
import com.example.gymlog.ui.theme.GymLogTheme
import com.example.gymlog.utils.isValidEmail

@Composable
fun RegisterScreen(
    onClickLogin: () -> Unit,
    onGoogleSignInClick: () -> Unit,
    onConventionalRegisterClick: (UserCredentials) -> Unit
) {
    val context = LocalContext.current
    var userName: String by rememberSaveable { mutableStateOf("") }
    var email: String by rememberSaveable { mutableStateOf("") }
    var password: String by rememberSaveable { mutableStateOf("") }
    var passwordConfirmation: String by rememberSaveable { mutableStateOf("") }
    val isSamePasswords = password == passwordConfirmation
    var userNameHasError: Boolean by remember { mutableStateOf(false) }
    var emailHasError: Boolean by remember { mutableStateOf(false) }
    var passwordHasError: Boolean by remember { mutableStateOf(false) }
    var passwordConfirmationHasError: Boolean by remember { mutableStateOf(false) }
    val emailErrorMessage: @Composable () -> String = {
        if (email.isEmpty() || !isValidEmail(email)) {
            stringResource(
                id = if (email.isEmpty()) R.string.common_text_field_error_message else
                    R.string.auth_invalid_email_message
            )
        } else {
            ""
        }
    }
    val passwordErrorMessage: @Composable () -> String = {
        if (password.isEmpty() || !isSamePasswords) {
            stringResource(
                id = if (password.isEmpty()) R.string.common_text_field_error_message else
                    R.string.auth_passwords_do_not_match
            )
        } else {
            ""
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly,
    ) {
        Column(
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
                modifier = Modifier.fillMaxWidth(),
                value = userName,
                onValueChange = { userName = it },
                charLimit = 30,
                singleLine = true,
                label = {
                    Text(
                        text = stringResource(id = R.string.common_user_name)
                    )
                },
                leadingIcon = {
                    Icon(imageVector = Icons.Rounded.Person, contentDescription = null)
                },
                isError = userNameHasError
            )
            DefaultTextField(
                modifier = Modifier.fillMaxWidth(),
                value = email,
                onValueChange = { email = it },
                singleLine = true,
                errorMessage = emailErrorMessage(),
                label = {
                    Text(
                        text = stringResource(
                            id = R.string.common_email
                        )
                    )
                },
                leadingIcon = {
                    Icon(imageVector = Icons.Rounded.Email, contentDescription = null)
                },
                isError = emailHasError
            )
            DefaultPasswordTextField(
                modifier = Modifier.fillMaxWidth(),
                value = password,
                onValueChange = { password = it },
                label = {
                    Text(
                        text = stringResource(id = R.string.common_password)
                    )
                },
                leadingIcon = { Icon(imageVector = Icons.Rounded.Lock, contentDescription = null) },
                isError = passwordHasError,
                errorMessage = passwordErrorMessage()
            )
            DefaultPasswordTextField(
                modifier = Modifier.fillMaxWidth(),
                value = passwordConfirmation,
                onValueChange = { passwordConfirmation = it },
                label = {
                    Text(
                        text = stringResource(id = R.string.auth_password_confirmation)
                    )
                },
                leadingIcon = { Icon(imageVector = Icons.Rounded.Lock, contentDescription = null) },
                isError = passwordConfirmationHasError,
                errorMessage = passwordErrorMessage()
            )
            Button(onClick = {
                userNameHasError = userName.isEmpty()
                emailHasError = email.isEmpty() || !isValidEmail(email)
                passwordHasError = password.isEmpty() || password != passwordConfirmation
                passwordConfirmationHasError =
                    passwordConfirmation.isEmpty() || password != passwordConfirmation
                val hasError =
                    userNameHasError || emailHasError || passwordHasError || passwordConfirmationHasError
                if (!hasError) {
                    val userCredentials = UserCredentials(
                        email = email,
                        password = password,
                        userName = userName
                    )
                    onConventionalRegisterClick(userCredentials)
                } else {
                    Toast.makeText(context, "Tem erro", Toast.LENGTH_SHORT).show()
                }
            }, modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = stringResource(id = R.string.common_register),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(vertical = dimensionResource(id = R.dimen.default_padding))
                )
            }
        }
        GoogleSignInButton(
            onClick = onGoogleSignInClick
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = stringResource(id = R.string.auth_have_account_ask),
                color = MaterialTheme.colorScheme.onBackground
            )
            TextButton(onClick = onClickLogin) {
                Text(text = stringResource(id = R.string.common_login))
            }
        }
    }
}

@Preview(uiMode = UI_MODE_NIGHT_YES, showBackground = true)
@Preview(showBackground = true)
@Composable
fun RegisterScreenPreview() {
    GymLogTheme {
        RegisterScreen(
            onClickLogin = {},
            onGoogleSignInClick = {},
            onConventionalRegisterClick = {})
    }
}

