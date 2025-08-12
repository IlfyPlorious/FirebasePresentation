package com.example.firebasedemo.domain.repository

import android.graphics.Bitmap
import android.graphics.Color
import androidx.core.graphics.get
import androidx.core.graphics.scale
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
    suspend fun classifyImage(bitmap: Bitmap): Result<Int>
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

    override suspend fun classifyImage(bitmap: Bitmap): Result<Int> {
        if (interpreter == null) {
            initializeModel().onFailure { return Result.failure(it) }
        }

        val processedInput = preprocess(bitmap)
        val bufferSize = 48 * java.lang.Float.SIZE / java.lang.Byte.SIZE
        val modelOutput = ByteBuffer.allocateDirect(bufferSize).order(ByteOrder.nativeOrder())

        interpreter?.run(processedInput, modelOutput)

        return Result.success(modelOutput.toClassPrediction())
    }

    private fun preprocess(bitmap: Bitmap): ByteBuffer {
        val input = ByteBuffer.allocateDirect(4 * 3 * 224 * 224).order(ByteOrder.nativeOrder())
        val scaledBitmap = bitmap.scale(224, 224, false)
        for (y in 0 until 224) {
            for (x in 0 until 224) {
                val px = scaledBitmap[x, y]

                // Get channel values from the pixel value.
                val r = Color.red(px)
                val g = Color.green(px)
                val b = Color.blue(px)

                // Normalize channel values to [-1.0, 1.0]. This requirement depends on the model.
                // For example, some models might require values to be normalized to the range
                // [0.0, 1.0] instead.
                val rf = (r - 127) / 255f
                val gf = (g - 127) / 255f
                val bf = (b - 127) / 255f

                input.putFloat(rf)
                input.putFloat(gf)
                input.putFloat(bf)
            }
        }

        return input.apply { rewind() }
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