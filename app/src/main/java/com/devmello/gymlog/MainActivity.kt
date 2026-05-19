package com.devmello.gymlog

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import com.devmello.gymlog.core.navigation.NavigationManager
import com.devmello.gymlog.core.ui.MessageManager
import com.devmello.gymlog.core.ui.ScaffoldManager
import com.devmello.gymlog.ui.MainScreen
import com.devmello.gymlog.ui.theme.GymLogTheme
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
    val navigationManager = get<NavigationManager>()
    GymLogTheme {
        MainScreen(scaffoldManager = scaffoldManager, messageManager = messageManager, navigationManager = navigationManager)
    }
}