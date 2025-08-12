package com.example.firebasedemo.ui.home

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.compose.material3.Tab
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
    private val _isDropdownOpened: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isDropdownOpened: StateFlow<Boolean> = _isDropdownOpened

    private val _dropdownSelection: MutableStateFlow<DropdownItem> =
        MutableStateFlow(DropdownItem.CustomModel)
    val dropdownSelection: StateFlow<DropdownItem> = _dropdownSelection

    private val _boundingBoxedImage: MutableStateFlow<Bitmap?> = MutableStateFlow(null)
    val boundingBoxedImage: StateFlow<Bitmap?> = _boundingBoxedImage

    private val _imageUri: MutableStateFlow<Uri?> = MutableStateFlow(null)
    val imageUri: StateFlow<Uri?> = _imageUri

    private val _previewIsStarted: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val previewIsStarted: StateFlow<Boolean> = _previewIsStarted

    private val _predictionIsLoading: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val predictionIsLoading: StateFlow<Boolean> = _predictionIsLoading

    private val _showError: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val showError: StateFlow<Boolean> = _showError

    fun turnOnPreview() {
        _previewIsStarted.update {
            true
        }
        _showError.update {
            false
        }
        _boundingBoxedImage.update { null }
    }

    fun turnOffPreview() {
        _previewIsStarted.update {
            false
        }
    }

    fun handleCapturedImageForCustomModel(capturedImageUri: Uri, callback: (Brand) -> Unit) {
        _predictionIsLoading.update {
            true
        }
        _previewIsStarted.update {
            false
        }
        viewModelScope.launch {
            val prediction = async {
                delay(500) // delay to simulate loading
                imageClassifierUseCase.classify(capturedImageUri)
            }.await().getOrNull()

            _predictionIsLoading.update {
                false
            }

            prediction?.let {
                callback.invoke(it)
            } ?: showError()
        }
    }

    fun handleCapturedImageForPredefinedModel(capturedImageUri: Uri) {
        _predictionIsLoading.update {
            true
        }
        _previewIsStarted.update {
            false
        }
        viewModelScope.launch {
            val detectedObjects = async {
                delay(500) // delay to simulate loading
                objectDetectorUseCase.detectObjects(capturedImageUri)
            }.await().getOrElse {
                Log.d("HomeViewModel", "Error: ${it.message}")
                return@getOrElse null
            }

            _predictionIsLoading.update {
                false
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

            _boundingBoxedImage.update {
                capturedBitmap
            }
        }
    }

    private fun showError() {
        _showError.update {
            true
        }
    }

    fun closeDropdown() {
        _isDropdownOpened.update { false }
    }

    fun expandDropdown() {
        _isDropdownOpened.update { true }
    }

    fun setDropdownSelection(item: DropdownItem) {
        _dropdownSelection.update { item }
    }

    enum class DropdownItem {
        CustomModel,
        PredefinedObjectDetector
    }
}