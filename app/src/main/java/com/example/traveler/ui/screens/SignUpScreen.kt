package com.example.traveler.ui.screens

import coil.compose.rememberAsyncImagePainter
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.traveler.ui.components.*
import com.example.traveler.viewmodel.AuthViewModel


@Composable
fun SignUpScreen(modifier: Modifier, navController: NavController, authViewModel: AuthViewModel) {
    val context = LocalContext.current
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    val pickImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri = uri
    }

    val nameState = remember { TextFieldState() }
    val emailState = remember { TextFieldState() }
    val passwordState = remember { TextFieldState() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Profile Image Picker
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .clickable { pickImageLauncher.launch("image/*") },
            contentAlignment = Alignment.Center
        ) {
            if (imageUri != null) {
                Image(
                    painter = rememberAsyncImagePainter(imageUri),
                    contentDescription = "Selected profile picture",
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Default profile icon",
                    tint = Color.Gray,
                    modifier = Modifier.size(60.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))
        Text(text = "Choose a profile picture", style = MaterialTheme.typography.labelMedium)

        Spacer(modifier = Modifier.height(20.dp))

        //MyTextField(state = nameState, placeholder = "Full Name")
        //Spacer(modifier = Modifier.height(10.dp))

        MyTextField(state = emailState, placeholder = "Email")
        Spacer(modifier = Modifier.height(10.dp))

        MyTextField(state = passwordState, placeholder = "Password")
        Spacer(modifier = Modifier.height(10.dp))

        GoogleButton { /* Google signup logic */ }
        Spacer(modifier = Modifier.height(10.dp))

        FaceBookButton { /* Facebook signup logic */ }
        Spacer(modifier = Modifier.height(30.dp))

        MyButton(text = "Sign Up") {
            // Send name, email, password, and imageUri to backend
        }
    }
}
