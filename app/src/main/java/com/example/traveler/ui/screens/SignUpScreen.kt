package com.example.traveler.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.traveler.ui.components.FaceBookButton
import com.example.traveler.ui.components.GoogleButton
import com.example.traveler.ui.components.MyButton
import com.example.traveler.ui.components.MyTextField
import com.example.traveler.ui.components.TextFieldState
import com.example.traveler.viewmodel.AuthUiState
import com.example.traveler.viewmodel.AuthViewModel


@Composable
fun SignUpScreen(modifier: Modifier, navController: NavController, authViewModel: AuthViewModel) {
    val context = LocalContext.current

    val authUiState by authViewModel.authUiState.collectAsState()

    val emailState = remember { TextFieldState() }
    val passwordState = remember { TextFieldState() }


    LaunchedEffect(key1 = authUiState) {
        when (authUiState) {
            is AuthUiState.Success -> {

                navController.navigate("profile_setup") {
                    popUpTo("signup") { inclusive = true }
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

        MyTextField(state = emailState, placeholder = "Email")
        Spacer(modifier = Modifier.height(10.dp))

        MyTextField(state = passwordState, placeholder = "Password")
        Spacer(modifier = Modifier.height(10.dp))

        GoogleButton { /* Google signup logic */ }
        Spacer(modifier = Modifier.height(10.dp))

        FaceBookButton { /* Facebook signup logic */ }
        Spacer(modifier = Modifier.height(30.dp))

        MyButton(
            text = "Sign Up",
            isLoading = authUiState == AuthUiState.Loading,
            onClick = {
                authViewModel.signUp(emailState.text, passwordState.text)
            }
        )
    }
}
