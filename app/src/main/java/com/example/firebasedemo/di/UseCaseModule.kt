package com.example.firebasedemo.di

import com.example.firebasedemo.domain.ImageClassifierUseCase
import com.example.firebasedemo.domain.ImageClassifierUseCaseImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class UseCaseModule {

    @Binds
    abstract fun bindImageClassifierUseCase(impl: ImageClassifierUseCaseImpl): ImageClassifierUseCase
}