package com.satyamthakur.bio_guardian.data.datasource

import com.satyamthakur.bio_guardian.data.entity.AnimalPredictionResponse
import retrofit2.Response

interface AnimalPredictionData {
    suspend fun getAnimalPrediction(): Response<AnimalPredictionResponse>
}