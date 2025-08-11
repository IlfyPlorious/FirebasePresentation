package com.example.firebasedemo.di

import android.content.Context
import com.example.firebasedemo.domain.repository.ClassifierRepository
import com.example.firebasedemo.domain.repository.ClassifierRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class RepositoryModule {

    @Provides
    fun provideClassifierRepositoryImpl(
        @ApplicationContext context: Context
    ): ClassifierRepository = ClassifierRepositoryImpl(context)
}