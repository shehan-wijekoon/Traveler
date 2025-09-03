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
import com.example.traveler.ui.components.CategoryBar
import com.example.traveler.controllers.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    authViewModel: AuthViewModel
) {
    val categories = listOf("Jungle", "Beach", "Forest", "Mountain", "Waterfall", "Desert")
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
            CategoryBar(
                categories = categories,
                onCategorySelected = { selectedCategory ->
                    // TODO: Implement logic to filter the content based on the selected category
                    // For example, you can call a ViewModel function here to get new data
                }
            )


            ContentCard(
                imageResId = R.drawable.jungle_image,
                author = "John Doe",
                rating = "4.5",
                onClick = {
                    // Navigate to the content screen and pass a post ID
                    navController.navigate(Screen.Content.createRoute("post_id_1"))
                }
            )

            ContentCard(
                imageResId = R.drawable.jungle_image,
                author = "Jane Smith",
                rating = "5.0",
                onClick = {
                    // Navigate to the content screen and pass a different post ID
                    navController.navigate(Screen.Content.createRoute("post_id_2"))
                }
            )

        }
    }
}