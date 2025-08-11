package com.cercetaredocumentare.detectcar.network

import com.cercetaredocumentare.detectcar.remote.model.Prediction
import com.cercetaredocumentare.detectcar.remote.model.request.PredictionRequest
import com.cercetaredocumentare.detectcar.remote.model.request.PredictionReviewRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST


interface NNApiClient {
    @POST("prediction/predict")
    suspend fun sendPictureForPrediction(@Body predictionRequest: PredictionRequest): Response<Prediction?>

    @POST("/prediction/review")
    suspend fun sendPredictionReview(@Body predictionReviewRequest: PredictionReviewRequest): Response<Any>
}