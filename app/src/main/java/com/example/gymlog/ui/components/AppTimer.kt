package com.example.gymlog.ui.components

import android.content.Intent
import android.content.res.Configuration.UI_MODE_NIGHT_YES
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
import com.example.gymlog.services.TimerService
import com.example.gymlog.ui.theme.GymLogTheme
import java.util.Calendar


@Composable
fun AppTimer(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    Intent(context, TimerService::class.java).also { intent ->
        context.startService(intent)
    }
    val isRunning by TimerService.isRunning.collectAsStateWithLifecycle()
    val startTimeInMillis by TimerService.startTimeInMillis.collectAsStateWithLifecycle()
    val currentTimeInMillis by TimerService.currentTimeInMillis.collectAsStateWithLifecycle()
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
                    if (currentTimeInMillis != 0f) {
                        if (isRunning) TimerService.pause() else TimerService.start(context = context)
                    }

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
            OutlinedButton(onClick = { TimerService.reset() }) {
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
                    onValueChange = { TimerService.setStartInMillis(it) },
                    valueRange = 0f..3600000f,
                    modifier = Modifier
                        .fillMaxWidth()

                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.default_padding))
                ) {
                    FilledTonalButton(onClick = { TimerService.decreaseTime(10000f) }) {
                        Text(text = "-10s")
                    }
                    FilledTonalButton(onClick = { TimerService.decreaseTime(5000f) }) {
                        Text(text = "-5s")
                    }
                    FilledTonalButton(onClick = { TimerService.increaseTime(5000f) }) {
                        Text(text = "+5s")
                    }
                    FilledTonalButton(onClick = { TimerService.increaseTime(10000f) }) {
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
            AppTimer(modifier = Modifier.padding(8.dp))
        }
    }
}