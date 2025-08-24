package com.example.firebasedemo.domain.repository

import com.example.firebasedemo.di.IoDispatcher
import com.google.firebase.ml.modeldownloader.CustomModel
import com.google.firebase.ml.modeldownloader.CustomModelDownloadConditions
import com.google.firebase.ml.modeldownloader.DownloadType
import com.google.firebase.ml.modeldownloader.FirebaseModelDownloader
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import org.tensorflow.lite.Interpreter
import java.nio.ByteBuffer
import java.nio.ByteOrder
import javax.inject.Inject

interface CustomClassifierRepository {
    suspend fun initializeModel(): Result<Unit>
    suspend fun classifyImage(imageByteBuffer: ByteBuffer): Result<Int>
}

class CustomClassifierRepositoryImpl @Inject constructor(
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : CustomClassifierRepository {
    private var interpreter: Interpreter? = null

    override suspend fun initializeModel(): Result<Unit> = withContext(ioDispatcher) {
        try {
            // Download the custom model from Firebase
            val conditions = CustomModelDownloadConditions.Builder()
                .requireWifi()  // Also possible: .requireCharging() and .requireDeviceIdle()
                .build()

            FirebaseModelDownloader.getInstance()
                .getModel(
                    "car-detector", DownloadType.LOCAL_MODEL_UPDATE_IN_BACKGROUND,
                    conditions
                )
                .addOnSuccessListener { model: CustomModel? ->
                    // Download complete. Depending on your app, you could enable the ML
                    // feature, or switch from the local model to the remote model, etc.

                    // The CustomModel object contains the local path of the model file,
                    // which you can use to instantiate a TensorFlow Lite interpreter.
                    val modelFile = model?.file
                    if (modelFile != null) {
                        interpreter = Interpreter(modelFile)
                    }
                }.await()

            return@withContext Result.success(Unit)
        } catch (e: Exception) {
            return@withContext Result.failure(e)
        }
    }

    override suspend fun classifyImage(imageByteBuffer: ByteBuffer): Result<Int> {
        if (interpreter == null) {
            initializeModel().onFailure { return Result.failure(it) }
        }

        val bufferSize = 48 * java.lang.Float.SIZE / java.lang.Byte.SIZE
        val modelOutput = ByteBuffer.allocateDirect(bufferSize).order(ByteOrder.nativeOrder())

        interpreter?.run(imageByteBuffer, modelOutput)

        return Result.success(modelOutput.toClassPrediction())
    }


    private fun ByteBuffer.toClassPrediction(): Int {
        rewind()
        val probabilities = asFloatBuffer()
        var max = probabilities.get(0)
        var maxIndex = 0
        for (i in 1 until probabilities.capacity()) {
            if (probabilities.get(i) > max) {
                max = probabilities.get(i)
                maxIndex = i
            }
        }

        return maxIndex + 1
    }
}