/* While this template provides a good starting point for using Wear Compose, you can always
 * take a look at https://github.com/android/wear-os-samples/tree/main/ComposeStarter to find the
 * most up to date changes to the libraries and their usages.
 */

package com.example.gymlogwearapp.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.animateColorAsState
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ButtonColors
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.Colors
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.ScalingLazyColumn
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.TimeText
import androidx.wear.compose.material.rememberScalingLazyListState
import androidx.wear.tooling.preview.devices.WearDevices
import com.example.gymlogwearapp.R
import com.example.gymlogwearapp.presentation.theme.GymLogTheme
import kotlin.random.Random

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()

        super.onCreate(savedInstanceState)

        setTheme(android.R.style.Theme_DeviceDefault)

        setContent {
            WearApp("Android")
        }
    }
}

@Composable
fun WearApp(greetingName: String) {

    var colorState : Color by remember { mutableStateOf(randomizeColor()) }
    val buttonBackgroundColor by animateColorAsState(colorState)
    GymLogTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.background),
            contentAlignment = Alignment.Center
        ) {
            TimeText()
            SimpleButton(
                backgroundColor = buttonBackgroundColor,
                onClick =  {
                colorState = randomizeColor()
            })
        }
    }
}

private fun randomizeColor(): Color {
    return Color(
        Random.nextInt(256),
        Random.nextInt(256),
        Random.nextInt(256),
        alpha = 255
    )
}


@Preview(device = WearDevices.SMALL_ROUND, showSystemUi = true)
@Composable
fun DefaultPreview() {
    WearApp("Preview Android")
}

@Composable
fun CommonList(modifier: Modifier = Modifier) {
    val listState = rememberScalingLazyListState()

    ScalingLazyColumn {
        item { Text("First item") }
        item { Text("Second item") }
        item { Text("Third item") }
    }
}

@Composable
fun SimpleButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    backgroundColor: Color = Color.Blue
) {
    Button(
        modifier = modifier.fillMaxWidth(),
        onClick = onClick,
        colors = ButtonDefaults.primaryButtonColors(backgroundColor = backgroundColor)
    ) {
        Text("Mudar Cor")
    }
}