package com.example.traveler.ui.screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import coil.compose.rememberAsyncImagePainter
import com.example.traveler.R
import com.example.traveler.controllers.Screen
import com.example.traveler.viewmodel.UserProfileViewModel
import com.example.traveler.viewmodel.ProfileUiState // ⚠️ Corrected import
//import com.example.traveler.viewmodel.UserProfileUiState // ⚠️ Kept for now, but should be removed later

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileSetupScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    userProfileViewModel: UserProfileViewModel
) {
    val context = LocalContext.current
    // ⚠️ Correctly collect the single state flow from the ViewModel
    val uiState by userProfileViewModel.profileUiState.collectAsState()

    var name by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri = uri
    }

    LaunchedEffect(uiState) {
        when (uiState) {
            is ProfileUiState.Success -> {
                Toast.makeText(context, "Profile saved successfully!", Toast.LENGTH_SHORT).show()

                navController.navigate(Screen.Home.route) {
                    popUpTo(Screen.ProfileSetup.route) { inclusive = true }
                }
                userProfileViewModel.resetUiState()
            }
            is ProfileUiState.Error -> {
                val errorMessage = (uiState as ProfileUiState.Error).message
                Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                userProfileViewModel.resetUiState()
            }
            else -> { /* Do nothing */ }
        }
    }

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
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(Color.Gray)
                    .clickable { imagePickerLauncher.launch("image/*") },
                contentAlignment = Alignment.Center
            ) {
                if (imageUri != null) {
                    Image(
                        painter = rememberAsyncImagePainter(model = imageUri),
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
                text = "Choose a profile picture",
                modifier = Modifier.padding(top = 8.dp),
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(32.dp))

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
                    userProfileViewModel.saveUserProfile(name, username, description, imageUri)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                // ⚠️ Correctly check the state against the new sealed class
                enabled = uiState !is ProfileUiState.Loading
            ) {
                // ⚠️ Correctly check the state against the new sealed class
                if (uiState is ProfileUiState.Loading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text(text = "Save Profile", fontSize = 16.sp)
                }
            }
        }
    }
}