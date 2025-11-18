package com.example.traveler.ui.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
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
    val isSaveCompleted by userProfileViewModel.isSaveCompleted.collectAsState()

    val scrollState = rememberScrollState()

    var name by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var profilePictureUrl by remember { mutableStateOf("") }


    LaunchedEffect(uiState) {
        if (uiState is ProfileUiState.Success) {
            val profile = (uiState as ProfileUiState.Success).userProfile
            name = profile.title
            username = profile.username
            description = profile.description
            profilePictureUrl = profile.profilePictureUrl
        }
        if (uiState is ProfileUiState.Error) {
            Toast.makeText(context, "Error: ${(uiState as ProfileUiState.Error).message}", Toast.LENGTH_LONG).show()
        }
    }


    LaunchedEffect(isSaveCompleted) {
        if (isSaveCompleted) {
            Toast.makeText(context, "Account saved successfully!", Toast.LENGTH_SHORT).show()
            navController.navigate(Screen.Home.route) {
                popUpTo(navController.graph.startDestinationId) { inclusive = true }
            }
            userProfileViewModel.resetSaveCompleted()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Account Creation Setup",
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
                .padding(horizontal = 16.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))


            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(Color.Gray),
                contentAlignment = Alignment.Center
            ) {

                if (profilePictureUrl.isNotBlank()) {
                    Image(
                        painter = rememberAsyncImagePainter(model = profilePictureUrl),
                        contentDescription = "Promotional Image",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_profile_placeholder),
                        contentDescription = "Place Image Placeholder",
                        tint = Color.White,
                        modifier = Modifier.size(60.dp)
                    )
                }
            }
            Text(
                text = "Paste main promotional image URL",
                modifier = Modifier.padding(top = 8.dp),
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(16.dp))


            OutlinedTextField(
                value = profilePictureUrl,
                onValueChange = { profilePictureUrl = it },
                label = { Text("Profile Picture URL") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next, keyboardType = KeyboardType.Uri)
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Place/Destination Name") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next)
            )
            Spacer(modifier = Modifier.height(16.dp))


            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Promoter Username/Alias") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next)
            )
            Spacer(modifier = Modifier.height(16.dp))


            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Detailed Description of Place") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                singleLine = false
            )
            Spacer(modifier = Modifier.height(32.dp))


            Button(
                onClick = {

                    if (name.isBlank() || username.isBlank() || profilePictureUrl.isBlank()) {
                        Toast.makeText(context, "Name, Username, and Profile Picture URL are required.", Toast.LENGTH_SHORT).show()
                        return@Button
                    }


                    // CORRECTED: Calling the simplified saveUserProfile with only 4 arguments
                    userProfileViewModel.saveUserProfile(
                        name = name,
                        username = username,
                        description = description,
                        profilePictureUrl = profilePictureUrl
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                enabled = uiState !is ProfileUiState.Loading
            ) {
                if (uiState is ProfileUiState.Loading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text(text = "Save Account", fontSize = 16.sp)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}