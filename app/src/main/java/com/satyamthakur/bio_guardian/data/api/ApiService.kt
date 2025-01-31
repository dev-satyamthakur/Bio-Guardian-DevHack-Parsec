package com.satyamthakur.bio_guardian.data.api

import com.satyamthakur.bio_guardian.data.entity.AnimalInfoResponse
import com.satyamthakur.bio_guardian.data.entity.AnimalPredictionResponse
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Header
import retrofit2.http.Query

interface NinjaApiService {
    @GET("animals")
    suspend fun getAnimalInfo(
        @Query("name") animalName: String,
        @Header("x-api-key") xApiKey: String = "API_KEY_HERE"
    ): Response<AnimalInfoResponse>
}

interface ImagePredictionApiService {
    @GET("2")
    suspend fun getAnimalPrediction(
        @Query("api_key") apiKey: String = "API_KEY_HERE",
        @Query("image") imageUrl: String = "https://www.istockphoto.com/photo/song-sparrow-perched-on-a-wood-gm1368317578-438329228"
    ): Response<AnimalPredictionResponse>
}