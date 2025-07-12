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
import com.example.traveler.R
import com.example.traveler.ui.components.MyButton
import com.example.traveler.ui.components.MyTextField
import com.example.traveler.ui.components.GoogleButton

@Composable
fun LoginScreen() {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

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

        MyTextField(value = username, onValueChange = { username = it }, placeholder = "Username")
        Spacer(modifier = Modifier.height(10.dp))
        MyTextField(value = password, onValueChange = { password = it }, placeholder = "Password")
        Spacer(modifier = Modifier.height(10.dp))

        GoogleButton {
            // Google login logic
        }

        Spacer(modifier = Modifier.height(30.dp))

        MyButton(text = "Loging") {
            // Perform login
        }
    }
}
