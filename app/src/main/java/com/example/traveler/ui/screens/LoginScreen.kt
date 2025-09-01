package com.example.traveler.ui.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.traveler.R
import com.example.traveler.controllers.Screen // ⚠️ ADD THIS IMPORT
import com.example.traveler.ui.components.FaceBookButton
import com.example.traveler.ui.components.MyButton
import com.example.traveler.ui.components.MyTextField
import com.example.traveler.ui.components.TextFieldState
import com.example.traveler.viewmodel.AuthUiState
import com.example.traveler.viewmodel.AuthViewModel
import com.example.traveler.ui.components.GoogleButton

@Composable
fun LoginScreen(modifier: Modifier, navController: NavController, authViewModel: AuthViewModel) {
    val authUiState by authViewModel.authUiState.collectAsState()
    val context = LocalContext.current

    val gmailState = remember { TextFieldState() }
    val passwordState = remember { TextFieldState() }

    LaunchedEffect(key1 = authUiState) {
        when (authUiState) {
            is AuthUiState.Success -> {
                // Navigate to the correct route
                navController.navigate(Screen.Home.route) { // ⚠️ CORRECTED ROUTE
                    popUpTo(Screen.Login.route) { inclusive = true } // ⚠️ CORRECTED POP-UP ROUTE
                }
                authViewModel.resetAuthUiState()
            }
            is AuthUiState.Error -> {
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
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = null,
            modifier = Modifier
                .size(240.dp)
                .clip(CircleShape)
        )

        Spacer(modifier = Modifier.height(10.dp))
        Text(text = "Welcome Back", fontWeight = FontWeight.Light)
        Spacer(modifier = Modifier.height(20.dp))

        MyTextField(state = gmailState, placeholder = "gmail")
        Spacer(modifier = Modifier.height(10.dp))
        MyTextField(state = passwordState, placeholder = "Password")
        Spacer(modifier = Modifier.height(10.dp))
        Spacer(modifier = Modifier.height(30.dp))

        MyButton(
            text = "Login",
            isLoading = authUiState == AuthUiState.Loading,
            onClick = {
                authViewModel.login(gmailState.text, passwordState.text)
            }
        )

        TextButton(onClick = { navController.navigate(Screen.SignUp.route)}) { // ⚠️ CORRECTED ROUTE
            Text(text = "don't have any account, signup here")
        }
    }
}