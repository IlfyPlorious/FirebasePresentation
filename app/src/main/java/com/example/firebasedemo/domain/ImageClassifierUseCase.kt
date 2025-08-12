package com.example.firebasedemo.domain

import android.content.Context
import android.net.Uri
import com.example.firebasedemo.di.IoDispatcher
import com.example.firebasedemo.domain.repository.CustomClassifierRepository
import com.example.firebasedemo.util.Brand
import com.example.firebasedemo.util.loadBitmapFromUri
import com.example.firebasedemo.util.mapPredictionToBrand
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
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
        val brandResult =
            classifierRepository.classifyImage(bitmap)
                .getOrElse { return@withContext Result.failure(it) }.mapPredictionToBrand()

        return@withContext brandResult?.let { Result.success(it) }
            ?: Result.failure(Exception("Unknown brand"))
    }
}

