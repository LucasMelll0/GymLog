package com.example.gymlog.ui.timer

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
import com.example.gymlog.ui.components.AppTimer
import com.example.gymlog.ui.theme.GymLogTheme

@Composable
fun TimerScreen() {
    Scaffold(bottomBar = { TimerBottomBar(onNavIconClick = {}) }) { paddingValues ->
        AppTimer(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        )
    }
}

@Composable
fun TimerBottomBar(onNavIconClick: () -> Unit) {
    BottomAppBar(actions = {
        IconButton(onClick = { /*TODO*/ }) {
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
        TimerScreen()
    }
}