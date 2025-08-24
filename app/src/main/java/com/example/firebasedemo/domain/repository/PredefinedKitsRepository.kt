package com.example.firebasedemo.domain.repository

import com.example.firebasedemo.di.IoDispatcher
import com.google.firebase.Firebase
import com.google.firebase.ai.GenerativeModel
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.GenerativeBackend
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
    suspend fun detectObjectsInBitmap(inputImage: InputImage): Result<List<DetectedObject>>
    suspend fun askGemini(query: String): Result<String>
}

class PredefinedKitsRepositoryImpl @Inject constructor(
    @IoDispatcher
    private val ioDispatcher: CoroutineDispatcher
) : PredefinedKitsRepository {
    private var objectDetector: ObjectDetector? = null
    private var geminiModel: GenerativeModel? = null

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

    private fun initGemini() {
        geminiModel = Firebase.ai(backend = GenerativeBackend.googleAI())
            .generativeModel("gemini-2.5-flash")
    }

    override suspend fun detectObjectsInBitmap(inputImage: InputImage): Result<List<DetectedObject>> =
        withContext(ioDispatcher) {
            if (objectDetector == null) {
                initDetector()
            }

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

    override suspend fun askGemini(query: String): Result<String> {
        if (geminiModel == null) {
            initGemini()
        }
        val response = try {
            geminiModel?.generateContent(query)
        } catch (e: Exception) {
            return Result.failure(e)
        }

        return response?.text?.let { Result.success(it) }
            ?: Result.failure(Exception("Error! Query result is empty."))
    }
}