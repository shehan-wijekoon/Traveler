package com.example.traveler.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.traveler.viewmodel.AuthViewModel
import com.example.traveler.ui.components.MainHeader
import com.example.traveler.ui.components.ContentCard
import com.example.traveler.R
import com.example.traveler.ui.components.BottomNavigationBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    authViewModel: AuthViewModel
) {
    Scaffold(
        topBar = {
            MainHeader(
                title = "Home",
                onSearchClick = { /* TODO: Handle search click */ },
                onProfileClick = {

                    navController.navigate("profile_setup")
                },
                onNotificationsClick = {
                    // TODO: Handle notifications click
                }
            )
        },
        containerColor = MaterialTheme.colorScheme.surface,
                bottomBar = {
            BottomNavigationBar(navController = navController)
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top // Changed to Top to display cards from the top
        ) {
            Text(
                text = "Discover your next adventure!",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            // ⚠️ Use your custom ContentCard here with specific data
            ContentCard(
                imageResId = R.drawable.jungle_image, // Provide a drawable resource ID
                author = "John Doe",
                rating = "4.5"
            )

            // You can add more ContentCards with different data
            ContentCard(
                imageResId = R.drawable.jungle_image,
                author = "Jane Smith",
                rating = "5.0"
            )

        }
    }
}