package com.cercetaredocumentare.detectcar.ui.home

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.cercetaredocumentare.detectcar.R
import com.cercetaredocumentare.detectcar.network.repository.NNRepository
import com.cercetaredocumentare.detectcar.remote.model.Prediction
import com.cercetaredocumentare.detectcar.ui.home.camera.CameraPreview
import com.cercetaredocumentare.detectcar.ui.theme.DarkBlue
import com.cercetaredocumentare.detectcar.ui.theme.LightGray
import com.cercetaredocumentare.detectcar.ui.theme.Orange
import com.cercetaredocumentare.detectcar.ui.theme.Typography
import com.cercetaredocumentare.detectcar.ui.theme.VeryLightGray
import com.cercetaredocumentare.detectcar.util.loadBitmapFromAssets

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: HomeViewModel
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasCameraPermission = isGranted
    }

    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    if (hasCameraPermission) {
        // Your main content when permission is granted
        MainContent(navController, viewModel)
    } else {
        // Show a message explaining the need for permission
        PermissionDeniedContent { cameraPermissionLauncher.launch(Manifest.permission.CAMERA) }
    }
}

@Composable
fun PermissionDeniedContent(content: @Composable () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LightGray),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val context = LocalContext.current
        val imageBitmap = remember { loadBitmapFromAssets(context, "mechanic.png") }

        imageBitmap?.let {
            Image(
                painter = BitmapPainter(imageBitmap), contentDescription = "Decorative error image"
            )
        }

        Text(
            modifier = Modifier.padding(16.dp),
            text = stringResource(id = R.string.camera_permission_error),
            style = Typography.titleLarge,
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Center,
            color = Orange
        )

        Button(
            modifier = Modifier
                .width(160.dp)
                .padding(16.dp),
            onClick = {
                val intent = Intent(Settings.ACTION_SETTINGS)
                context.startActivity(intent)
            },
            shape = RoundedCornerShape(8.dp),
            contentPadding = PaddingValues(16.dp),
            colors = ButtonDefaults.buttonColors(
                contentColor = VeryLightGray,
                containerColor = DarkBlue
            )
        ) {
            Text(text = stringResource(id = R.string.settings))
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun MainContent(navController: NavController, viewModel: HomeViewModel) {
    val previewIsStarted = viewModel.previewIsStarted.collectAsState()
    val predictionIsLoading = viewModel.predictionIsLoading.collectAsState()
    val showError = viewModel.showError.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBlue),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            Text(
                text = stringResource(id = R.string.hero_title),
                style = Typography.titleLarge,
                color = VeryLightGray,
                textAlign = TextAlign.Center
            )
            Text(
                modifier = Modifier.padding(vertical = 12.dp),
                text = stringResource(id = R.string.hero_subtitle),
                style = Typography.bodyLarge,
                color = VeryLightGray,
            )
        }

        if (showError.value) {
            Icon(
                tint = Color.Red,
                modifier = Modifier.size(58.dp)
                    .weight(1f, fill = true),
                imageVector = Icons.Default.Close,
                contentDescription = "Close",
            )
        } else {
            if (predictionIsLoading.value) {
                GlideImage(
                    modifier = Modifier
                        .weight(1f, fill = true),
                    model = R.drawable.wheel_spin,
                    contentDescription = "Loading indicator"
                )
            } else {
                if (previewIsStarted.value) {
                    CameraPreview(
                        modifier = Modifier
                            .weight(1f, fill = true)
                    ) { capturedImageUri ->
                        viewModel.handleCapturedImage(capturedImageUri) { prediction ->
                            navController.navigate("prediction/${prediction.id}/${prediction.prediction}")
                        }
                    }
                } else {
                    Box(
                        modifier = Modifier.weight(1f, fill = true)
                    )
                }
            }
        }

        Button(
            shape = RectangleShape,
            contentPadding = PaddingValues(0.dp),
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(Color.Transparent)
                .padding(0.dp),
            onClick = {
                if (previewIsStarted.value) {
                    viewModel.turnOffPreview()
                } else {
                    viewModel.turnOnPreview()
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
        ) {
            Image(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(0.dp),
                painter = painterResource(id = if (previewIsStarted.value) R.drawable.car_hood_beams else R.drawable.car_hood),
                contentDescription = "Make prediction",
                contentScale = ContentScale.FillBounds
            )
        }
    }
}

@Preview
@Composable
fun MainContentPreview() {
    MainContent(
        navController = NavController(LocalContext.current),
        viewModel = HomeViewModel(object : NNRepository {
            override suspend fun sendImageForPrediction(imageUri: Uri): Prediction {
                //no-op
                return Prediction("", 0)
            }

            override suspend fun sendPredictionReview(
                imgId: String,
                prediction: Int,
                review: Boolean
            ) {
                // no-op
            }
        })
    )
}
