package com.satyamthakur.bio_guardian.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.satyamthakur.bio_guardian.data.entity.AnimalInfoResponse
import com.satyamthakur.bio_guardian.data.entity.FakeApiResponse
import com.satyamthakur.bio_guardian.data.entity.ResourceState
import com.satyamthakur.bio_guardian.ui.respository.AnimalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AnimalViewModel @Inject constructor(
    private val animalRepository: AnimalRepository
) : ViewModel() {

    private val _post : MutableStateFlow<ResourceState<AnimalInfoResponse>> = MutableStateFlow(ResourceState.Loading())
    val post: StateFlow<ResourceState<AnimalInfoResponse>> = _post

    init {
        getPost()
    }

    private fun getPost() {
        viewModelScope.launch(Dispatchers.IO) {
            animalRepository.getPost()
                .collectLatest { animalResponse ->
                    _post.value = animalResponse
                }
        }
    }
}