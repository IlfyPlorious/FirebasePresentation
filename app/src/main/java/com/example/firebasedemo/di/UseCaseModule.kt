package com.example.firebasedemo.di

import com.example.firebasedemo.domain.GeminiQueryUseCase
import com.example.firebasedemo.domain.GeminiQueryUseCaseImpl
import com.example.firebasedemo.domain.ImageClassifierUseCase
import com.example.firebasedemo.domain.ImageClassifierUseCaseImpl
import com.example.firebasedemo.domain.ObjectDetectionUseCase
import com.example.firebasedemo.domain.ObjectDetectionUseCaseImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class UseCaseModule {

    @Binds
    abstract fun bindImageClassifierUseCase(impl: ImageClassifierUseCaseImpl): ImageClassifierUseCase

    @Binds
    abstract fun objectDetectionUseCase(impl: ObjectDetectionUseCaseImpl): ObjectDetectionUseCase

    @Binds
    abstract fun geminiInformationUseCase(impl: GeminiQueryUseCaseImpl): GeminiQueryUseCase
}