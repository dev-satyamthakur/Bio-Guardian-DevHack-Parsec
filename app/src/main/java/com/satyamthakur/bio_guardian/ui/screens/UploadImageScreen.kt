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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.core.content.FileProvider
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import com.google.cloud.storage.BlobId
import com.google.cloud.storage.BlobInfo
import android.Manifest
import android.content.Intent
import com.satyamthakur.bio_guardian.ui.navigation.Endpoints
import com.satyamthakur.bio_guardian.ui.theme.Montserrat
import com.satyamthakur.bio_guardian.ui.theme.Roboto
import com.satyamthakur.bio_guardian.ui.theme.md_theme_light_background
import com.satyamthakur.bio_guardian.utils.getStorageCredentials
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.net.URLEncoder
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

sealed class ImageSelectionOption {
    object Camera : ImageSelectionOption()
    object Gallery : ImageSelectionOption()
}

@Composable
fun ImagePickerDialog(
    onDismiss: () -> Unit,
    onOptionSelected: (ImageSelectionOption) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Choose an option") },
        text = { Text("How would you like to select an image?") },
        confirmButton = {
            TextButton(onClick = { onOptionSelected(ImageSelectionOption.Camera) }) {
                Text("Camera")
            }
        },
        dismissButton = {
            TextButton(onClick = { onOptionSelected(ImageSelectionOption.Gallery) }) {
                Text("Gallery")
            }
        }
    )
}

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
            val inputStream = context.contentResolver.openInputStream(imageUri)
            if (inputStream == null) {
                Log.e("GCS Upload", "Failed to open InputStream from Uri")
                return@withContext
            }

            val storage = getStorageCredentials(context).service

            val blobId = BlobId.of(bucketName.trim(), "images/$imageName.jpg")
            val blobInfo = BlobInfo.newBuilder(blobId)
                .setContentType("image/jpeg")
                .setContentDisposition("inline")
                .build()

            inputStream.use { stream ->
                storage.create(blobInfo, stream.readBytes())
                Log.d("GCS Upload", "Image uploaded successfully")
            }
        } catch (e: Exception) {
            Log.e("GCS Upload", "Upload failed", e)
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun SelectAnImageCardWithHeading(navController: NavController) {
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val coroutineScope = rememberCoroutineScope()

    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var isImageUploading by remember { mutableStateOf(false) }
    var showImagePickerDialog by remember { mutableStateOf(false) }

    // For temporary camera image storage
    val tempImageUri = remember {
        mutableStateOf<Uri?>(null)
    }

    // Create a temporary file for camera image
    fun createImageFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val imageFileName = "JPEG_${timeStamp}_"
        return File.createTempFile(
            imageFileName,
            ".jpg",
            context.cacheDir
        )
    }

    // Gallery launcher
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { imageUri = it }
    }

    // Camera launcher
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            tempImageUri.value?.let {
                imageUri = it
            }
        }
    }

    // Permission launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            try {
                val photoFile = createImageFile()
                photoFile?.let { file ->
                    tempImageUri.value = FileProvider.getUriForFile(
                        context,
                        "${context.packageName}.bio_guardian_fileprovider",
                        file
                    )
                    // Create camera intent with back camera specification
                    tempImageUri.value?.let { uri ->
                        // We need to wrap our launcher to specify camera facing
                        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
                            putExtra(MediaStore.EXTRA_OUTPUT, uri)
                            // This is where we specify the back camera
                            putExtra("android.intent.extras.CAMERA_FACING", android.hardware.Camera.CameraInfo.CAMERA_FACING_BACK)
                            // For newer Android versions
                            putExtra("android.intent.extras.LENS_FACING_FRONT", 0)
                            putExtra("android.intent.extra.USE_FRONT_CAMERA", false)
                        }
                        cameraLauncher.launch(uri)
                    }
                }
            } catch (e: Exception) {
                Log.e("Camera", "Error launching camera", e)
                val errorMessage = "Could not launch camera"
            }
        } else {
            val errorMessage = "Camera permission is required to take photos"
        }
    }

    // Handle option selection
    fun handleOptionSelection(option: ImageSelectionOption) {
        when (option) {
            ImageSelectionOption.Camera -> {
                permissionLauncher.launch(Manifest.permission.CAMERA)
            }
            ImageSelectionOption.Gallery -> {
                galleryLauncher.launch("image/*")
            }
        }
        showImagePickerDialog = false
    }

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
            onClick = { showImagePickerDialog = true }
        ) {
            // Rest of your card content remains the same
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

        // Show dialog when needed
        if (showImagePickerDialog) {
            ImagePickerDialog(
                onDismiss = { showImagePickerDialog = false },
                onOptionSelected = { option -> handleOptionSelection(option) }
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        if (isImageUploading) {
            CircularProgressIndicator(
                modifier = Modifier
                    .width(40.dp)
                    .align(Alignment.CenterHorizontally)
            )
        }

        if (imageUri != null) {
            Button(
                onClick = {
                    isImageUploading = true
                    coroutineScope.launch {
                        try {
                            val bucketName = "bio-guardian-image-bucket".trim()
                            val fileName = UUID.randomUUID().toString()
                            val imageUrlIfSuccessfullyUploaded = "https://storage.googleapis.com/bio-guardian-image-bucket/images/$fileName.jpg"

                            uploadImageToGCS(context, imageUri!!, fileName, bucketName)
                            Log.d("BIOAPP", imageUrlIfSuccessfullyUploaded)

                            // Reset after successful upload
                            imageUri = null
                            isImageUploading = false

                            // Navigate to next screen
                            val imageUrl = URLEncoder.encode(imageUrlIfSuccessfullyUploaded, "UTF-8")
                            navController.navigate("${Endpoints.ANIMAL_DESC}/${imageUrl}/${"null"}")
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

