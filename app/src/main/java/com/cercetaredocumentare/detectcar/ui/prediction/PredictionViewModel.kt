package com.cercetaredocumentare.detectcar.ui.prediction

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cercetaredocumentare.detectcar.network.repository.NNRepository
import com.cercetaredocumentare.detectcar.util.Brand
import com.cercetaredocumentare.detectcar.util.mapPredictionToBrand
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = PredictionViewModelAssistedFactory::class)
class PredictionViewModel @AssistedInject constructor(
    @Assisted private val prediction: Int,
    @Assisted private val imgId: String,
    private val repository: NNRepository
) : ViewModel() {

    private val _predictionState: MutableStateFlow<Brand?> = MutableStateFlow(null)
    val predictionState: StateFlow<Brand?> = _predictionState

    private val _thumbsUpButtonsState: MutableStateFlow<Boolean?> = MutableStateFlow(null)
    val thumbsUpButtonsState: StateFlow<Boolean?> = _thumbsUpButtonsState

    private val _saveReviewLoading: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val saveReviewLoading: StateFlow<Boolean> = _saveReviewLoading

    init {
        _predictionState.update {
            prediction.mapPredictionToBrand()
        }
    }

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

    fun sendPredictionReview() {
        _saveReviewLoading.update {
            true
        }
        viewModelScope.launch {
            thumbsUpButtonsState.value?.let {
                repository.sendPredictionReview(
                    imgId = imgId,
                    prediction = prediction,
                    review = it
                )
            }
            _saveReviewLoading.update { false }
        }
    }
}

@AssistedFactory
interface PredictionViewModelAssistedFactory {
    fun create(predictionId: Int, imgId: String): PredictionViewModel
}