package com.devmello.gymlog

import com.devmello.gymlog.navigation.AppNavHost
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.devmello.gymlog.core.ui.MessageManager
import com.devmello.gymlog.core.ui.ScaffoldManager
import com.devmello.gymlog.ui.MainScreen
import com.devmello.gymlog.ui.theme.GymLogTheme
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import org.koin.androidx.compose.get

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GymLogTheme {
                GymLogApp()
            }
        }
    }
}



@OptIn(ExperimentalAnimationApi::class)
@Composable
fun GymLogApp() {
    val scaffoldManager = get<ScaffoldManager>()
    val messageManager = get<MessageManager>()
    GymLogTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            MainScreen(scaffoldManager = scaffoldManager, messageManager = messageManager)
        }

    }
}