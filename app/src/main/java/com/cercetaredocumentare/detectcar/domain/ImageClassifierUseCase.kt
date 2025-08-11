package com.cercetaredocumentare.detectcar.domain

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import com.cercetaredocumentare.detectcar.di.IoDispatcher
import com.cercetaredocumentare.detectcar.domain.repository.ClassifierRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

interface ImageClassifierUseCase {
    suspend fun classify(imageUri: Uri): Result<Int>
}

class ImageClassifierUseCaseImpl @Inject constructor(
    @IoDispatcher
    private val ioDispatcher: CoroutineDispatcher,
    @ApplicationContext
    private val context: Context,
    private val classifierRepository: ClassifierRepository
) : ImageClassifierUseCase {
    override suspend fun classify(imageUri: Uri): Result<Int> = withContext(ioDispatcher) {
        val bitmap = loadBitmapFromUri(imageUri)
        val brandResult =
            classifierRepository.classifyImage(bitmap)
                .getOrElse { return@withContext Result.failure(it) }

        return@withContext Result.success(brandResult)
    }

    private fun loadBitmapFromUri(uri: Uri): Bitmap {
        val inputStream = context.contentResolver.openInputStream(uri)
            ?: throw IllegalArgumentException("Unable to open URI: $uri")
        return BitmapFactory.decodeStream(inputStream)
    }
}

