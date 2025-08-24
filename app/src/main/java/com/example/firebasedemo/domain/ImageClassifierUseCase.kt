package com.example.firebasedemo.domain

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import androidx.core.graphics.get
import androidx.core.graphics.scale
import com.example.firebasedemo.di.IoDispatcher
import com.example.firebasedemo.domain.repository.CustomClassifierRepository
import com.example.firebasedemo.util.Brand
import com.example.firebasedemo.util.loadBitmapFromUri
import com.example.firebasedemo.util.mapPredictionToBrand
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.nio.ByteBuffer
import java.nio.ByteOrder
import javax.inject.Inject

interface ImageClassifierUseCase {
    suspend fun classify(imageUri: Uri): Result<Brand>
}

class ImageClassifierUseCaseImpl @Inject constructor(
    @IoDispatcher
    private val ioDispatcher: CoroutineDispatcher,
    @ApplicationContext
    private val context: Context,
    private val classifierRepository: CustomClassifierRepository
) : ImageClassifierUseCase {
    override suspend fun classify(imageUri: Uri): Result<Brand> = withContext(ioDispatcher) {
        val bitmap = context.loadBitmapFromUri(imageUri)

        val processedInput = preprocess(bitmap)

        val brandResult =
            classifierRepository.classifyImage(processedInput)
                .getOrElse { return@withContext Result.failure(it) }.mapPredictionToBrand()

        return@withContext brandResult?.let { Result.success(it) }
            ?: Result.failure(Exception("Unknown brand"))
    }

    private fun preprocess(bitmap: Bitmap): ByteBuffer {
        val floatSize = java.lang.Float.SIZE / java.lang.Byte.SIZE
        val input =
            ByteBuffer.allocateDirect(floatSize * 3 * 224 * 224).order(ByteOrder.nativeOrder())
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
                val rf = r / 255f
                val gf = g / 255f
                val bf = b / 255f

                input.putFloat(rf)
                input.putFloat(gf)
                input.putFloat(bf)
            }
        }

        return input.apply { rewind() }
    }
}

