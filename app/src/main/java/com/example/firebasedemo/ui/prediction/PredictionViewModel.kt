package com.example.firebasedemo.ui.prediction

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.firebasedemo.domain.GeminiQueryUseCase
import com.example.firebasedemo.util.Brand
import com.example.firebasedemo.util.GeminiQuery
import com.example.firebasedemo.util.mapPredictionToBrand
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PredictionViewModel @Inject constructor(
    private val geminiQueryUseCase: GeminiQueryUseCase
) : ViewModel() {

    private val _predictionState: MutableStateFlow<Brand?> = MutableStateFlow(null)
    val predictionState: StateFlow<Brand?> = _predictionState

    private val _thumbsUpButtonsState: MutableStateFlow<Boolean?> = MutableStateFlow(null)
    val thumbsUpButtonsState: StateFlow<Boolean?> = _thumbsUpButtonsState

    private val _saveReviewLoading: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val saveReviewLoading: StateFlow<Boolean> = _saveReviewLoading

    private val _imageId: MutableStateFlow<String> = MutableStateFlow("")
    val imageId: StateFlow<String> = _imageId

    private val _geminiState: MutableStateFlow<GeminiState> = MutableStateFlow(GeminiState.Idle)
    val geminiState: StateFlow<GeminiState> = _geminiState

    fun toggleThumbsUp() {
        if (thumbsUpButtonsState.value == true) {
            _thumbsUpButtonsState.update { null }
        } else {
            _thumbsUpButtonsState.update { true }
        }
    }

    fun toggleThumbsDown() {
        if (thumbsUpButtonsState.value == false) {
            _thumbsUpButtonsState.update { null }
        } else {
            _thumbsUpButtonsState.update { false }
        }
    }

    fun initialize(predictionId: Int) {
        _predictionState.update {
            predictionId.mapPredictionToBrand()
        }
    }

    fun askGeminiAboutTheBrand() {
        if (geminiState.value is GeminiState.Thinking) return
        predictionState.value?.let { brand ->
            viewModelScope.launch {
                _geminiState.update { GeminiState.Thinking }

                val geminiResult =
                    geminiQueryUseCase.askGemini(GeminiQuery.CarBrandInfo(brand)).getOrElse {
                        _geminiState.update {
                            GeminiState.Idle
                        }
                        return@launch
                    }

                _geminiState.update {
                    GeminiState.Ready(geminiResult)
                }
            }
        }
    }

    fun clearGeminiAnswer() {
        _geminiState.update { GeminiState.Idle }
    }

    sealed class GeminiState {
        object Thinking : GeminiState()
        object Idle : GeminiState()
        class Ready(val text: String) : GeminiState()
    }
}