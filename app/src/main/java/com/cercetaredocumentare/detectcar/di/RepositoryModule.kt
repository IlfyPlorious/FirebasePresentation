package com.cercetaredocumentare.detectcar.di

import com.cercetaredocumentare.detectcar.domain.repository.ClassifierRepository
import com.cercetaredocumentare.detectcar.domain.repository.ClassifierRepositoryImpl
import com.cercetaredocumentare.detectcar.network.repository.NNRepository
import com.cercetaredocumentare.detectcar.network.repository.NNRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent

@Module
@InstallIn(ActivityRetainedComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun provideEventsRepositoryImpl(impl: NNRepositoryImpl): NNRepository

    @Binds
    abstract fun provideClassifierRepositoryImpl(impl: ClassifierRepositoryImpl): ClassifierRepository
}