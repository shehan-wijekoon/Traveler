package com.example.traveler.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.traveler.R // R file containing your drawable resource IDs
import com.example.traveler.controllers.Screen
import com.example.traveler.ui.theme.TravelerTheme
import kotlinx.coroutines.delay

@Composable
fun LoadingScreen(
    modifier: Modifier = Modifier,
    navController: NavController
) {
    LaunchedEffect(Unit) {
        // You can use a real task here, like checking user authentication status
        delay(2000) // Delay for 2 seconds (2000 milliseconds)

        // Navigate to the next screen after the delay
        // Example: if (isUserLoggedIn) navController.navigate(Screen.Home.route) else navController.navigate(Screen.SignUp.route)
        navController.navigate(Screen.SignUp.route) {
            // This pops the LoadingScreen from the back stack
            popUpTo(Screen.Loading.route) { inclusive = true }
        }
    }

    // A Box composable to center the content
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo), // Replace with your logo resource ID
            contentDescription = "App Logo",
            modifier = Modifier.size(500.dp) // Adjust size as needed, a larger size looks better without other elements
        )
    }
}

@Preview(showBackground = true)
@Composable
fun LoadingScreenPreview() {
    TravelerTheme {
        LoadingScreen(navController = NavController(androidx.compose.ui.platform.LocalContext.current))
    }
}