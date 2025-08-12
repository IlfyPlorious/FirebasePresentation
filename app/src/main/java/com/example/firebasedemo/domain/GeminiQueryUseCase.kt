package com.example.firebasedemo.domain

import com.example.firebasedemo.di.IoDispatcher
import com.example.firebasedemo.domain.repository.PredefinedKitsRepository
import com.example.firebasedemo.util.GeminiQuery
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

interface GeminiQueryUseCase {
    suspend fun askGemini(query: GeminiQuery): Result<String>
}

class GeminiQueryUseCaseImpl @Inject constructor(
    @IoDispatcher
    private val ioDispatcher: CoroutineDispatcher,
    private val predefinedKitsRepository: PredefinedKitsRepository
) : GeminiQueryUseCase {
    override suspend fun askGemini(query: GeminiQuery): Result<String> = withContext(ioDispatcher) {
        return@withContext predefinedKitsRepository.askGemini(query.getQuery())
    }
}