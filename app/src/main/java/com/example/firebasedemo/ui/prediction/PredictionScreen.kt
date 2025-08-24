package com.example.firebasedemo.ui.prediction

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.firebasedemo.R
import com.example.firebasedemo.ui.theme.DarkBlue
import com.example.firebasedemo.ui.theme.DarkGray
import com.example.firebasedemo.ui.theme.Orange
import com.example.firebasedemo.ui.theme.Typography
import com.example.firebasedemo.ui.theme.VeryLightGray
import com.halilibo.richtext.markdown.Markdown
import com.halilibo.richtext.ui.BasicRichText

@OptIn(ExperimentalMaterial3Api::class, ExperimentalGlideComposeApi::class)
@Composable
fun PredictionScreen(
    navController: NavController,
    viewModel: PredictionViewModel = hiltViewModel<PredictionViewModel>()
) {
    val uiState = viewModel.predictionScreenState.collectAsState()
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = false,
    )

    val context = LocalContext.current

    if (uiState.value.uiElementsState.showDialog) {
        AlertDialog(
            containerColor = VeryLightGray,
            onDismissRequest = { viewModel.closeDialog() },
            title = {
                Text(text = "Information", style = Typography.titleLarge, color = DarkBlue)
            },
            text = {
                Text(
                    text = stringResource(R.string.info_dialog_desc),
                    style = Typography.bodyMedium,
                    color = DarkGray
                )
            },
            confirmButton = {
                Button(
                    modifier = Modifier
                        .padding(8.dp),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        contentColor = VeryLightGray,
                        containerColor = DarkBlue,
                    ),
                    onClick = {
                        viewModel.closeDialog()
                    }) {
                    Text(stringResource(R.string.close))
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBlue),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = VeryLightGray)
            }
            IconButton(onClick = { viewModel.showDialog() }) {
                Icon(Icons.Filled.Info, contentDescription = "Info", tint = VeryLightGray)
            }
        }

        Card(
            modifier = Modifier
                .weight(4f, fill = true)
                .padding(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = uiState.value.uiElementsState.brand?.color ?: VeryLightGray
            )
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(
                        id = uiState.value.uiElementsState.brand?.icon ?: R.drawable.brand_unknown
                    ),
                    contentDescription = "Hero Image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentScale = ContentScale.Inside
                )
            }
        }

        Text(
            text = "Wow! The car is a ${uiState.value.uiElementsState.brand?.name}!\nBrowse more models in the link below \uD83D\uDC47",
            fontSize = 18.sp,
            style = TextStyle(color = VeryLightGray, fontSize = 16.sp),
            modifier = Modifier
                .padding(16.dp)
                .weight(1f)
        )

        AskGeminiButton(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(), uiState.value.geminiState
        ) { viewModel.askGeminiAboutTheBrand() }

        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Card(
                modifier = Modifier
                    .size(width = 100.dp, height = 30.dp)
                    .clickable {
                        val intent = Intent(Intent.ACTION_VIEW).apply {
                            setData(Uri.parse("https://www.autovit.ro/autoturisme/${uiState.value.uiElementsState.brand?.name}"))
                        }

                        context.startActivity(intent)
                    },
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = VeryLightGray)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Image(
                        modifier = Modifier.padding(8.dp),
                        painter = painterResource(id = R.drawable.autovit),
                        contentDescription = "Autovit",
                        contentScale = ContentScale.FillWidth
                    )
                }
            }

            IconButton(onClick = {
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    setData(Uri.parse(uiState.value.uiElementsState.brand?.website))
                }

                context.startActivity(intent)
            }) {
                Image(
                    painter = painterResource(id = R.drawable.website),
                    contentDescription = "Navigate to website",
                    contentScale = ContentScale.FillHeight
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(top = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {

            Row(
                modifier = Modifier.weight(2f, fill = true),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    modifier = Modifier
                        .weight(1f)
                        .padding(8.dp),
                    onClick = { viewModel.toggleThumbsUp() }) {
                    Icon(
                        if (uiState.value.uiElementsState.thumbsUp == true) Icons.Filled.ThumbUp else Icons.Outlined.ThumbUp,
                        contentDescription = "Thumbs Up",
                        tint = VeryLightGray
                    )
                }
                IconButton(
                    modifier = Modifier
                        .weight(1f)
                        .padding(8.dp),
                    onClick = { viewModel.toggleThumbsDown() }) {
                    Icon(
                        if (uiState.value.uiElementsState.thumbsUp == false) Icons.Filled.ThumbUp else Icons.Outlined.ThumbUp,
                        contentDescription = "Thumbs Down",
                        tint = VeryLightGray,
                        modifier = Modifier.rotate(180f)
                    )
                }
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .weight(5f),
                    text = stringResource(id = R.string.thumbs_up_down_label),
                    style = TextStyle(color = VeryLightGray),
                    fontSize = 12.sp
                )
            }
        }
    }

    if (uiState.value.geminiState is PredictionViewModel.GeminiState.Ready) {
        ModalBottomSheet(
            modifier = Modifier.fillMaxHeight(),
            sheetState = sheetState,
            onDismissRequest = { viewModel.clearGeminiAnswer() }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                BasicRichText(
                    modifier = Modifier.padding(vertical = 8.dp),
                ) {
                    Markdown(
                        (uiState.value.geminiState as PredictionViewModel.GeminiState.Ready).text
                    )
                }
            }
        }
    }
}


