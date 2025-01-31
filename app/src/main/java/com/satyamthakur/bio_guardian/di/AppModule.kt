package com.satyamthakur.bio_guardian.di

import com.google.firebase.storage.FirebaseStorage
import com.satyamthakur.bio_guardian.data.api.MistralImageRecognitionApi
import com.satyamthakur.bio_guardian.data.datasource.MistralData
import com.satyamthakur.bio_guardian.data.datasource.MistralDataImpl
import com.satyamthakur.bio_guardian.data.repository.MistralRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun providesRetrofitForMistralApi(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api.mistral.ai/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Singleton
    @Provides
    fun providesMistralAPIService(retrofit: Retrofit): MistralImageRecognitionApi {
        return retrofit.create(MistralImageRecognitionApi::class.java)
    }

    @Singleton
    @Provides
    fun provideMistralData(apiService: MistralImageRecognitionApi): MistralData {
        return MistralDataImpl(apiService)
    }

    @Singleton
    @Provides
    fun provideMistralRepository(mistralData: MistralData): MistralRepository {
        return MistralRepository(mistralData)
    }
}

object FirebaseRef {
    val storageRef = FirebaseStorage.getInstance().reference
}
