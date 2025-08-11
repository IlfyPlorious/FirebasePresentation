package com.cercetaredocumentare.detectcar.network.repository

import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import com.cercetaredocumentare.detectcar.di.IoDispatcher
import com.cercetaredocumentare.detectcar.network.NNApiClient
import com.cercetaredocumentare.detectcar.remote.model.Prediction
import com.cercetaredocumentare.detectcar.remote.model.request.PredictionRequest
import com.cercetaredocumentare.detectcar.remote.model.request.PredictionReviewRequest
import com.cercetaredocumentare.detectcar.util.rotate
import com.cercetaredocumentare.detectcar.util.toByteArray
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.io.File
import java.util.UUID
import javax.inject.Inject

interface NNRepository {
    suspend fun sendImageForPrediction(imageUri: Uri): Prediction?

    suspend fun sendPredictionReview(imgId: String, prediction: Int, review: Boolean)
}

class NNRepositoryImpl @Inject constructor(
    @IoDispatcher
    val ioDispatcher: CoroutineDispatcher,
    private val nnApiClient: NNApiClient
) : NNRepository {
    override suspend fun sendImageForPrediction(imageUri: Uri): Prediction? =
        withContext(ioDispatcher) {
            try {
                val imageFile = imageUri.path?.let { File(it) }
                val fileInputStream = imageFile?.inputStream()
                val bitmap = BitmapFactory.decodeStream(fileInputStream)
                val response = nnApiClient.sendPictureForPrediction(
                    PredictionRequest(
                        UUID.randomUUID().toString(), bitmap.rotate(90f).toByteArray()
                    )
                )

                if (response.isSuccessful) {
                    Log.d("Network success", "Success :: ${response.body()} :: ${response.code()}")
                    return@withContext response.body()
                } else {
                    Log.d("Network error", "Error :: ${response.body()} :: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("Network exception", e.message.toString())
            }
            return@withContext null
        }

    override suspend fun sendPredictionReview(imgId: String, prediction: Int, review: Boolean) {
        withContext(ioDispatcher) {
            try {
                val response =
                    nnApiClient.sendPredictionReview(
                        PredictionReviewRequest(
                            review,
                            prediction,
                            imgId
                        )
                    )
                if (response.isSuccessful) {
                    Log.d("Network success", "Success :: ${response.body()} :: ${response.code()}")
                } else {
                    Log.d("Network error", "Error :: ${response.body()} :: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("Network exception", e.message.toString())
            }
        }
    }
}