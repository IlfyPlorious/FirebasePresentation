package com.example.firebasedemo.domain.repository

import android.content.Context
import android.graphics.Bitmap
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import javax.inject.Inject

interface ClassifierRepository {
    suspend fun classifyImage(bitmap: Bitmap): Result<Int>
}

class ClassifierRepositoryImpl @Inject constructor(
    @ApplicationContext
    private val context: Context
): ClassifierRepository {
    override suspend fun classifyImage(bitmap: Bitmap): Result<Int> {
        //TODO implement classifier
    }
}