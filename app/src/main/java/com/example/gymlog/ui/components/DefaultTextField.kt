package com.example.gymlog.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.text.isDigitsOnly
import com.example.gymlog.R
import com.example.gymlog.ui.theme.GymLogTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DefaultTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    errorMessage: String? = null,
    supportingText: String? = null,
    charLimit: Int? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default
) {
    OutlinedTextField(
        value = value,
        onValueChange = { newValue ->
            charLimit?.let {
                if (newValue.length <= charLimit) onValueChange(newValue)
            } ?: onValueChange(newValue)
        },
        isError = isError,
        colors = TextFieldDefaults.textFieldColors(containerColor = Color.Transparent),
        supportingText = {
            if (isError) {
                errorMessage?.let {
                    Text(text = errorMessage)
                }
            } else {
                Column {
                    supportingText?.let {
                        Text(text = it)
                    }
                    charLimit?.let {
                        Text(
                            text = stringResource(
                                R.string.default_text_field_char_limit_place_holder,
                                value.length,
                                charLimit
                            )
                        )
                    }

                }
            }
        },
        label = label,
        keyboardOptions = keyboardOptions,
        modifier = modifier,

        )
}

@Preview(showBackground = true)
@Composable
private fun DefaultTextFieldPreview() {
    var text by remember {
        mutableStateOf("")
    }
    var hasError by remember {
        mutableStateOf(false)
    }
    GymLogTheme {
        DefaultTextField(
            text,
            {
                text = it
                hasError = it.isDigitsOnly()
            },
            { Text(text = "Test") },
            charLimit = 10,
            isError = hasError,
            errorMessage = "Deu erro",
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth()
        )
    }
}