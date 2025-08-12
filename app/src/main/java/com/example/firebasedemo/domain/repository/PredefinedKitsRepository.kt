package com.example.firebasedemo.domain.repository

import android.graphics.Bitmap
import com.example.firebasedemo.di.IoDispatcher
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.objects.DetectedObject
import com.google.mlkit.vision.objects.ObjectDetection
import com.google.mlkit.vision.objects.ObjectDetector
import com.google.mlkit.vision.objects.defaults.ObjectDetectorOptions
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

interface PredefinedKitsRepository {
    suspend fun detectObjectsInBitmap(bitmap: Bitmap): Result<List<DetectedObject>>
}

class PredefinedKitsRepositoryImpl @Inject constructor(
    @IoDispatcher
    private val ioDispatcher: CoroutineDispatcher
) : PredefinedKitsRepository {
    private var objectDetector: ObjectDetector? = null

    private fun initDetector() {
//        // Live detection and tracking
//        val options = ObjectDetectorOptions.Builder()
//            .setDetectorMode(ObjectDetectorOptions.STREAM_MODE)
//            .enableClassification()  // Optional
//            .build()

        // Multiple object detection in static images
        val options = ObjectDetectorOptions.Builder()
            .setDetectorMode(ObjectDetectorOptions.SINGLE_IMAGE_MODE)
            .enableMultipleObjects()
            .enableClassification()  // Optional
            .build()

        objectDetector = ObjectDetection.getClient(options)
    }

    override suspend fun detectObjectsInBitmap(bitmap: Bitmap): Result<List<DetectedObject>> =
        withContext(ioDispatcher) {
            if (objectDetector == null) {
                initDetector()
            }

            val inputImage = InputImage.fromBitmap(bitmap, 0)

            val detectedObjectsList = mutableListOf<DetectedObject>()

            try {
                objectDetector?.process(inputImage)
                    ?.addOnSuccessListener { detectedObjects ->
                        detectedObjectsList.addAll(detectedObjects)
                    }
                    ?.addOnFailureListener { e ->
                        throw e
                    }?.await()
            } catch (e: Exception) {
                return@withContext Result.failure(e)
            }

            return@withContext Result.success(detectedObjectsList)
        }
}