package com.example.firebasedemo.domain

import android.content.Context
import android.net.Uri
import com.example.firebasedemo.di.IoDispatcher
import com.example.firebasedemo.domain.repository.PredefinedKitsRepository
import com.example.firebasedemo.util.loadBitmapFromUri
import com.google.mlkit.vision.objects.DetectedObject
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

interface ObjectDetectionUseCase {
    suspend fun detectObjects(imageUri: Uri): Result<List<DetectedObject>>
}

class ObjectDetectionUseCaseImpl @Inject constructor(
    @IoDispatcher
    private val ioDispatcher: CoroutineDispatcher,
    @ApplicationContext
    private val context: Context,
    private val predefinedKitsRepository: PredefinedKitsRepository
) : ObjectDetectionUseCase {
    override suspend fun detectObjects(imageUri: Uri): Result<List<DetectedObject>> =
        withContext(ioDispatcher) {
            val bitmap = context.loadBitmapFromUri(imageUri)
            val detectedObjects =
                predefinedKitsRepository.detectObjectsInBitmap(bitmap)
                    .getOrElse { return@withContext Result.failure(it) }

            return@withContext detectedObjects.takeIf { it.isNotEmpty() }
                ?.let { Result.success(it) }
                ?: Result.failure(Exception("No object detected"))
        }
}