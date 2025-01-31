package com.satyamthakur.bio_guardian.data.datasource

import com.satyamthakur.bio_guardian.data.entity.MistralRequest
import com.satyamthakur.bio_guardian.data.entity.MistralResponse
import dagger.Provides
import retrofit2.Response

interface MistralData {
    suspend fun getMistralData(mistralRequest: MistralRequest): Response<MistralResponse>
}
