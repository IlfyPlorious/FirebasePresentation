package com.cercetaredocumentare.detectcar

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.cercetaredocumentare.detectcar.ui.home.HomeScreen
import com.cercetaredocumentare.detectcar.ui.prediction.PredictionScreen
import com.cercetaredocumentare.detectcar.ui.prediction.PredictionViewModelAssistedFactory
import com.cercetaredocumentare.detectcar.ui.theme.DarkBlue
import com.cercetaredocumentare.detectcar.ui.theme.DetectcarTheme
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
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val navController = rememberNavController()
                    NavHost(
                        navController = navController,
                        startDestination = "home",
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable("home") {
                            HomeScreen(Modifier.fillMaxSize(), navController, hiltViewModel())
                        }
                        composable(
                            "prediction/{imgid}/{predictionId}",
                            arguments = listOf(
                                navArgument("predictionId") { type = NavType.IntType },
                                navArgument("imgid") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val predictionId =
                                backStackEntry.arguments?.getInt("predictionId") ?: -1
                            val imgid = backStackEntry.arguments?.getString("imgid") ?: ""
                            PredictionScreen(
                                navController = navController,
                                viewModel = hiltViewModel(creationCallback = { factory: PredictionViewModelAssistedFactory ->
                                    factory.create(
                                        predictionId,
                                        imgid
                                    )
                                })
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