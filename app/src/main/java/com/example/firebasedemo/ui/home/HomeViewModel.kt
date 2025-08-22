package com.example.firebasedemo.ui.home

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.firebasedemo.domain.ImageClassifierUseCase
import com.example.firebasedemo.domain.ObjectDetectionUseCase
import com.example.firebasedemo.util.Brand
import com.example.firebasedemo.util.applyBoundingBoxToImage
import com.example.firebasedemo.util.loadBitmapFromUri
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val imageClassifierUseCase: ImageClassifierUseCase,
    private val objectDetectorUseCase: ObjectDetectionUseCase,
) : ViewModel() {

    private val _uiState: MutableStateFlow<HomescreenUIState> =
        MutableStateFlow(HomescreenUIState())
    val uiState: StateFlow<HomescreenUIState> = _uiState

    fun turnOnPreview() {
        _uiState.update {
            it.copy(
                previewState = it.previewState.copy(
                    isStarted = true, showError = false, boundingBoxedImage = null
                )
            )
        }
    }

    fun turnOffPreview() {
        _uiState.update {
            it.copy(
                previewState = it.previewState.copy(
                    isStarted = false
                )
            )
        }
    }

    fun handleCapturedImageForCustomModel(capturedImageUri: Uri, callback: (Brand) -> Unit) {
        _uiState.update {
            it.copy(
                previewState = it.previewState.copy(
                    isLoading = true,
                    isStarted = false
                )
            )
        }

        viewModelScope.launch {
            val prediction = async {
                delay(500) // delay to simulate loading
                imageClassifierUseCase.classify(capturedImageUri)
            }.await().getOrNull()

            _uiState.update {
                it.copy(
                    previewState = it.previewState.copy(
                        isLoading = false
                    )
                )
            }

            prediction?.let {
                callback.invoke(it)
            } ?: showError()
        }
    }

    fun handleCapturedImageForPredefinedModel(capturedImageUri: Uri) {
        _uiState.update { it: HomescreenUIState ->
            it.copy(
                previewState = it.previewState.copy(
                    isLoading = true,
                    isStarted = false
                )
            )
        }

        viewModelScope.launch {
            val detectedObjects = async {
                delay(500) // delay to simulate loading
                objectDetectorUseCase.detectObjects(capturedImageUri)
            }.await().getOrElse {
                Log.d("HomeViewModel", "Error: ${it.message}")
                return@getOrElse null
            }

            _uiState.update {
                it.copy(
                    previewState = it.previewState.copy(
                        isLoading = false
                    )
                )
            }

            var capturedBitmap = context.loadBitmapFromUri(capturedImageUri)
            detectedObjects?.forEachIndexed { index, detectedObject ->
                Log.d(
                    "HomeViewModel",
                    "Detected object :: id ${index + 1} :: labels: ${
                        detectedObject.labels.joinToString("\n") { "${it.text} with confidence ${it.confidence}" }
                    }"
                )
                capturedBitmap =
                    capturedBitmap.applyBoundingBoxToImage(detectedObject.boundingBox, index + 1)
            } ?: showError()

            _uiState.update {
                it.copy(
                    previewState = it.previewState.copy(
                        boundingBoxedImage = capturedBitmap
                    )
                )
            }
        }
    }

    private fun showError() {
        _uiState.update {
            it.copy(
                previewState = it.previewState.copy(
                    showError = true
                )
            )
        }
    }

    fun closeDropdown() {
        _uiState.update {
            it.copy(
                dropdownState = it.dropdownState.copy(
                    isOpen = false
                )
            )
        }
    }

    fun expandDropdown() {
        _uiState.update {
            it.copy(
                dropdownState = it.dropdownState.copy(
                    isOpen = true
                )
            )
        }
    }

    fun setDropdownSelection(item: DropdownItem) {
        _uiState.update {
            it.copy(
                dropdownState = it.dropdownState.copy(
                    selectedOption = item
                )
            )
        }
    }

    data class DropdownState(
        val isOpen: Boolean = false,
        val selectedOption: DropdownItem = DropdownItem.CustomModel
    )

    data class PreviewState(
        val isLoading: Boolean = false,
        val isStarted: Boolean = false,
        val showError: Boolean = false,
        val boundingBoxedImage: Bitmap? = null
    )

    data class HomescreenUIState(
        val dropdownState: DropdownState = DropdownState(),
        val previewState: PreviewState = PreviewState()
    )

    enum class DropdownItem {
        CustomModel,
        PredefinedObjectDetector
    }
}