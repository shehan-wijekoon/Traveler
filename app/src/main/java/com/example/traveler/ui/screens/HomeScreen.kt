package com.example.traveler.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.traveler.viewmodel.AuthViewModel
import com.example.traveler.viewmodel.HomeViewModel
import com.example.traveler.viewmodel.HomeUiState
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
    authViewModel: AuthViewModel,
    // 🎯 CRITICAL FIX: Added the HomeViewModel parameter
    homeViewModel: HomeViewModel
) {
    // 🎯 OBSERVE: Collect the state of the posts from the ViewModel
    val uiState by homeViewModel.uiState.collectAsState()

    val categories = listOf("Jungle", "Beach", "Forest", "Mountain", "Waterfall", "Desert")

    Scaffold(
        topBar = {
            MainHeader(
                title = "Home",
                onSearchClick = { /* TODO: Handle search click */ },
                // You may want to navigate to Screen.UserProfile.route here instead of profile_setup
                onProfileClick = { navController.navigate(Screen.UserProfile.route) },
                onNotificationsClick = {
                    // TODO: Handle notifications click
                },
                onGuideClick = {
                    navController.navigate(Screen.TravelersGuide.route)
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
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            CategoryBar(
                categories = categories,
                onCategorySelected = { selectedCategory ->
                    // TODO: Implement filtering/re-fetching logic
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 🎯 NEW: Display Posts based on the UI State
            when (uiState) {
                is HomeUiState.Loading -> {
                    // Show a spinner while data is being fetched
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                is HomeUiState.Error -> {
                    // Show an error message
                    Text("Error loading feed: ${(uiState as HomeUiState.Error).message}")
                }
                is HomeUiState.Success -> {
                    val posts = (uiState as HomeUiState.Success).posts

                    if (posts.isEmpty()) {
                        Text("No posts yet. Be the first to upload one!")
                    } else {
                        // 🎯 Use LazyColumn to efficiently list the fetched posts
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(posts) { post ->
                                // ⚠️ IMPORTANT: You need to ensure your ContentCard can handle the 'post' object
                                // which contains imageUrl (String), not imageResId (Int).
                                ContentCard(
                                    imageUrl = post.imageUrl, // Pass the URL string
                                    author = post.authorId, // Display author ID (can be improved later)
                                    rating = post.rating.toString(), // Convert rating to string
                                    onClick = {
                                        // Navigate to the content screen and pass a post ID (you need a post ID field in your Post model)
                                        // For now, let's use a dummy ID until you update the Post model to include one.
                                        navController.navigate(Screen.Content.createRoute("dummy_post_id"))
                                    }
                                )
                            }
                        }
                    }
                }
                else -> { /* HomeUiState.Idle or unexpected state */ }
            }
        }
    }
}