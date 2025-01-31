package com.satyamthakur.bio_guardian.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.satyamthakur.bio_guardian.ui.viewmodel.MistralViewModel

@Composable
fun MistralScreen(viewModel: MistralViewModel = viewModel()) {
    var imageUrl by remember { mutableStateOf("https://storage.googleapis.com/bio-guardian-image-bucket/images/ab613058-154b-41d3-83db-aadcd979cb38.jpg") }
    val mistralResponse by viewModel.mistralResponse.collectAsState()
    val errorMessage by viewModel.error.collectAsState()
    val isLoading by viewModel.loading.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        OutlinedTextField(
            value = imageUrl,
            onValueChange = { imageUrl = it },
            label = { Text("Enter Image URL") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { viewModel.fetchAnimalInfo(imageUrl) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Get Animal Info")
        }

        Spacer(modifier = Modifier.height(16.dp))

        when {
            isLoading -> CircularProgressIndicator()
            mistralResponse != null -> Text("Response: ${mistralResponse?.choices?.firstOrNull()?.message?.content ?: "No response"}")
            errorMessage != null -> Text("Error: $errorMessage", color = MaterialTheme.colorScheme.error)
        }
    }
}
