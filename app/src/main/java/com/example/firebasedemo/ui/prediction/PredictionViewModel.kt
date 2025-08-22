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

    private val _predictionScreenState: MutableStateFlow<PredictionScreenState> = MutableStateFlow(
        PredictionScreenState()
    )
    val predictionScreenState: StateFlow<PredictionScreenState> = _predictionScreenState

    fun showDialog() {
        _predictionScreenState.update {
            it.copy(
                uiElementsState = it.uiElementsState.copy(
                    showDialog = true
                )
            )
        }
    }

    fun closeDialog() {
        _predictionScreenState.update {
            it.copy(
                uiElementsState = it.uiElementsState.copy(
                    showDialog = false
                )
            )
        }
    }

    fun toggleThumbsUp() {
        if (predictionScreenState.value.uiElementsState.thumbsUp == true) {
            _predictionScreenState.update {
                it.copy(
                    uiElementsState = it.uiElementsState.copy(
                        thumbsUp = null
                    )
                )
            }
        } else {
            _predictionScreenState.update {
                it.copy(
                    uiElementsState = it.uiElementsState.copy(
                        thumbsUp = true
                    )
                )
            }
        }
    }

    fun toggleThumbsDown() {
        if (predictionScreenState.value.uiElementsState.thumbsUp == false) {
            _predictionScreenState.update {
                it.copy(
                    uiElementsState = it.uiElementsState.copy(
                        thumbsUp = null
                    )
                )
            }
        } else {
            _predictionScreenState.update {
                it.copy(
                    uiElementsState = it.uiElementsState.copy(
                        thumbsUp = false
                    )
                )
            }
        }
    }

    fun initialize(predictionId: Int) {
        _predictionScreenState.update {
            it.copy(
                uiElementsState = it.uiElementsState.copy(
                    brand = predictionId.mapPredictionToBrand()
                )
            )
        }
    }

    fun askGeminiAboutTheBrand() {
        if (predictionScreenState.value.geminiState is GeminiState.Thinking) return
        predictionScreenState.value.uiElementsState.brand?.let { brand ->
            viewModelScope.launch {
                _predictionScreenState.update {
                    it.copy(
                        geminiState = GeminiState.Thinking
                    )
                }

                val geminiResult =
                    geminiQueryUseCase.askGemini(GeminiQuery.CarBrandInfo(brand)).getOrElse {
                        _predictionScreenState.update {
                            it.copy(
                                geminiState = GeminiState.Idle
                            )
                        }
                        return@launch
                    }

                _predictionScreenState.update {
                    it.copy(
                        geminiState = GeminiState.Ready(geminiResult)
                    )
                }
            }
        }
    }

    fun clearGeminiAnswer() {
        _predictionScreenState.update {
            it.copy(
                geminiState = GeminiState.Idle
            )
        }
    }

    data class UIElementsState(
        val brand: Brand? = null,
        val thumbsUp: Boolean? = null,
        val showDialog: Boolean = false,
    )

    data class PredictionScreenState(
        val uiElementsState: UIElementsState = UIElementsState(),
        val geminiState: GeminiState = GeminiState.Idle
    )

    sealed class GeminiState {
        object Thinking : GeminiState()
        object Idle : GeminiState()
        class Ready(val text: String) : GeminiState()
    }
}