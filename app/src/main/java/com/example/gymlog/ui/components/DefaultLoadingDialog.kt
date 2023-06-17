package com.example.gymlog.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.DialogProperties
import com.example.gymlog.R
import com.example.gymlog.ui.theme.GymLogTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoadingDialog(modifier: Modifier = Modifier) {
    AlertDialog(
        onDismissRequest = {},
        modifier = modifier.size(dimensionResource(id = R.dimen.default_icon_size)),
        properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
    ) {
        CircularProgressIndicator()
    }
}

@Preview
@Composable
fun LoadingDialogPreview() {
    GymLogTheme {
        LoadingDialog()
    }
}