@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun AskGeminiButton(
    modifier: Modifier,
    geminiState: PredictionViewModel.GeminiState,
    onClick: () -> Unit
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Card(
            modifier = Modifier.clickable { onClick() },
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(containerColor = VeryLightGray)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                if (geminiState !is PredictionViewModel.GeminiState.Thinking) {
                    Image(
                        painter = painterResource(
                            id = R.drawable.stars
                        ),
                        modifier = Modifier
                            .height(32.dp)
                            .padding(6.dp),
                        contentDescription = "Gemini icon",
                        contentScale = ContentScale.Fit,
                        colorFilter = ColorFilter.tint(Orange)
                    )
                } else {
                    Image(
                        painter = painterResource(
                            id = R.drawable.wheel_spin
                        ),
                        modifier = Modifier
                            .height(32.dp)
                            .padding(6.dp),
                        contentDescription = "Gemini icon",
                        contentScale = ContentScale.Fit,
                    )
//                    GlideImage(
//                        modifier = Modifier
//                            .height(32.dp)
//                            .padding(6.dp),
//                        model = R.drawable.wheel_spin,
//                        contentDescription = "Loading indicator"
//                    )
                }
                Text(
                    modifier = Modifier.padding(8.dp),
                    text = if (geminiState !is PredictionViewModel.GeminiState.Thinking) "Ask Gemini about the brand" else "Gemini is thinking",
                    style = Typography.titleLarge, color = DarkBlue
                )
            }
        }
    }
}

//@Preview
//@Composable
//fun PredictionScreenPreview() {
//    PredictionScreen(
//        navController = NavController(LocalContext.current),
//        viewModel = PredictionViewModel(object : GeminiQueryUseCase {
//            override suspend fun askGemini(query: GeminiQuery): Result<String> {
//                return Result.success("This is a prediction")
//            }
//        })
//    )
//}

class GeminiPreviewStates : PreviewParameterProvider<PredictionViewModel.GeminiState> {
    override val values: Sequence<PredictionViewModel.GeminiState> =
        sequenceOf(
            PredictionViewModel.GeminiState.Idle,
            PredictionViewModel.GeminiState.Ready("This is a prediction"),
            PredictionViewModel.GeminiState.Thinking
        )
}

@Preview
@Composable
fun AskGeminiButtonPreview(@PreviewParameter(GeminiPreviewStates::class) geminiPreviewStates: PredictionViewModel.GeminiState) {
    AskGeminiButton(
        modifier = Modifier
            .padding(12.dp)
            .fillMaxWidth(),
        geminiPreviewStates
    ) {}
}