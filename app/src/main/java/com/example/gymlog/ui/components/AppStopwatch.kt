package com.example.gymlog.ui.components

import android.content.Intent
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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

@Composable
fun AppStopwatch(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    Intent(context, StopwatchService::class.java).also {
        context.startService(it)
    }
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
            Button(onClick = {
                if (isRunning) StopwatchService.pause() else StopwatchService.start()
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
                    onClick = { StopwatchService.reset() },
                    modifier = Modifier.padding(start = dimensionResource(id = R.dimen.default_padding))
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Refresh,
                        contentDescription = stringResource(id = R.string.common_reset)
                    )
                }
            }
        }
    }
}

@Composable
fun StopwatchFace(timeInMillis: Long, modifier: Modifier = Modifier) {
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
    val minutes = timeInMillis / 1000 / 60
    val seconds = timeInMillis / 1000 % 60
    val millis = timeInMillis % 1000
    Box(contentAlignment = Alignment.Center, modifier = modifier.size(size)) {
        Canvas(modifier = Modifier
            .size(size)
            .rotate(degrees = rotateAnimation)) {
            drawCircle(
                brush = circleColor,
                radius = size.toPx() / 2,
                style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
            )
        }
        Text(
            text = stringResource(
                id = R.string.stopwatch_face_time_format,
                minutes,
                seconds,
                millis
            ), style = MaterialTheme.typography.displaySmall
        )
    }
}

@Preview(uiMode = UI_MODE_NIGHT_YES)
@Preview
@Composable
fun AppChronometerPreview() {
    GymLogTheme {
        Surface {
            AppStopwatch()
        }
    }
}