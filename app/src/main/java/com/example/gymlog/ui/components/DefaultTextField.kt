package com.example.gymlog.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.text.isDigitsOnly
import com.example.gymlog.R
import com.example.gymlog.ui.theme.GymLogTheme

@Composable
fun DefaultOutlinedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    leadingIcon: @Composable() (() -> Unit)? = null,
    errorMessage: String = stringResource(id = R.string.common_text_field_error_message),
    supportingText: String? = null,
    charLimit: Int? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    prefix: @Composable (() -> Unit)? = null,
    suffix: @Composable (() -> Unit)? = null
) {
    OutlinedTextField(
        suffix = suffix,
        prefix = prefix,
        leadingIcon = leadingIcon,
        value = value,
        onValueChange = { newValue ->
            charLimit?.let {
                if (newValue.length <= charLimit) onValueChange(newValue)
            } ?: onValueChange(newValue)
        },
        isError = isError,
        supportingText = {
            if (isError) {
                Text(text = errorMessage)
            } else {
                Column {
                    charLimit?.let {
                        Text(
                            text = stringResource(
                                R.string.default_text_field_char_limit_place_holder,
                                value.length,
                                charLimit
                            )
                        )
                    }
                    supportingText?.let {
                        Text(text = it)
                    }

                }
            }
        },
        label = label,
        keyboardOptions = keyboardOptions,
        modifier = modifier,

        )
}

@Composable
fun DefaultTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: @Composable() (() -> Unit)? = null,
    leadingIcon: @Composable() (() -> Unit)? = null,
    isError: Boolean = false,
    charLimit: Int? = null,
    errorMessage: String = stringResource(id = R.string.common_text_field_error_message),
    supportingText: String? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    singleLine: Boolean = false,
) {
    TextField(
        singleLine = singleLine,
        modifier = modifier,
        leadingIcon = leadingIcon,
        label = label,
        value = value,
        isError = isError,
        supportingText = {
            if (isError) {
                Text(text = errorMessage)
            } else {
                Column {
                    charLimit?.let {
                        Text(
                            text = stringResource(
                                R.string.default_text_field_char_limit_place_holder,
                                value.length,
                                charLimit
                            )
                        )
                    }
                    supportingText?.let {
                        Text(text = it)
                    }

                }
            }
        },
        onValueChange = onValueChange,
        shape = MaterialTheme.shapes.extraLarge,
        colors = TextFieldDefaults.colors(
            focusedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
            focusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            errorIndicatorColor = Color.Transparent,
            cursorColor = MaterialTheme.colorScheme.onSurfaceVariant,

            ),
        keyboardOptions = keyboardOptions,

        )
}

@Composable
fun DefaultPasswordTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: @Composable() (() -> Unit)? = null,
    leadingIcon: @Composable() (() -> Unit)? = null,
    maxLines: Int = 1,
    isError: Boolean = false,
    errorMessage: String = stringResource(id = R.string.common_text_field_error_message),
    supportingText: String? = null,
) {
    val focusManager = LocalFocusManager.current
    var showPassword: Boolean by remember { mutableStateOf(false) }
    TextField(
        maxLines = maxLines,
        modifier = modifier,
        leadingIcon = leadingIcon,
        trailingIcon = {
            val (icon, iconColor) = if (showPassword) {
                Pair(
                    painterResource(id = R.drawable.ic_visibility),
                    MaterialTheme.colorScheme.tertiary
                )
            } else {
                Pair(
                    painterResource(id = R.drawable.ic_visibility_off),
                    MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            IconButton(onClick = { showPassword = !showPassword }) {
                Icon(
                    painter = icon,
                    contentDescription = stringResource(id = R.string.common_visibility),
                    tint = iconColor
                )
            }
        },
        visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
        label = label,
        value = value,
        isError = isError,
        supportingText = {
            if (isError) {
                Text(text = errorMessage)
            } else {
                supportingText?.let {
                    Text(text = it)
                }
            }
        },
        singleLine = true,
        onValueChange = onValueChange,
        shape = MaterialTheme.shapes.extraLarge,
        colors = TextFieldDefaults.colors(
            focusedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
            focusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            errorIndicatorColor = Color.Transparent,
            cursorColor = MaterialTheme.colorScheme.onSurfaceVariant,

            ),
        keyboardOptions = KeyboardOptions.Default.copy(
            autoCorrect = true,
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(
            onDone = {
                focusManager.clearFocus()
            }
        )
    )
}

@Preview
@Composable
fun DefaultPasswordTextFieldPreview() {
    GymLogTheme {
        var value by remember { mutableStateOf("") }
        DefaultPasswordTextField(value = value, onValueChange = { value = it })
    }
}

@Preview(backgroundColor = 0xFFFFFFFF, showBackground = true)
@Composable
fun DefaultTextFieldPreview() {
    GymLogTheme {
        var value by remember { mutableStateOf("") }
        DefaultTextField(
            value = value,
            onValueChange = { value = it },
            modifier = Modifier.padding(16.dp),
            label = { Text(text = "Teste") },
            leadingIcon = { Icon(imageVector = Icons.Rounded.Email, contentDescription = null) },
            isError = true,
            supportingText = "Teste",
            singleLine = false,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun DefaultOutlinedTextFieldPreview() {
    var text by remember {
        mutableStateOf("")
    }
    var hasError by remember {
        mutableStateOf(false)
    }
    GymLogTheme {
        DefaultOutlinedTextField(
            value = text,
            onValueChange = {
                text = it
                hasError = it.isDigitsOnly()
            },
            label = { Text(text = "Test") },
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            isError = hasError,
            leadingIcon = { Icon(imageVector = Icons.Rounded.Edit, contentDescription = null) },
            errorMessage = "Deu erro",
            charLimit = 10,
        )
    }
}