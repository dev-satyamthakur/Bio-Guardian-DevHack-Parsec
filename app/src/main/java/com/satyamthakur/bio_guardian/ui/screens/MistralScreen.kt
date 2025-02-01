import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import com.google.gson.Gson
import com.satyamthakur.bio_guardian.ui.screens.AnimalNotFoundScreen
import com.satyamthakur.bio_guardian.ui.viewmodel.MistralViewModel

fun parseAnimalDetails(rawResponse: String): AnimalDetails? {
    return try {
        // Clean up the response
        val cleanedJson = rawResponse
            .replace("```json", "")
            .replace("```", "")
            .trimIndent()
            .trim()

        // Parse with Gson
        Gson().fromJson(cleanedJson, AnimalDetails::class.java)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

@Composable
fun MistralScreen(imageUrl: String, animalName: String, navController: NavController) {
    val viewModel: MistralViewModel = hiltViewModel()
    val mistralResponse by viewModel.mistralResponse.collectAsState()
    val errorMessage by viewModel.error.collectAsState()
    val isLoading by viewModel.loading.collectAsState()

    // Fetch animal info when imageUrl changes or screen appears
    LaunchedEffect(imageUrl, animalName) {
        viewModel.fetchAnimalInfo(imageUrl, animalName)
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        when {
            isLoading -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally, // Center horizontally
                    verticalArrangement = Arrangement.Center // Center vertically
                ) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Fetching Details...")
                }

            }
            mistralResponse != null -> {
                Log.d("BIOAPP RESPONSE", "${mistralResponse?.choices?.firstOrNull()?.message?.content ?: "No response"}")
//                Text("Response: ${mistralResponse?.choices?.firstOrNull()?.message?.content ?: "No response"}")

                val rawResponse = mistralResponse?.choices?.firstOrNull()?.message?.content ?: ""
                val animalDetails = parseAnimalDetails(rawResponse)

                Log.d("BIOAPP", animalDetails.toString())

                if (animalDetails != null && animalDetails.species == "not found") {
                    Log.d("BIOAPP", "ANIMAL NOT FOUND")
                    AnimalNotFoundScreen(navController)
                } else {
                    val sampleData = AnimalDetails(
                        species = "Giant Panda",
                        scientificName = "Ailuropoda melanoleuca",
                        habitat = "Mountain forests in central China",
                        diet = "Herbivorous, primarily bamboo",
                        lifespan = "20-30 years in wild",
                        sizeWeight = "1.5m long, 80-140 kg",
                        reproduction = "3-5 month gestation",
                        behavior = "Solitary animals",
                        conservationStatus = "Vulnerable",
                        specialAdaptations = "Pseudo-thumbs for bamboo"
                    )


                        AnimalDetailGrid(
                            animalDetails = animalDetails ?: sampleData,
                            imageUrl = imageUrl
                            // Add horizontal padding
                        )


                }
            }
            errorMessage != null -> Text("Error: $errorMessage", color = MaterialTheme.colorScheme.error)
        }
    }
}
