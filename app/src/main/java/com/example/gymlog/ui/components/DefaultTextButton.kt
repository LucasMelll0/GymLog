package com.example.gymlog.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.gymlog.ui.theme.GymLogTheme

@Composable
fun DefaultTextButton(
    text: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant,
        contentColor = MaterialTheme.colorScheme.primary,
        modifier = modifier
    ) {
        TextButton(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
            Text(text = text)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun DefaultTextButtonPreview() {
    GymLogTheme {
        DefaultTextButton(
            text = "teste"
        ) {}
    }
}