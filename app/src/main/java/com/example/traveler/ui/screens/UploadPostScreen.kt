package com.example.traveler.ui.screens

import android.net.Uri
import android.widget.Toast
// ⚠️ REMOVED: import androidx.activity.compose.rememberLauncherForActivityResult // Not needed
// ⚠️ REMOVED: import androidx.activity.result.contract.ActivityResultContracts // Not needed
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.traveler.ui.components.HeaderBar
import com.example.traveler.viewmodel.UploadPostUiState
import com.example.traveler.viewmodel.UploadPostViewModel
import com.example.traveler.controllers.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UploadPostScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    uploadPostViewModel: UploadPostViewModel
) {
    val context = LocalContext.current
    val uiState by uploadPostViewModel.uiState.collectAsState()

    // 🎯 CHANGE 1: Switched imageUri (Uri?) to imageUrl (String)
    var imageUrl by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    // ⚠️ REMOVED: imagePickerLauncher definition

    // Observe the UI state for feedback and navigation
    LaunchedEffect(uiState) {
        when (uiState) {
            is UploadPostUiState.Success -> {
                Toast.makeText(context, "Post uploaded successfully!", Toast.LENGTH_SHORT).show()
                navController.navigate(Screen.Home.route) {
                    // This prevents navigating back to the incomplete post screen
                    popUpTo(Screen.Home.route) { inclusive = true }
                }
                uploadPostViewModel.resetUiState()
            }
            is UploadPostUiState.Error -> {
                val errorMessage = (uiState as UploadPostUiState.Error).message
                Toast.makeText(context, "Error: $errorMessage", Toast.LENGTH_SHORT).show()
                uploadPostViewModel.resetUiState()
            }
            else -> {}
        }
    }

    Scaffold(
        topBar = {
            HeaderBar(
                onBackClick = { navController.popBackStack() },
                onMenuClick = { /* Do nothing for this screen */ }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Create a New Post",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // 🎯 CHANGE 2: Image preview now loads from the imageUrl state
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.LightGray)
                    .border(2.dp, Color.DarkGray, RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                // Load image preview only if a non-blank URL is provided
                if (imageUrl.isNotBlank()) {
                    Image(
                        painter = rememberAsyncImagePainter(model = imageUrl),
                        contentDescription = "Selected image preview",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Select Image",
                            modifier = Modifier.size(48.dp),
                            tint = Color.DarkGray
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Paste a public image URL below",
                            fontSize = 16.sp,
                            color = Color.DarkGray
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 🎯 CHANGE 3: New input field for the Image URL
            OutlinedTextField(
                value = imageUrl,
                onValueChange = { imageUrl = it },
                label = { Text("Image URL") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Description input field (remains the same)
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Write a description...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                singleLine = false
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Upload button
            Button(
                onClick = {
                    // 🎯 CHANGE 4: Call uploadPost with the imageUrl string
                    if (imageUrl.isNotBlank() && description.isNotBlank()) {
                        uploadPostViewModel.uploadPost(imageUrl, description)
                    } else {
                        Toast.makeText(context, "Please enter a valid URL and add a description.", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                enabled = uiState !is UploadPostUiState.Loading && imageUrl.isNotBlank() && description.isNotBlank()
            ) {
                if (uiState is UploadPostUiState.Loading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text(text = "Upload Post", fontSize = 16.sp)
                }
            }
        }
    }
}