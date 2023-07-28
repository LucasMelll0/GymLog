package com.example.gymlog.ui.components

import android.content.Intent
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.os.Build
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.gymlog.R
import com.example.gymlog.services.StopwatchService
import com.example.gymlog.ui.theme.GymLogTheme
import com.example.gymlog.utils.formatTime
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun AppStopwatch(
    savedTimesList: List<Long>,
    onSaveTime: (Long) -> Unit,
    onReset: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val notificationPermissionState = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        rememberPermissionState(permission = android.Manifest.permission.POST_NOTIFICATIONS)
    } else null
    val stopwatchService = Intent(context, StopwatchService::class.java)
    val isRunning by StopwatchService.isRunning.collectAsStateWithLifecycle(false)
    val currentTime by StopwatchService.currentTime.collectAsStateWithLifecycle(initialValue = 0L)
    Column(modifier = modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        StopwatchFace(
            timeInMillis = currentTime,
            modifier.padding(dimensionResource(id = R.dimen.large_padding))
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            AnimatedVisibility(
                visible = isRunning,
                modifier = Modifier.padding(end = dimensionResource(id = R.dimen.default_padding))
            ) {
                FilledTonalButton(onClick = { onSaveTime(currentTime) }) {
                    TextWithIcon(
                        text = stringResource(id = R.string.stopwatch_to_mark),
                        icon = {
                            Icon(
                                imageVector = Icons.Rounded.Check,
                                contentDescription = null
                            )
                        }
                    )
                }
            }
            Button(onClick = {
                val playPauseFunction =
                    {
                        if (isRunning) StopwatchService.pause() else {
                            if (currentTime == 0L) context.startService(stopwatchService)
                            StopwatchService.start()
                        }
                    }
                notificationPermissionState?.let {
                    if (!it.status.isGranted) it.launchPermissionRequest() else playPauseFunction()
                } ?: playPauseFunction()
            }) {
                Crossfade(
                    targetState = isRunning,
                    animationSpec = tween(200),
                    label = "Play/Pause Button Crossfade"
                ) {
                    if (it) TextWithIcon(
                        text = stringResource(id = R.string.common_pause_text),
                        icon = {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_pause),
                                contentDescription = null
                            )
                        }) else TextWithIcon(
                        text = stringResource(id = R.string.common_play_text),
                        icon = {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_play),
                                contentDescription = null
                            )
                        },
                    )
                }
            }
            AnimatedVisibility(visible = !isRunning) {
                OutlinedButton(
                    onClick = {
                        StopwatchService.reset()
                        context.stopService(stopwatchService)
                        onReset()
                    },
                    modifier = Modifier.padding(start = dimensionResource(id = R.dimen.default_padding))
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Refresh,
                        contentDescription = stringResource(id = R.string.common_reset)
                    )
                }
            }
        }
        AnimatedVisibility(visible = savedTimesList.isNotEmpty()) {
            SavedTimesList(
                list = savedTimesList,
                modifier = Modifier.padding(dimensionResource(id = R.dimen.large_padding))
            )
        }
    }
}

@Composable
fun StopwatchFace(timeInMillis: Long, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val infiniteTransition = rememberInfiniteTransition(label = "Stopwatch Face Gradient Effect")
    val rotateAnimation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 5000,
                easing = LinearEasing
            )
        ),
        label = "Stopwatch Face Gradient Effect"
    )
    val size = 200.dp
    val primaryColor = MaterialTheme.colorScheme.primary
    val secondaryColor = MaterialTheme.colorScheme.secondary
    val circleColor = Brush.sweepGradient(listOf(primaryColor, secondaryColor, primaryColor))
    Box(contentAlignment = Alignment.Center, modifier = modifier.size(size)) {
        Canvas(
            modifier = Modifier
                .size(size)
                .rotate(degrees = rotateAnimation)
        ) {
            drawCircle(
                brush = circleColor,
                radius = size.toPx() / 2,
                style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
            )
        }
        Text(
            text = formatTime(context, timeInMillis = timeInMillis),
            style = MaterialTheme.typography.displaySmall
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SavedTimesList(list: List<Long>, modifier: Modifier = Modifier) {
    Card(
        shape = MaterialTheme.shapes.extraLarge,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize()
    ) {
        LazyColumn(
            modifier = Modifier
                .padding(dimensionResource(id = R.dimen.large_padding))
                .heightIn(max = dimensionResource(id = R.dimen.default_max_list_height))
        ) {
            items(list) {
                val position = list.indexOf(it)
                val time = if (position == 0) it else (it - list[position - 1])
                SavedTimesItem(
                    position = position + 1,
                    time = time,
                    totalTime = it,
                    modifier = Modifier.animateItemPlacement()
                )
            }
        }
    }
}

@Composable
fun SavedTimesItem(position: Int, time: Long, totalTime: Long, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = "${position}º")
        Text(text = formatTime(context, timeInMillis = time))
        Text(text = formatTime(context, timeInMillis = totalTime))
    }
}

@Preview(uiMode = UI_MODE_NIGHT_YES)
@Preview
@Composable
fun SavedTimesListPreview() {
    GymLogTheme {
        Surface {
            val list = listOf<Long>(1455, 10987, 24678)
            SavedTimesList(
                list = list,
                modifier = Modifier.padding(dimensionResource(id = R.dimen.large_padding))
            )
        }
    }
}

@Preview
@Composable
fun SavedTimesItemPreview() {
    GymLogTheme {
        SavedTimesItem(position = 1, time = 3078, totalTime = 23856)
    }
}

@Preview(uiMode = UI_MODE_NIGHT_YES)
@Preview
@Composable
fun AppStopwatchPreview() {
    GymLogTheme {
        Surface {
            val savedTimes: MutableList<Long> = remember { mutableStateListOf() }
            AppStopwatch(
                savedTimesList = savedTimes,
                onSaveTime = { savedTimes.add(it) },
                onReset = { savedTimes.clear() })
        }
    }
}