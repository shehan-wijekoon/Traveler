package com.example.traveler.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavController
import com.example.traveler.R
import com.example.traveler.ui.components.FaceBookButton
import com.example.traveler.ui.components.MyButton
import com.example.traveler.ui.components.MyTextField
import com.example.traveler.ui.components.GoogleButton
import com.example.traveler.ui.components.TextFieldState
import com.example.traveler.viewmodel.AuthViewModel
import com.example.traveler.viewmodel.AuthUiState // **ADDED**
import android.widget.Toast // **ADDED**
import androidx.compose.ui.platform.LocalContext // **ADDED**

@Composable
fun LoginScreen(modifier: Modifier, navController: NavController, authViewModel: AuthViewModel) {
    // **ADDED**: Collect the authentication UI state from the ViewModel
    val authUiState by authViewModel.authUiState.collectAsState()
    val context = LocalContext.current

    //val usernameState = remember { TextFieldState() }
    val gmailState = remember { TextFieldState() }
    val passwordState = remember { TextFieldState() }

    // **ADDED**: LaunchedEffect to observe the authUiState and react to changes
    LaunchedEffect(key1 = authUiState) {
        when (authUiState) {
            is AuthUiState.Success -> {
                // Navigate to the main screen on successful login
                navController.navigate("home_screen") {
                    // This prevents the user from going back to the login screen
                    popUpTo("login") { inclusive = true }
                }
                authViewModel.resetAuthUiState()
            }
            is AuthUiState.Error -> {
                // Show a toast message with the error
                val errorMessage = (authUiState as AuthUiState.Error).message
                Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
                authViewModel.resetAuthUiState()
            }
            else -> {}
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Profile Image
        Image(
            painter = painterResource(id = R.drawable.profile_pic),
            contentDescription = null,
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(text = "@John Wick", fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(20.dp))

        //MyTextField(state = usernameState, placeholder = "Username")
        MyTextField(state = gmailState, placeholder = "gmail")
        Spacer(modifier = Modifier.height(10.dp))
        MyTextField(state = passwordState, placeholder = "Password")
        Spacer(modifier = Modifier.height(10.dp))

        GoogleButton {
            // **ADDED**: Call the ViewModel function for Google login
            authViewModel.googleLogin()
        }

        Spacer(modifier = Modifier.height(10.dp))

        FaceBookButton {
            // **ADDED**: Call the ViewModel function for Facebook login
            authViewModel.facebookLogin()
        }

        Spacer(modifier = Modifier.height(30.dp))

        // **FIXED**: The onClick lambda is now a separate, named parameter
        MyButton(
            text = "Login",
            isLoading = authUiState == AuthUiState.Loading,
            onClick = {
                authViewModel.login(gmailState.text, passwordState.text)
            }
        )

        // **FIXED**: The TextButton is now a separate component, outside of MyButton's block
        TextButton(onClick = { navController.navigate("signup")}) { Text(text = "don't have any account, signup here")}
    }
}