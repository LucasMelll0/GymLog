package com.example.gymlog.ui.stopwatch

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
import com.example.gymlog.R
import com.example.gymlog.ui.components.AppStopwatch
import com.example.gymlog.ui.stopwatch.viewmodel.StopwatchViewModel
import com.example.gymlog.ui.stopwatch.viewmodel.StopwatchViewModelImpl
import com.example.gymlog.ui.theme.GymLogTheme
import org.koin.androidx.compose.koinViewModel

@Composable
fun StopwatchScreen(
    onNavIconClick: () -> Unit,
    viewModel: StopwatchViewModel = koinViewModel<StopwatchViewModelImpl>()
) {
    Scaffold(bottomBar = { StopwatchBottomBar(onNavIconClick = onNavIconClick) }) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
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
}

@Composable
fun StopwatchBottomBar(onNavIconClick: () -> Unit) {
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
        StopwatchScreen(onNavIconClick = {}, viewModel = viewModel)
    }
}