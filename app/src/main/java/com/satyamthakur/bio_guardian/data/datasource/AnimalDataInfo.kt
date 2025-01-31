package com.satyamthakur.bio_guardian.data.datasource

import com.satyamthakur.bio_guardian.data.entity.AnimalInfoResponse
import retrofit2.Response

interface AnimalDataInfo {

    suspend fun getAnimalData(): Response<AnimalInfoResponse>

}