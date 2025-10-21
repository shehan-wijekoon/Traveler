package com.example.traveler.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
// NOTE: LaunchedEffect is already imported in your provided code
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.traveler.R
import com.example.traveler.ui.components.HeaderBar
import com.example.traveler.ui.components.ContentCard
import com.example.traveler.viewmodel.UserProfileViewModel
import com.example.traveler.controllers.Screen
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import com.example.traveler.viewmodel.ProfileUiState
import coil.compose.rememberAsyncImagePainter
import com.example.traveler.ui.components.BottomNavigationBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserProfileScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    userProfileViewModel: UserProfileViewModel
) {
    // 🎯 FIX: Call fetchUserProfile every time this composable enters the composition.
    // This forces the ViewModel to refresh the profile AND the posts list.
    LaunchedEffect(Unit) {
        userProfileViewModel.fetchUserProfile()
    }

    val profileUiState by userProfileViewModel.profileUiState.collectAsState()
    val userPosts by userProfileViewModel.userPosts.collectAsState()

    Scaffold(
        topBar = {
            HeaderBar(
                onBackClick = { navController.popBackStack() },
                onMenuClick = { /* Handle menu click */ }
            )
        },
        bottomBar = {
            BottomNavigationBar(navController = navController)
        }
    ) { paddingValues ->

        when (val state = profileUiState) {
            is ProfileUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            is ProfileUiState.Success -> {
                val userProfile = state.userProfile

                Column(
                    modifier = modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // --- Profile Picture Section ---
                    val profilePainter = if (userProfile.profilePictureUrl.isNullOrBlank()) {
                        // Use a placeholder if URL is null or blank
                        painterResource(id = R.drawable.ic_profile_placeholder)
                    } else {
                        // Use Coil to load the image from the URL
                        rememberAsyncImagePainter(model = userProfile.profilePictureUrl)
                    }

                    Image(
                        painter = profilePainter,
                        contentDescription = "Profile Picture",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .shadow(4.dp, CircleShape)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = userProfile.name,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "@${userProfile.username}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // --- Stats Section ---
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 32.dp),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        StatColumn("Posts", userPosts.size.toString()) // Dynamically update post count
                        StatColumn("Followers", "567")
                        StatColumn("Following", "124")
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // --- Posts Grid Section ---
                    Text(
                        text = "My Posts",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp, bottom = 8.dp),
                        textAlign = TextAlign.Start
                    )

                    if (userPosts.isEmpty()) {
                        Text(
                            text = "No posts yet. Time to explore!",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(16.dp)
                        )
                    } else {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            // Set a reasonable max height for the grid to avoid parent scroll issues
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 1.dp, max = 600.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            items(userPosts) { post ->
                                ContentCard(
                                    imageUrl = post.imageUrl,
                                    // You might want to display username here instead of authorId
                                    // If your Post model doesn't include username, you'll see the UID
                                    author = post.authorId,
                                    rating = post.rating.toString(),
                                    onClick = {
                                        navController.navigate(Screen.Content.createRoute("post_id_placeholder"))
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // --- Error State: Display error message ---
            is ProfileUiState.Error -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = state.message,
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(32.dp)
                    )
                }
            }

            // --- Idle State: Display placeholder or initial loading hint ---
            is ProfileUiState.Idle -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = "Awaiting profile data...", color = MaterialTheme.colorScheme.onBackground)
                }
            }
        }
    }
}

@Composable
fun StatColumn(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}