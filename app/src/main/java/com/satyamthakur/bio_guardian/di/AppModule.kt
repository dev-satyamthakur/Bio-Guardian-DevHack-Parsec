package com.satyamthakur.bio_guardian.di

import com.google.firebase.storage.FirebaseStorage
import com.satyamthakur.bio_guardian.data.api.ImagePredictionApiService
import com.satyamthakur.bio_guardian.data.api.NinjaApiService
import com.satyamthakur.bio_guardian.data.datasource.AnimalDataInfo
import com.satyamthakur.bio_guardian.data.datasource.AnimalDataInfoImpl
import com.satyamthakur.bio_guardian.data.datasource.AnimalPredictionData
import com.satyamthakur.bio_guardian.data.datasource.AnimalPredictionDataImpl
import com.satyamthakur.bio_guardian.ui.respository.AnimalRepository
import com.satyamthakur.bio_guardian.ui.respository.PredictionRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    @Singleton
    fun providesRetrofitForAnimalInfo(): Retrofit {
        return Retrofit.Builder().baseUrl("https://api.api-ninjas.com/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }


    @Singleton
    @Provides
    fun providesApiService(retrofit: Retrofit = providesRetrofitForAnimalInfo()): NinjaApiService {
        return retrofit.create(NinjaApiService::class.java)
    }

    @Singleton
    @Provides
    fun providesApiServiceForAninmalPrediction()
    : ImagePredictionApiService {
        val retrofit = Retrofit.Builder().baseUrl("https://detect.roboflow.com/bird-v2/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit.create(ImagePredictionApiService::class.java)
    }

    @Singleton
    @Provides
    fun providesNewsDataSource(apiService: NinjaApiService): AnimalDataInfo {
        return AnimalDataInfoImpl(apiService)
    }

    @Singleton
    @Provides
    fun providesAnimalPredictionDataSource(apiService: ImagePredictionApiService): AnimalPredictionData {
        return AnimalPredictionDataImpl(apiService)
    }

    @Singleton
    @Provides
    fun providesPostRepository(postDataSource: AnimalDataInfo): AnimalRepository {
        return AnimalRepository(postDataSource)
    }

    @Singleton
    @Provides
    fun providesAnimalPredictionRepository(
        animalPredictionDataSource: AnimalPredictionData
    ): PredictionRepository {
        return PredictionRepository(animalPredictionDataSource)
    }

}

object FirebaseRef {
    val storageRef = FirebaseStorage.getInstance().reference
}