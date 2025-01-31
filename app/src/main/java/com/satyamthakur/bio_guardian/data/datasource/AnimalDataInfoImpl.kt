package com.satyamthakur.bio_guardian.data.datasource

import com.satyamthakur.bio_guardian.data.api.NinjaApiService
import com.satyamthakur.bio_guardian.data.entity.AnimalInfoResponse
import retrofit2.Response
import javax.inject.Inject


class AnimalDataInfoImpl @Inject constructor(
    private val apiService: NinjaApiService
) : AnimalDataInfo {
    override suspend fun getAnimalData(): Response<AnimalInfoResponse> {
        return apiService.getAnimalInfo("Song Sparrow")
    }
}