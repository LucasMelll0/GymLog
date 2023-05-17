package com.example.gymlog

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.example.gymlog.navigation.AppNavHost
import com.example.gymlog.ui.theme.GymLogTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GymLogApp()
        }
    }
}

@Composable
fun GymLogApp() {
    GymLogTheme {
        val navController = rememberNavController()
        AppNavHost(navController = navController)
    }
}