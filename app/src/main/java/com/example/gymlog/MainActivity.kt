package com.example.gymlog

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gymlog.data.Mock
import com.example.gymlog.ui.home.HomeScreen
import com.example.gymlog.ui.home.viewmodel.HomeViewModel
import com.example.gymlog.ui.theme.GymLogTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GymLogTheme {
                val viewModel: HomeViewModel = viewModel()
                viewModel.addAllTrainings(Mock.getTrainings(size = 5))
                HomeScreen(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    GymLogTheme {
        Greeting("Android")
    }
}