//package com.satyamthakur.bio_guardian.ui.respository
//
//import com.satyamthakur.bio_guardian.data.datasource.AnimalPredictionData
//import com.satyamthakur.bio_guardian.data.entity.AnimalPredictionResponse
//import com.satyamthakur.bio_guardian.data.entity.ResourceState
//import kotlinx.coroutines.flow.Flow
//import kotlinx.coroutines.flow.catch
//import kotlinx.coroutines.flow.flow
//import javax.inject.Inject
//
//class PredictionRepository @Inject constructor(
//    private val animalPredictionDataSource: AnimalPredictionData
//){
//    suspend fun getPost(): Flow<ResourceState<AnimalPredictionResponse>> {
//        return flow {
//            emit(ResourceState.Loading())
//
//            val response = animalPredictionDataSource.getAnimalPrediction()
//            if (response.isSuccessful && response.body() != null) {
//                emit(ResourceState.Success(response.body()!!))
//            } else {
//                emit(ResourceState.Error("Something went wrong in fetching news!"))
//            }
//
//        }.catch { e ->
//            emit(ResourceState.Error("Error: ${e.localizedMessage ?: "Something went wrong in flow"}"))
//        }
//    }
//}