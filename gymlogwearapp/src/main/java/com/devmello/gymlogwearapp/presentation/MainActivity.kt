/* While this template provides a good starting point for using Wear Compose, you can always
 * take a look at https://github.com/android/wear-os-samples/tree/main/ComposeStarter to find the
 * most up to date changes to the libraries and their usages.
 */

package com.devmello.gymlogwearapp.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.health.services.client.data.DataTypeAvailability
import androidx.health.services.client.data.SampleDataPoint
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.TimeText
import com.devmello.gymlogwearapp.domain.viewmodel.MainViewModel
import com.devmello.gymlogwearapp.presentation.theme.GymLogTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import org.koin.androidx.compose.koinViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()

        super.onCreate(savedInstanceState)

        setTheme(android.R.style.Theme_DeviceDefault)

        setContent {
            WearApp()
        }
    }
}

const val PERMISSION = android.Manifest.permission.BODY_SENSORS
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun WearApp(
    viewmodel: MainViewModel = koinViewModel<MainViewModel>()
) {

    val enabled by viewmodel.enabled.collectAsState()
    val hr by viewmodel.hr.collectAsState()
    val permissionState = rememberPermissionState(
        permission = PERMISSION,
        onPermissionResult = { granted ->
            if (granted) viewmodel.toggleEnabled()
        }
    )

    GymLogTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.background),
            contentAlignment = Alignment.Center
        ) {
            TimeText()
            Column {
                Text(text = "${hr.toInt()}", style = MaterialTheme.typography.title2)
                SimpleButton(
                    onClick = {
                        if(permissionState.status.isGranted) {
                            viewmodel.toggleEnabled()
                        } else {
                            permissionState.launchPermissionRequest()
                        }
                    }, text = if(enabled) "Stop" else "Measure HR")
            }

        }
    }
}



sealed class MeasureMessage {
    class MeasureAvailability(val availability: DataTypeAvailability) : MeasureMessage()
    class MeasureData(val data: List<SampleDataPoint<Double>>) : MeasureMessage()
}

@Composable
fun SimpleButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    text: String
) {
    Button(
        modifier = modifier.fillMaxWidth(),
        onClick = onClick,
        colors = ButtonDefaults.primaryButtonColors()
    ) {
        Text(text)
    }
}