package com.devmello.gymlog.ui.dropdown_timer

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
import com.devmello.gymlog.R
import com.devmello.gymlog.core.ui.ScaffoldConfig
import com.devmello.gymlog.core.ui.ScaffoldManager
import com.devmello.gymlog.ui.components.AppDropdownTimer
import com.devmello.gymlog.ui.theme.GymLogTheme

@Composable
fun DropdownTimerScreen(scaffoldManager: ScaffoldManager, modifier: Modifier = Modifier) {
    scaffoldManager.updateConfig(ScaffoldConfig(showBottomBar = false))
    AppDropdownTimer(
        modifier = modifier
            .fillMaxSize()
    )
}


@Preview
@Composable
fun TimerScreenPreview() {
    GymLogTheme {
        DropdownTimerScreen(scaffoldManager = ScaffoldManager())
    }
}