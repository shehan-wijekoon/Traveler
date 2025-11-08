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
import com.example.traveler.R
import com.example.traveler.controllers.Screen
import com.example.traveler.ui.theme.TravelerTheme
import kotlinx.coroutines.delay

@Composable
fun LoadingScreen(
    modifier: Modifier = Modifier,
    navController: NavController
) {
    LaunchedEffect(Unit) {
        delay(2000)

        navController.navigate(Screen.Login.route) {
            popUpTo(Screen.Loading.route) { inclusive = true }
        }
    }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "App Logo",
            modifier = Modifier.size(500.dp)
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