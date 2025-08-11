package com.cercetaredocumentare.detectcar.di

import com.cercetaredocumentare.detectcar.domain.ImageClassifierUseCase
import com.cercetaredocumentare.detectcar.domain.ImageClassifierUseCaseImpl
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