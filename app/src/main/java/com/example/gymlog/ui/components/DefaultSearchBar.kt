package com.example.gymlog.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import com.example.gymlog.R
import com.example.gymlog.ui.theme.GymLogTheme

@Composable
fun DefaultSearchBar(
    modifier: Modifier = Modifier,
    focusRequester: FocusRequester = FocusRequester(),
    value: String,
    onClickBackButton: () -> Unit,
    onClickClearText: () -> Unit,
    onValueChanged: (String) -> Unit
) {
    LaunchedEffect(key1 = Unit) {
        focusRequester.requestFocus()
    }
    Surface(color = MaterialTheme.colorScheme.surfaceVariant) {

        Row(
            modifier = modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.default_padding))
        ) {
            IconButton(onClick = onClickBackButton) {
                Icon(imageVector = Icons.Rounded.ArrowBack, contentDescription = "Voltar")
            }
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester),
                value = value,
                onValueChange = onValueChanged,
                visualTransformation = VisualTransformation.None,
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent
                ),
                suffix = if (value.isNotEmpty()) {
                    {
                        IconButton(onClick = onClickClearText) {
                            Icon(imageVector = Icons.Rounded.Close, contentDescription = "Clear")
                        }
                    }
                } else null
            )
        }
    }
}

@Preview(widthDp = 230)
@Preview
@Composable
private fun DefaultSearchBarPreview() {
    var text by remember {
        mutableStateOf("")
    }
    GymLogTheme {
        DefaultSearchBar(
            value = text,
            onValueChanged = { text = it },
            onClickClearText = { text = "" },
            onClickBackButton = {}
        )
    }
}