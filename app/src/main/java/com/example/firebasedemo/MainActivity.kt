package com.example.firebasedemo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.firebasedemo.ui.home.HomeScreen
import com.example.firebasedemo.ui.prediction.PredictionScreen
import com.example.firebasedemo.ui.prediction.PredictionViewModel
import com.example.firebasedemo.ui.theme.DarkBlue
import com.example.firebasedemo.ui.theme.DetectcarTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(DarkBlue.toArgb()),
            navigationBarStyle = SystemBarStyle.dark(DarkBlue.toArgb())
        )

        setContent {
            DetectcarTheme {
                Scaffold(modifier = Modifier
                    .fillMaxSize()
                    .background(DarkBlue)) { innerPadding ->
                    val navController = rememberNavController()
                    NavHost(
                        navController = navController,
                        startDestination = "home",
                        modifier = Modifier
                            .padding(innerPadding)
                            .background(DarkBlue)
                    ) {
                        composable("home") {
                            HomeScreen(Modifier.fillMaxSize(), navController)
                        }
                        composable(
                            "prediction/{predictionId}",
                            arguments = listOf(
                                navArgument("predictionId") { type = NavType.IntType },
                            )
                        ) { backStackEntry ->
                            val predictionId =
                                backStackEntry.arguments?.getInt("predictionId") ?: -1
                            val viewModel = hiltViewModel<PredictionViewModel>()
                            viewModel.initialize(predictionId)
                            PredictionScreen(
                                navController = navController,
                                viewModel = viewModel
                            )
                        }
                    }
                }
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
    DetectcarTheme {
        Greeting("Android")
    }
}