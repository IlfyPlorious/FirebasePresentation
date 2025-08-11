package com.example.firebasedemo.ui.home

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.firebasedemo.domain.ImageClassifierUseCase
import com.example.firebasedemo.util.Brand
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val imageClassifierUseCase: ImageClassifierUseCase
) : ViewModel() {
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
    }

    fun turnOffPreview() {
        _previewIsStarted.update {
            false
        }
    }

    fun handleCapturedImage(capturedImageUri: Uri, callback: (Brand) -> Unit) {
        _predictionIsLoading.update {
            true
        }
        _previewIsStarted.update {
            false
        }
        viewModelScope.launch {
            val prediction = async {
                delay(2000) // delay to simulate loading
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

    private fun showError() {
        _showError.update {
            true
        }
    }
}