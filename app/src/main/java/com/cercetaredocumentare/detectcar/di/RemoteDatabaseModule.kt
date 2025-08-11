package com.cercetaredocumentare.detectcar.di

import com.cercetaredocumentare.detectcar.network.NNApiClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit

@Module
@InstallIn(SingletonComponent::class)
class RemoteDatabaseModule {

    @Provides
    fun provideCouchDBClient(retrofit: Retrofit): NNApiClient {
        return retrofit.create(NNApiClient::class.java)
    }
}