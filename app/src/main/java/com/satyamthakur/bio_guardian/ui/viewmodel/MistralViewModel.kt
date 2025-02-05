package com.satyamthakur.bio_guardian.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.satyamthakur.bio_guardian.data.entity.MistralRequest
import com.satyamthakur.bio_guardian.data.entity.MistralResponse
import com.satyamthakur.bio_guardian.data.entity.Message
import com.satyamthakur.bio_guardian.data.entity.Content
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

    fun fetchAnimalInfo(imageUrl: String, animalName: String = "null") {
        _loading.value = true
        _error.value = null

        val mistralRequest: MistralRequest = if (animalName != "null") {
            Log.d("BIOAPP", "Fetching details for animal: $animalName")
            MistralRequest(
                messages = listOf(
                    Message(
                        content = listOf(
                            Content(
                                type = "text",
                                text = """
                                    Provide details of the $animalName in the following JSON format:

                                    {
                                      "Species": "...",
                                      "Scientific Name": "...",
                                      "Habitat": "...",
                                      "Diet": "...",
                                      "Lifespan": "...",
                                      "Size & Weight": "...",
                                      "Reproduction": "...",
                                      "Behavior": "...",
                                      "Conservation Status": "...",
                                      "Special Adaptations": "..."
                                    }

                                    Ensure the response is a valid JSON object with no extra characters or formatting. Do not include any additional text or explanations.
                                """.trimIndent()
                            )
                        )
                    )
                )
            )
        } else {
            Log.d("BIOAPP", "Fetching details based on image")
            MistralRequest(
                messages = listOf(
                    Message(
                        content = listOf(
                            Content(
                                type = "text",
                                text = """
                                    Identify the animal in the image. If no animal is detected (including humans), return a JSON response with all fields set to "not found". If an animal is detected, provide its details in the following JSON format:

                                    {
                                      "Species": "...",
                                      "Scientific Name": "...",
                                      "Habitat": "...",
                                      "Diet": "...",
                                      "Lifespan": "...",
                                      "Size & Weight": "...",
                                      "Reproduction": "...",
                                      "Behavior": "...",
                                      "Conservation Status": "...",
                                      "Special Adaptations": "..."
                                    }

                                    Ensure the response is a valid JSON object with no extra characters or formatting. Do not include any additional text or explanations.
                                """.trimIndent()
                            ),
                            Content(
                                type = "image_url",
                                image_url = imageUrl
                            )
                        )
                    )
                )
            )
        }

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
