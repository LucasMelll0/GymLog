package com.example.gymlog

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import com.example.gymlog.navigation.AppNavHost
import com.example.gymlog.ui.theme.GymLogTheme
import com.google.accompanist.navigation.animation.rememberAnimatedNavController

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GymLogApp()
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun GymLogApp() {
    GymLogTheme {
        val navController = rememberAnimatedNavController()
        AppNavHost(navController = navController)
    }
}