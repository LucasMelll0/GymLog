package com.example.gymlog.ui.components

import android.Manifest
import android.content.Intent
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.os.Build
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.gymlog.R
import com.example.gymlog.services.DropdownTimerService
import com.example.gymlog.ui.theme.GymLogTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import java.util.Calendar


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun AppDropdownTimer(modifier: Modifier = Modifier) {
    val notificationPermissionState = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        rememberPermissionState(permission = Manifest.permission.POST_NOTIFICATIONS)
    } else null
    val context = LocalContext.current
    Intent(context, DropdownTimerService::class.java).also { intent ->
        context.startService(intent)
    }
    val isRunning by DropdownTimerService.isRunning.collectAsStateWithLifecycle()
    val startTimeInMillis by DropdownTimerService.startTimeInMillis.collectAsStateWithLifecycle()
    val currentTimeInMillis by DropdownTimerService.currentTimeInMillis.collectAsStateWithLifecycle()
    val time = Calendar.getInstance().apply {
        timeInMillis = if (isRunning || currentTimeInMillis != startTimeInMillis) {
            currentTimeInMillis.toLong()
        } else {
            startTimeInMillis.toLong()
        }

    }
    val hour = time.get(Calendar.HOUR)
    val minutes = time.get(Calendar.MINUTE)
    val seconds = time.get(Calendar.SECOND)
    val progress =
        if (startTimeInMillis > 0) ((currentTimeInMillis * 100) / startTimeInMillis) else 100f
    Column(
        modifier = modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        val formattedMinutes = if (minutes < 10) "0$minutes" else minutes.toString()
        val formattedSeconds = if (seconds < 10) "0$seconds" else seconds.toString()
        CustomCircularProgressbar(
            modifier = Modifier.padding(dimensionResource(id = R.dimen.default_padding)),
            progress = progress,
            text = "${hour}h ${formattedMinutes}m ${formattedSeconds}s",
        )
        Spacer(modifier = Modifier.padding(dimensionResource(id = R.dimen.default_padding)))
        // Control buttons
        Row(
            modifier = Modifier.padding(vertical = dimensionResource(id = R.dimen.default_padding)),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Button(
                onClick = {
                    val playPauseFunction = {
                        if (currentTimeInMillis != 0f) {
                            if (isRunning) DropdownTimerService.pause() else DropdownTimerService.start(
                                context = context
                            )
                        }
                    }
                    notificationPermissionState?.let {
                        if (!it.status.isGranted) it.launchPermissionRequest() else playPauseFunction()
                    } ?: playPauseFunction()

                }, modifier = Modifier.padding(
                    horizontal = dimensionResource(
                        id = R.dimen.default_padding
                    )
                )
            ) {
                Crossfade(targetState = isRunning, animationSpec = tween(200)) {
                    if (it) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_pause),
                            contentDescription = null
                        )
                    } else {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_play),
                            contentDescription = null
                        )
                    }
                }
                Crossfade(targetState = isRunning, animationSpec = tween(200)) {
                    if (it) {
                        Text(text = stringResource(id = R.string.app_timer_pause_text))
                    } else {
                        Text(text = stringResource(id = R.string.app_timer_play_text))
                    }
                }
            }
            OutlinedButton(onClick = { DropdownTimerService.reset(context) }) {
                Icon(imageVector = Icons.Rounded.Refresh, contentDescription = "refresh")
            }

        }

        AnimatedVisibility(visible = (!isRunning && currentTimeInMillis == startTimeInMillis)) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(
                    dimensionResource(id = R.dimen.large_padding)
                )
            ) {
                Slider(
                    value = startTimeInMillis,
                    onValueChange = { DropdownTimerService.setStartInMillis(it) },
                    valueRange = 0f..3600000f,
                    modifier = Modifier
                        .fillMaxWidth()

                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.default_padding))
                ) {
                    FilledTonalButton(onClick = { DropdownTimerService.decreaseTime(10000f) }) {
                        Text(text = "-10s")
                    }
                    FilledTonalButton(onClick = { DropdownTimerService.decreaseTime(5000f) }) {
                        Text(text = "-5s")
                    }
                    FilledTonalButton(onClick = { DropdownTimerService.increaseTime(5000f) }) {
                        Text(text = "+5s")
                    }
                    FilledTonalButton(onClick = { DropdownTimerService.increaseTime(10000f) }) {
                        Text(text = "+10s")
                    }
                }
            }
        }
    }

}


@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES, name = "Night")
@Preview(showBackground = true)
@Composable
private fun AppTimerPreview() {
    GymLogTheme {
        Card() {
            AppDropdownTimer(modifier = Modifier.padding(8.dp))
        }
    }
}