package com.example.firebasedemo.di

import com.example.firebasedemo.domain.repository.CustomClassifierRepository
import com.example.firebasedemo.domain.repository.CustomClassifierRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class RepositoryModule {

    @Provides
    fun provideClassifierRepositoryImpl(): CustomClassifierRepository = CustomClassifierRepositoryImpl()
}