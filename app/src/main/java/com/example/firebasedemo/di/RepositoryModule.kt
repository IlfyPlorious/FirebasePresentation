package com.example.firebasedemo.di

import com.example.firebasedemo.domain.repository.CustomClassifierRepository
import com.example.firebasedemo.domain.repository.CustomClassifierRepositoryImpl
import com.example.firebasedemo.domain.repository.PredefinedKitsRepository
import com.example.firebasedemo.domain.repository.PredefinedKitsRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher

@Module
@InstallIn(SingletonComponent::class)
class RepositoryModule {

    @Provides
    fun provideClassifierRepositoryImpl(@IoDispatcher ioDispatcher: CoroutineDispatcher): CustomClassifierRepository =
        CustomClassifierRepositoryImpl(ioDispatcher = ioDispatcher)

    @Provides
    fun providePredefinedKitsRepositoryImpl(@IoDispatcher ioDispatcher: CoroutineDispatcher): PredefinedKitsRepository =
        PredefinedKitsRepositoryImpl(ioDispatcher = ioDispatcher)

}