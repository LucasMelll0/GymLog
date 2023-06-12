package com.example.gymlog

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
import com.example.gymlog.navigation.AppNavHost
import com.example.gymlog.ui.bmi.BmiHistoricScreen
import com.example.gymlog.ui.theme.GymLogTheme
import com.google.accompanist.navigation.animation.rememberAnimatedNavController

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GymLogTheme {
                BmiHistoricScreen(onNavIconClick = { /*TODO*/ }, onError = { /*TODO*/ })
            }
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun GymLogApp() {
    GymLogTheme {
        Box(modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)) {
            val navController = rememberAnimatedNavController()
            AppNavHost(navController = navController)
        }

    }
}