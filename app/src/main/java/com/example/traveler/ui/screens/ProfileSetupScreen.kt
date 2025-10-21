package com.example.traveler.ui.screens

import android.net.Uri
import android.widget.Toast
// ... (imports remain mostly the same, removed Uri-specific ones)
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter // Used to load the external URL
import com.example.traveler.R
import com.example.traveler.controllers.Screen
import com.example.traveler.viewmodel.UserProfileViewModel
import com.example.traveler.viewmodel.ProfileUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileSetupScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    userProfileViewModel: UserProfileViewModel
) {
    val context = LocalContext.current
    val uiState by userProfileViewModel.profileUiState.collectAsState()

    var name by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    // ⚠️ CHANGE 1: Change imageUri (Uri?) to imageUrl (String?)
    var imageUrl by remember { mutableStateOf("") }

    // ⚠️ CHANGE 2: Removed imagePickerLauncher as we no longer select local files.

    // ... LaunchedEffect block remains the same ...

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "User Profile Setup",
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
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

            // ⚠️ CHANGE 3: Update the Box/Image logic to use the URL string
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(Color.Gray),
                contentAlignment = Alignment.Center
            ) {
                // If a URL is entered, try to display it. Otherwise, show the placeholder.
                if (imageUrl.isNotBlank()) {
                    Image(
                        // Load the image directly from the URL string
                        painter = rememberAsyncImagePainter(model = imageUrl),
                        contentDescription = "Profile Picture",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_profile_placeholder),
                        contentDescription = "Select Profile Picture",
                        tint = Color.White,
                        modifier = Modifier.size(60.dp)
                    )
                }
            }
            Text(
                text = "Paste a public image URL below", // ⚠️ Updated guidance text
                modifier = Modifier.padding(top = 8.dp),
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(16.dp)) // Reduced space to add the URL field

            // ⚠️ CHANGE 4: Add the OutlinedTextField for the image URL
            OutlinedTextField(
                value = imageUrl,
                onValueChange = { imageUrl = it },
                label = { Text("Profile Picture URL") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next)
            )
            Spacer(modifier = Modifier.height(16.dp))

            // ... (name, username, description fields remain the same) ...
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Name") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next)
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Username") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next)
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                singleLine = false
            )
            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    // ⚠️ CHANGE 5: Pass the imageUrl string instead of imageUri
                    userProfileViewModel.saveUserProfile(name, username, description, imageUrl)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                enabled = uiState !is ProfileUiState.Loading
            ) {

                if (uiState is ProfileUiState.Loading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text(text = "Save Profile", fontSize = 16.sp)
                }
            }
        }
    }
}