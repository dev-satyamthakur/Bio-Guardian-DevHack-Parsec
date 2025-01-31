package com.satyamthakur.bio_guardian.data.datasource

import com.satyamthakur.bio_guardian.data.api.ImagePredictionApiService
import com.satyamthakur.bio_guardian.data.entity.AnimalPredictionResponse
import retrofit2.Response
import javax.inject.Inject

class AnimalPredictionDataImpl @Inject constructor(
    private val apiService: ImagePredictionApiService
) : AnimalPredictionData {
    override suspend fun getAnimalPrediction(): Response<AnimalPredictionResponse> {
        return apiService.getAnimalPrediction()
    }
}