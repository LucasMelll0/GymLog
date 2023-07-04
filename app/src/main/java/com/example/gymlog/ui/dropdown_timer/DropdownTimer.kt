package com.example.gymlog.ui.dropdown_timer

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.gymlog.R
import com.example.gymlog.ui.components.AppDropdownTimer
import com.example.gymlog.ui.theme.GymLogTheme

@Composable
fun DropdownTimerScreen(onNavIconClick: () -> Unit) {
    Scaffold(bottomBar = { DropdownTimerBottomBar(onNavIconClick = onNavIconClick) }) { paddingValues ->
        AppDropdownTimer(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        )
    }
}

@Composable
fun DropdownTimerBottomBar(onNavIconClick: () -> Unit) {
    BottomAppBar(actions = {
        IconButton(onClick = onNavIconClick) {
            Icon(
                imageVector = Icons.Rounded.Menu,
                contentDescription = stringResource(id = R.string.common_open_navigation_drawer)
            )
        }
    })
}


@Preview
@Composable
fun TimerScreenPreview() {
    GymLogTheme {
        DropdownTimerScreen(onNavIconClick = {})
    }
}