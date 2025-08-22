package com.example.firebasedemo.ui.home

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.firebasedemo.R
import com.example.firebasedemo.domain.ImageClassifierUseCase
import com.example.firebasedemo.domain.ObjectDetectionUseCase
import com.example.firebasedemo.ui.home.camera.CameraPreview
import com.example.firebasedemo.ui.theme.DarkBlue
import com.example.firebasedemo.ui.theme.LightGray
import com.example.firebasedemo.ui.theme.Orange
import com.example.firebasedemo.ui.theme.Typography
import com.example.firebasedemo.ui.theme.VeryLightGray
import com.example.firebasedemo.ui.theme.White
import com.example.firebasedemo.util.Brand
import com.example.firebasedemo.util.loadBitmapFromAssets
import com.google.mlkit.vision.objects.DetectedObject


@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel<HomeViewModel>()
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

@OptIn(ExperimentalGlideComposeApi::class, ExperimentalMaterial3Api::class)
@Composable
fun MainContent(navController: NavController, viewModel: HomeViewModel) {
    val uiState = viewModel.uiState.collectAsState()

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

        if (uiState.value.previewState.showError) {
            Icon(
                tint = Color.Red,
                modifier = Modifier
                    .size(58.dp)
                    .weight(1f, fill = true),
                imageVector = Icons.Default.Close,
                contentDescription = "Close",
            )
        } else {
            if (uiState.value.previewState.isLoading) {
                GlideImage(
                    modifier = Modifier
                        .weight(1f, fill = true),
                    model = R.drawable.wheel_spin,
                    contentDescription = "Loading indicator"
                )
            } else {
                if (uiState.value.previewState.isStarted) {
                    CameraPreview(
                        modifier = Modifier
                            .weight(1f, fill = true)
                    ) { capturedImageUri ->
                        when (uiState.value.dropdownState.selectedOption) {
                            HomeViewModel.DropdownItem.CustomModel -> viewModel.handleCapturedImageForCustomModel(
                                capturedImageUri
                            ) { prediction ->
                                navController.navigate("prediction/${prediction.id}")
                            }

                            HomeViewModel.DropdownItem.PredefinedObjectDetector -> viewModel.handleCapturedImageForPredefinedModel(
                                capturedImageUri
                            )
                        }
                    }
                } else if (uiState.value.previewState.boundingBoxedImage != null) {
                    GlideImage(
                        model = uiState.value.previewState.boundingBoxedImage,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .weight(1f, fill = true),
                        contentDescription = "Bounding boxed image"
                    )
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
                .fillMaxWidth()
                .weight(1f)
                .background(Color.Transparent)
                .padding(0.dp),
            onClick = {
                if (uiState.value.previewState.isStarted) {
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
                painter = painterResource(id = if (uiState.value.previewState.isStarted) R.drawable.car_hood_beams else R.drawable.car_hood),
                contentDescription = "Make prediction",
                contentScale = ContentScale.FillBounds
            )
        }

        ExposedDropdownMenuBox(
            expanded = uiState.value.dropdownState.isOpen,
            onExpandedChange = {
                if (uiState.value.dropdownState.isOpen)
                    viewModel.closeDropdown()
                else viewModel.expandDropdown()
            }) {
            OutlinedTextField(
                value = uiState.value.dropdownState.selectedOption.name,
                onValueChange = {},
                readOnly = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                    .clickable { viewModel.expandDropdown() },
                textStyle = TextStyle(color = White),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Orange,
                    unfocusedBorderColor = White,
                    cursorColor = White
                ),
                label = { Text("Recognition task", color = Orange) },
            )

            ExposedDropdownMenu(
                expanded = uiState.value.dropdownState.isOpen,
                onDismissRequest = { viewModel.closeDropdown() },
                modifier = Modifier
                    .zIndex(10f)
                    .padding(12.dp)
                    .fillMaxWidth()
            ) {
                HomeViewModel.DropdownItem.entries.forEachIndexed { index, item ->
                    DropdownMenuItem(
                        text = { Text(item.name, style = TextStyle(color = DarkBlue)) },
                        onClick = {
                            viewModel.setDropdownSelection(item)
                            viewModel.closeDropdown()
                        }
                    )

                    if (index < HomeViewModel.DropdownItem.entries.size - 1) Spacer(
                        Modifier
                            .height(1.dp)
                            .fillMaxWidth()
                            .background(DarkBlue)
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun MainContentPreview() {
    MainContent(
        navController = NavController(LocalContext.current),
        viewModel = HomeViewModel(LocalContext.current, object : ImageClassifierUseCase {
            override suspend fun classify(imageUri: Uri): Result<Brand> {
                return Result.success(Brand.AstonMartini)
            }
        }, object : ObjectDetectionUseCase {
            override suspend fun detectObjects(imageUri: Uri): Result<List<DetectedObject>> {
                return Result.success(emptyList())
            }
        })
    )
}
