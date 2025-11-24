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
    homeViewModel: HomeViewModel
) {
    val uiState by homeViewModel.uiState.collectAsState()

    val categories = listOf("All", "Jungle", "Beach", "Forest", "Mountain", "Waterfall", "Desert")

    Scaffold(
        topBar = {
            MainHeader(
                title = "Home",
                onSearchClick = { /* TODO: Handle search click */ },
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
                    val filterValue = if (selectedCategory == "All") null else selectedCategory
                    homeViewModel.selectCategory(filterValue)
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            when (uiState) {
                is HomeUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                is HomeUiState.Error -> {
                    Text("Error loading feed: ${(uiState as HomeUiState.Error).message}")
                }
                is HomeUiState.Success -> {
                    val posts = (uiState as HomeUiState.Success).posts

                    if (posts.isEmpty()) {
                        Text("No posts yet. Be the first to upload one!")
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(posts) { post ->
                                val imageUrl = post.imageUrls.firstOrNull() ?: ""

                                ContentCard(
                                    imageUrl = imageUrl,
                                    author = post.title,
                                    onClick = {
                                        navController.navigate(Screen.Content.createRoute(post.id))
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