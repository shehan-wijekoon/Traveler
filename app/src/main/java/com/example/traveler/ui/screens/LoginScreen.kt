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


@Composable
fun LoginScreen(modifier: Modifier, navController: NavController, authViewModel: AuthViewModel) {
    //val usernameState = remember { TextFieldState() }
    val gmailState = remember { TextFieldState() }
    val passwordState = remember { TextFieldState() }

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
            // Google login logic
        }

        Spacer(modifier = Modifier.height(10.dp))

        FaceBookButton {
            //face book login logics
        }

        Spacer(modifier = Modifier.height(30.dp))

        MyButton(text = "Login") {
            // Perform login
        }

        TextButton(onClick = { navController.navigate("signup")}) { Text(text = "don't have any account, signup here")}
    }
}
