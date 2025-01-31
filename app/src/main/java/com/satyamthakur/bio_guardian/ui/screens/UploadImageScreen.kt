package com.satyamthakur.bio_guardian.ui.screens

import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PhotoCameraBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import com.google.cloud.storage.BlobId
import com.google.cloud.storage.BlobInfo
import com.satyamthakur.bio_guardian.ui.navigation.Endpoints
import com.satyamthakur.bio_guardian.ui.theme.Montserrat
import com.satyamthakur.bio_guardian.ui.theme.Roboto
import com.satyamthakur.bio_guardian.ui.theme.md_theme_light_background
import com.satyamthakur.bio_guardian.utils.getStorageCredentials
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.UUID

@Composable
fun UploadImageScreen(paddingValues: PaddingValues, navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(md_theme_light_background)
            .padding(paddingValues)

    ) {
        Spacer(modifier = Modifier.height(10.dp))
        UploadImageScreenTitle()
        Spacer(modifier = Modifier.height(20.dp))
        SelectAnImageCardWithHeading(navController)
    }
}

@Composable
fun UploadImageScreenTitle() {
    Text(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        text = "Upload",
        fontFamily = Montserrat,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
    )
}


suspend fun uploadImageToGCS(
    context: Context,
    imageUri: Uri,
    imageName: String,
    bucketName: String
) {
    withContext(Dispatchers.IO) {
        try {
            val file = File(getPathFromUri(context, imageUri))

            val storage = getStorageCredentials(context).service

            val blobId = BlobId.of(bucketName.trim(), "images/$imageName.jpg")
            val blobInfo = BlobInfo.newBuilder(blobId)
                .setContentType("image/jpeg") // Set MIME type
                .setContentDisposition("inline") // Allow inline viewing
                .build()

            file.inputStream().use { inputStream ->
                storage.create(blobInfo, inputStream.readBytes())
                Log.d("GCS Upload", "Image uploaded successfully")
            }
        } catch (e: Exception) {
            Log.e("GCS Upload", "Upload failed", e)
        }
    }
}

// Helper function to get file path from Uri
fun getPathFromUri(context: Context, uri: Uri): String {
    val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
    val cursor = context.contentResolver.query(uri, filePathColumn, null, null, null)
    cursor?.use {
        it.moveToFirst()
        val columnIndex = it.getColumnIndex(filePathColumn[0])
        return it.getString(columnIndex)
    }
    throw IllegalArgumentException("Cannot find file path for URI")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectAnImageCardWithHeading(navController: NavController) {

    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val coroutineScope = rememberCoroutineScope()

    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var isImageUploading by remember { mutableStateOf(false) }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            uri?.let {
                imageUri = it
            }
        }
    )


    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = "Select an image",
            fontFamily = Roboto,
            fontSize = 16.sp
        )
        Spacer(modifier = Modifier.height(10.dp))

        Card(
            modifier = Modifier
                .width(screenWidth)
                .height(screenWidth - 16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            border = BorderStroke(2.dp, Color.Black),
            onClick = { galleryLauncher.launch("image/*") }
        ) {
            if (imageUri == null) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.PhotoCameraBack,
                        contentDescription = null,
                        modifier = Modifier.size(32.dp)
                    )
                }
            } else {
                Image(
                    painter = rememberAsyncImagePainter(model = imageUri),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        if (isImageUploading) {
            CircularProgressIndicator(
                modifier = Modifier
                    .width(40.dp)
                    .align(Alignment.CenterHorizontally)
            )
        }

//        if (imageUri != null) {
//            Button(onClick = {
//
//                isImageUploading = true
//
//                val animalImage =
//                    FirebaseRef.storageRef.child("images/${imageUri!!.lastPathSegment}")
//                var uploadTask = animalImage.putFile(imageUri!!)
//
//                imageUri = null // set imageUri to null again while uploading image
//
//                // Register observers to listen for when the download is done or if it fails
//                uploadTask.addOnFailureListener {
//                    Log.d("BIOAPP", "Failed to upload")
//                }.addOnSuccessListener { taskSnapshot ->
//                    // taskSnapshot.metadata contains file metadata such as size, content-type, etc.
//                    // ...
//                    Log.d("BIOAPP", "Successfully uploaded")
//                    isImageUploading = false
//                    animalImage.getDownloadUrl().addOnSuccessListener { uri ->
//                        val imageUrl: String =
//                            uri.toString() // getting uploaded image url as link
//                        Log.d("BIOAPP", imageUrl)
//                    }
//                    navController.navigate(Endpoints.ANIMAL_DESC)
//                }
//
//            }, modifier = Modifier.fillMaxWidth()) {
//                Text(text = "Upload Now")
//            }
//        }

        if (imageUri != null) {
            Button(
                onClick = {
                    isImageUploading = true
                    coroutineScope.launch {
                        try {
                            val bucketName = "bio-guardian-image-bucket".trim()
                            val fileName = UUID.randomUUID().toString()
                            val imageUrlIfSuccess = "https://storage.googleapis.com/bio-guardian-image-bucket/images/$fileName.jpg"

                            uploadImageToGCS(context, imageUri!!, fileName, bucketName)
                            Log.d("BIOAPP", imageUrlIfSuccess)

                            // Reset after successful upload
                            imageUri = null
                            isImageUploading = false

                            // Navigate to next screen
                            navController.navigate(Endpoints.ANIMAL_DESC)
                        } catch (e: Exception) {
                            Log.e("BIOAPP", "Upload process error", e)
                            isImageUploading = false
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Upload Now")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun UploadImagePrev() {
    UploadImageScreen(paddingValues = PaddingValues(), rememberNavController())
}

