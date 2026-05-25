package com.devmello.gymlog.ui.stopwatch

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModel
import com.devmello.gymlog.R
import com.devmello.gymlog.core.ui.ScaffoldConfig
import com.devmello.gymlog.core.ui.ScaffoldManager
import com.devmello.gymlog.ui.components.AppStopwatch
import com.devmello.gymlog.ui.stopwatch.viewmodel.StopwatchViewModel
import com.devmello.gymlog.ui.stopwatch.viewmodel.StopwatchViewModelImpl
import com.devmello.gymlog.ui.theme.GymLogTheme
import org.koin.androidx.compose.koinViewModel

@Composable
fun StopwatchScreen(
    scaffoldManager: ScaffoldManager,
    modifier: Modifier = Modifier,
    viewModel: StopwatchViewModel = koinViewModel<StopwatchViewModelImpl>()
) {
    scaffoldManager.updateConfig(ScaffoldConfig(showBottomBar = false))
    Box(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        AppStopwatch(
            savedTimesList = viewModel.savedTimes,
            onSaveTime = { viewModel.saveTime(it) },
            onReset = { viewModel.reset() },
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Preview
@Composable
fun StopwatchScreenPreview() {
    GymLogTheme {
        val viewModel = object : StopwatchViewModel, ViewModel() {
            override val savedTimes: List<Long>
                get() = mutableStateListOf()

            override fun saveTime(time: Long) {

            }

            override fun reset() {

            }
        }
        StopwatchScreen(viewModel = viewModel, scaffoldManager = ScaffoldManager())
    }
}