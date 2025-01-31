package com.satyamthakur.bio_guardian.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.satyamthakur.bio_guardian.data.entity.MistralRequest
import com.satyamthakur.bio_guardian.data.entity.MistralResponse
import com.satyamthakur.bio_guardian.data.repository.MistralRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MistralViewModel @Inject constructor(
    private val repository: MistralRepository
) : ViewModel() {

    private val _mistralResponse = MutableStateFlow<MistralResponse?>(null)
    val mistralResponse: StateFlow<MistralResponse?> = _mistralResponse

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    fun fetchAnimalInfo(imageUrl: String) {
        _loading.value = true
        _error.value = null

        val mistralRequest = MistralRequest(
            messages = listOf(
                com.satyamthakur.bio_guardian.data.entity.Message(
                    content = listOf(
                        com.satyamthakur.bio_guardian.data.entity.Content(type = "text", text = "Identify the type of animal in the image: If it's a human being, then consider Homo sapiens. \n" +
                                "Give me the details of the animal in the following format:\n" +
                                "\n" +
                                "{\n" +
                                "  \"Species\": \"...\",\n" +
                                "  \"Scientific Name\": \"...\",\n" +
                                "  \"Habitat\": \"...\",\n" +
                                "  \"Diet\": \"...\",\n" +
                                "  \"Lifespan\": \"...\",\n" +
                                "  \"Size & Weight\": \"...\",\n" +
                                "  \"Reproduction\": \"...\",\n" +
                                "  \"Behavior\": \"...\",\n" +
                                "  \"Conservation Status\": \"...\",\n" +
                                "  \"Special Adaptations\": \"...\"\n" +
                                "}\n" +
                                "\n" +
                                "Ensure the response is a **valid JSON object** with no extra characters, formatting, or markdown (like triple backticks). \n" +
                                "Do NOT include any additional text or explanations."),
                        com.satyamthakur.bio_guardian.data.entity.Content(type = "image_url", image_url = imageUrl)
                    )
                )
            )
        )

        viewModelScope.launch {
            try {
                val response = repository.getAnimalInfoFromImage(mistralRequest)
                if (response.isSuccessful) {
                    _mistralResponse.value = response.body()
                } else {
                    _error.value = "Error: ${response.errorBody()?.string()}"
                }
            } catch (e: Exception) {
                _error.value = "Exception: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }
}
